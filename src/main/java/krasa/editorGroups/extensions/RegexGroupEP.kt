package krasa.editorGroups.extensions

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginAware
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.util.xmlb.annotations.Attribute
import krasa.editorGroups.model.RegexGroupModel
import krasa.editorGroups.model.Scope
import krasa.editorGroups.support.fromHex
import krasa.editorGroups.support.generateColor
import krasa.editorGroups.support.toValueList

class RegexGroupEP : PluginAware {
  private var pluginDescriptor: PluginDescriptor? = null

  @Attribute("id")
  var id: String? = null

  @Attribute("name")
  var name: String? = null

  @Attribute("regex")
  var regex: String? = null

  @Attribute("template")
  var template: String? = null

  @Attribute("types")
  var types: String? = null

  @Attribute("extensions")
  var extensions: String? = null

  @Attribute("optionalType")
  var optionalType: Boolean = false

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

  @Attribute("description")
  var description: String? = null

  override fun setPluginDescriptor(pluginDescriptor: PluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor
  }

  fun buildRegexAndIgnoredGroups(): Pair<String, String> {
    regex?.takeIf { it.isNotBlank() }?.let { return it to (ignoredGroups ?: "") }

    val typeList = types.toValueList()
    val extList = extensions.toValueList(removeLeadingDot = true)

    if (typeList.isEmpty() && extList.isEmpty() && template.isNullOrBlank()) {
      return ".*" to (ignoredGroups ?: "")
    }

    val effectiveTemplate = template?.takeIf { it.isNotBlank() } ?: when {
      typeList.isNotEmpty() && extList.isNotEmpty() -> "{name}{type}.{ext}"
      extList.isNotEmpty()                          -> "{name}.{ext}"
      else                                          -> "{name}{type}"
    }

    val compiled = RegexTemplateCompiler.compile(
      template = effectiveTemplate,
      types = typeList,
      extensions = extList,
      optionalType = optionalType,
      ignoredGroups = ignoredGroups,
    )

    return compiled.pattern to compiled.ignoredGroups
  }

  fun toModel(): RegexGroupModel {
    val groupName = name ?: id ?: "RegexGroup"
    val (groupRegex, groupNotComparing) = buildRegexAndIgnoredGroups()
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
      description = description,
    )
  }

  companion object {
    @JvmField
    val EP_NAME: ExtensionPointName<RegexGroupEP> =
      ExtensionPointName.create("krasa.editorGroups.regexGroup")
  }
}
