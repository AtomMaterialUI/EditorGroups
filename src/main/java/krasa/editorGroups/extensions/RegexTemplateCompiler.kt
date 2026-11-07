package krasa.editorGroups.extensions

internal data class CompiledRegexTemplate(
  val pattern: String,
  val ignoredGroups: String,
)

internal object RegexTemplateCompiler {
  private val placeholder = Regex("\\{(name|type|ext)}")

  fun compile(
    template: String,
    types: List<String>,
    extensions: List<String>,
    optionalType: Boolean,
    ignoredGroups: String? = null,
  ): CompiledRegexTemplate {
    val typePattern = regexPipe(types)
    val extensionPattern = regexPipe(extensions)
    val pattern = StringBuilder()
    val computedIgnoredGroups = mutableListOf<Int>()
    var groupIndex = 0
    var literalStart = 0

    placeholder.findAll(template).forEach { match ->
      pattern.appendLiteral(template.substring(literalStart, match.range.first))

      when (match.groupValues[1]) {
        "name" -> {
          pattern.append("(.*?)")
          groupIndex++
        }

        "type" -> if (typePattern.isNotEmpty()) {
          pattern.append('(').append(typePattern).append(')')

          if (optionalType) pattern.append('?')
          computedIgnoredGroups.add(++groupIndex)
        }

        "ext"  -> if (extensionPattern.isNotEmpty()) {
          pattern.append('(').append(extensionPattern).append(')')
          computedIgnoredGroups.add(++groupIndex)
        }
      }

      literalStart = match.range.last + 1
    }

    pattern.appendLiteral(template.substring(literalStart))

    return CompiledRegexTemplate(
      pattern = pattern.toString(),
      ignoredGroups = ignoredGroups ?: computedIgnoredGroups.joinToString(","),
    )
  }

  private fun regexPipe(values: List<String>): String = values
    .sortedByDescending { it.length }
    .joinToString("|") { Regex.escape(it) }

  private fun StringBuilder.appendLiteral(value: String) {
    if (value.isNotEmpty()) append(Regex.escape(value))
  }
}
