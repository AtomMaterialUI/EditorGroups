package krasa.editorGroups.support

import com.intellij.openapi.util.IconLoader
import java.awt.Component
import java.awt.Graphics
import java.io.File
import javax.swing.Icon

/** Loads an SVG icon from an absolute disk path or from the classpath (`/icons/x.svg`), clamped to 16x16. */
object IconPathLoader {
  fun load(path: String?, classLoader: ClassLoader? = null): Icon? {
    if (path.isNullOrBlank()) return null
    if (!path.lowercase().endsWith(".svg")) return null

    return runCatching {
      val file = File(path)
      val icon = if (file.isFile) {
        IconLoader.findIcon(file.toURI().toURL())
      } else {
        IconLoader.findIcon(path, classLoader ?: javaClass.classLoader)
      }
      icon?.let { clampTo16(it) } ?: null
    }.getOrNull()
  }

  private fun clampTo16(icon: Icon): Icon = object : Icon {
    override fun getIconWidth() = 16
    override fun getIconHeight() = 16
    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
      icon.paintIcon(c, g, x, y)
    }
  }
}
