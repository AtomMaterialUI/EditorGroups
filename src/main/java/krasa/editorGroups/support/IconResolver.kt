package krasa.editorGroups.support

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import krasa.editorGroups.model.RegexGroupModel
import javax.swing.Icon

/**
 * Resolves an icon from a string path.
 *
 * Supports two formats:
 * 1. `AllIcons.*` — resolves a built-in IntelliJ platform icon by its
 *    fully qualified field path, e.g. `AllIcons.Actions.Regex` or
 *    `AllIcons.Nodes.Folder`.
 * 2. `/icons/...` — resolves a custom icon resource bundled in the plugin,
 *    e.g. `/icons/spring.svg`.
 *
 * Returns `null` if the path is blank or resolution fails.
 */
object IconResolver {

  @JvmStatic
  fun resolve(iconPath: String?): Icon? {
    if (iconPath.isNullOrBlank()) return null

    return when {
      iconPath.startsWith("AllIcons.") -> resolveAllIcons(iconPath)
      else                             -> resolveResourceIcon(iconPath)
    }
  }

  private fun resolveAllIcons(path: String): Icon? {
    // Strip the "AllIcons." prefix to get the nested class + field path, e.g. "Actions.Regex"
    val fieldPath = path.removePrefix("AllIcons.")
    if (fieldPath.isBlank()) return null

    return try {
      // AllIcons uses nested classes like AllIcons.Actions.Regex
      val parts = fieldPath.split('.')
      var clazz: Class<*> = AllIcons::class.java

      // Navigate through nested classes (e.g. Actions, Nodes, General, etc.)
      for (i in 0 until parts.size - 1) {
        clazz = clazz.getDeclaredField(parts[i]).type
      }

      // Get the final field (the actual Icon)
      val fieldName = parts[parts.size - 1]
      val field = clazz.getDeclaredField(fieldName)
      field.get(null) as? Icon
    } catch (e: Exception) {
      null
    }
  }

  private fun resolveResourceIcon(path: String): Icon? {
    return try {
      // If the path doesn't start with '/', add it
      val resourcePath = when {
        path.startsWith('/') -> path
        else                 -> "/$path"
      }
      IconLoader.getIcon(resourcePath, RegexGroupModel::class.java)
    } catch (e: Exception) {
      null
    }
  }
}
