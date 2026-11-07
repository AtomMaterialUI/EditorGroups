package krasa.editorGroups.settings.regex

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.messages.Topic
import krasa.editorGroups.extensions.RegexGroupEP
import krasa.editorGroups.model.RegexGroupModel
import krasa.editorGroups.model.RegexGroupModels
import krasa.editorGroups.settings.EditorGroupSetting
import krasa.editorGroups.settings.regex.RegexGroupsSettings.RegexGroupsSettingsState

@Service(Service.Level.APP)
@State(
  name = "RegexGroups",
  storages = [Storage(value = "EditorGroups.xml")],
  category = SettingsCategory.UI,
)
class RegexGroupsSettings : SimplePersistentStateComponent<RegexGroupsSettingsState>(RegexGroupsSettingsState()) {
  class RegexGroupsSettingsState : BaseState() {
    var regexGroupModels: RegexGroupModels by property(RegexGroupModels())
    var disabledExtensionIds: MutableSet<String> by stringSet()
  }

  @EditorGroupSetting([EditorGroupSetting.Category.REGEX, EditorGroupSetting.Category.GROUPS])
  var regexGroupModels: RegexGroupModels
    get() = state.regexGroupModels
    set(value) {
      state.regexGroupModels = value
      state.regexGroupModels.regexModels.forEach { it.touched = false }
    }

  var disabledExtensionIds: MutableSet<String>
    get() = state.disabledExtensionIds
    set(value) {
      state.disabledExtensionIds = value
    }

  fun fireChanged() {
    ApplicationManager
      .getApplication()
      ?.messageBus
      ?.syncPublisher(TOPIC)
      ?.configChanged(this)
  }

  fun loadExtensionGroupModels(): List<RegexGroupModel> {
    return try {
      RegexGroupEP.EP_NAME.extensionList.map { ep ->
        ep.toModel().apply {
          val id = extensionId ?: myName
          if (state.disabledExtensionIds.contains(id)) isEnabled = false
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun getAllRegexGroupModels(): RegexGroupModels {
    val epModels = loadExtensionGroupModels()
    val userModels = state.regexGroupModels.regexModels.filterNot { it.isFromExtension }

    return RegexGroupModels().apply {
      regexModels = (epModels + userModels).toMutableList()
    }
  }

  fun saveModels(models: List<RegexGroupModel>) {
    val (extensionModels, userModels) = models.partition { it.isFromExtension }

    state.disabledExtensionIds = extensionModels
      .filterNot { it.isEnabled }
      .mapTo(mutableSetOf()) { it.extensionId ?: it.myName }

    this.regexGroupModels = RegexGroupModels().apply { regexModels = userModels.toMutableList() }
    this.fireChanged()
  }

  fun clone(): RegexGroupsSettings {
    val clone = RegexGroupsSettings()
    clone.regexGroupModels = this.regexGroupModels
    clone.disabledExtensionIds = HashSet(this.disabledExtensionIds)
    return clone
  }

  fun apply(state: RegexGroupsSettings) {
    this.regexGroupModels = state.regexGroupModels
    this.disabledExtensionIds = state.disabledExtensionIds
    this.fireChanged()
  }

  fun reset() {
    this.regexGroupModels = RegexGroupModels()
    this.disabledExtensionIds.clear()
    this.fireChanged()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RegexGroupsSettings
    if (regexGroupModels != other.regexGroupModels) return false
    if (disabledExtensionIds != other.disabledExtensionIds) return false
    if (state != other.state) return false
    return true
  }

  override fun hashCode(): Int {
    var result = regexGroupModels.hashCode()
    result = 31 * result + disabledExtensionIds.hashCode()
    result = 31 * result + state.hashCode()
    return result
  }

  override fun toString(): String =
    """
    RegexGroupsSettings(
      regexGroupModels=$regexGroupModels,
      disabledExtensionIds=$disabledExtensionIds
    )
    """.trimMargin()

  fun isModified(models: MutableList<RegexGroupModel>): Boolean {
    val currentMerged = getAllRegexGroupModels().regexModels
    if (models.size != currentMerged.size) return true
    for (i in models.indices) {
      val m = models[i]
      val c = currentMerged[i]
      if (m.isEnabled != c.isEnabled ||
        m.myName != c.myName ||
        m.myRegex != c.myRegex ||
        m.myScope != c.myScope ||
        m.myNotComparingGroups != c.myNotComparingGroups ||
        m.priority != c.priority ||
        m.color != c.color ||
        m.isFromExtension != c.isFromExtension ||
        m.extensionId != c.extensionId
      ) {
        return true
      }
    }
    return false
  }

  interface RegexSettingsNotifier {
    /** When Config is changed (settings). */
    fun configChanged(config: RegexGroupsSettings): Unit = Unit
  }

  companion object {
    @Topic.AppLevel
    @JvmField
    val TOPIC: Topic<RegexSettingsNotifier> = Topic.create(
      "Regex Groups Settings",
      RegexSettingsNotifier::class.java,
    )

    @JvmStatic
    val instance: RegexGroupsSettings by lazy { service<RegexGroupsSettings>() }
  }
}