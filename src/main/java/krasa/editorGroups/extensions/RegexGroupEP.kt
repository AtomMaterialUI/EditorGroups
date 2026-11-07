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

  override fun setPluginDescriptor(pluginDescriptor: PluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor
  }

  fun buildRegexAndIgnoredGroups(): Pair<String, String> {
    if (!regex.isNullOrBlank()) {
      return Pair(regex!!, ignoredGroups ?: "")
    }

    val typeList = types?.split(",", ";")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    val extList = extensions?.split(",", ";")?.map { it.trim().removePrefix(".") }?.filter { it.isNotEmpty() } ?: emptyList()

    if (typeList.isEmpty() && extList.isEmpty() && template.isNullOrBlank()) {
      return Pair(".*", ignoredGroups ?: "")
    }

    val effectiveTemplate = when {
      !template.isNullOrBlank() -> template!!
      typeList.isNotEmpty() && extList.isNotEmpty() -> "{name}{type}.{ext}"
      extList.isNotEmpty() -> "{name}.{ext}"
      else -> "{name}{type}"
    }

    val sortedTypes = typeList.sortedByDescending { it.length }
    val typesPattern = if (sortedTypes.isNotEmpty()) {
      val joined = sortedTypes.joinToString("|") { Regex.escape(it) }
      if (optionalType) "($joined)?" else "($joined)"
    } else {
      ""
    }

    val sortedExts = extList.sortedByDescending { it.length }
    val extsPattern = if (sortedExts.isNotEmpty()) {
      val joined = sortedExts.joinToString("|") { Regex.escape(it) }
      "($joined)"
    } else {
      ""
    }

    val tokenName = "\u0001"
    val tokenType = "\u0002"
    val tokenExt = "\u0003"

    val hasName = effectiveTemplate.contains("{name}")
    val hasType = effectiveTemplate.contains("{type}")
    val hasExt = effectiveTemplate.contains("{ext}")

    val raw = effectiveTemplate
      .replace("{name}", tokenName)
      .replace("{type}", tokenType)
      .replace("{ext}", tokenExt)

    val escapedBuilder = StringBuilder()
    for (ch in raw) {
      when (ch) {
        '\u0001', '\u0002', '\u0003' -> escapedBuilder.append(ch)
        '.', '^', '$', '*', '+', '?', '(', ')', '[', ']', '{', '}', '|', '\\' -> escapedBuilder.append('\\').append(ch)
        else -> escapedBuilder.append(ch)
      }
    }

    val resultPattern = escapedBuilder.toString()
      .replace(tokenName, "(.*)")
      .replace(tokenType, typesPattern)
      .replace(tokenExt, extsPattern)

    val computedIgnoredGroups = if (ignoredGroups != null) {
      ignoredGroups!!
    } else {
      val ignored = mutableListOf<Int>()
      var groupIdx = 1
      if (hasName) groupIdx++
      if (hasType && sortedTypes.isNotEmpty()) {
        ignored.add(groupIdx)
        groupIdx++
      }
      if (hasExt && sortedExts.isNotEmpty()) {
        ignored.add(groupIdx)
        groupIdx++
      }
      ignored.joinToString(",")
    }

    return Pair(resultPattern, computedIgnoredGroups)
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
    )
  }

  companion object {
    @JvmField
    val EP_NAME: ExtensionPointName<RegexGroupEP> =
      ExtensionPointName.create("krasa.editorGroups.regexGroup")
  }
}
