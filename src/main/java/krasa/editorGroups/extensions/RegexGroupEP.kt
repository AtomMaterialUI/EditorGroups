package krasa.editorGroups.extensions

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginAware
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.util.xmlb.annotations.Attribute
import krasa.editorGroups.model.RegexGroupModel
import krasa.editorGroups.model.Scope
import krasa.editorGroups.support.fromHex
import krasa.editorGroups.support.generateColor

class RegexGroupEP : PluginAware {
  private var pluginDescriptor: PluginDescriptor? = null

  @Attribute("id")
  var id: String? = null

  @Attribute("name")
  var name: String? = null

  @Attribute("regex")
  var regex: String? = null

  @Attribute("ignoredGroups")
  var ignoredGroups: String? = null

  @Attribute("scope")
  var scope: Scope = Scope.CURRENT_FOLDER

  @Attribute("priority")
  var priority: Int = 0

  @Attribute("color")
  var color: String? = null

  @Attribute("enabled")
  var enabled: Boolean = true

  override fun setPluginDescriptor(pluginDescriptor: PluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor
  }

  fun toModel(): RegexGroupModel {
    val groupName = name ?: id ?: "RegexGroup"
    val groupRegex = regex ?: ".*"
    val groupNotComparing = ignoredGroups ?: ""
    val groupColor = color?.fromHex() ?: generateColor(groupName)

    return RegexGroupModel.from(
      name = groupName,
      isEnabled = enabled,
      regex = groupRegex,
      scope = scope,
      notComparingGroups = groupNotComparing,
      color = groupColor,
      priority = priority,
      isFromExtension = true,
      extensionId = id ?: name ?: groupName,
    )
  }

  companion object {
    @JvmField
    val EP_NAME: ExtensionPointName<RegexGroupEP> =
      ExtensionPointName.create("krasa.editorGroups.regexGroup")
  }
}
