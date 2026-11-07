package krasa.editorGroups.tabs2.impl.themes

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import krasa.editorGroups.settings.EditorGroupsSettings
import java.awt.Color
import java.awt.Font
import javax.swing.UIManager

object EditorGroupsUI {
  val defaultTheme: EditorGroupDefaultTabTheme = EditorGroupDefaultTabTheme()
  val defaultSize: Int = JBUI.scale(14)

  fun underlineColor(): Color = JBColor.namedColor(
    "EditorGroupsTabs.underlineColor",
    defaultTheme.underlineColor
  )

  fun underlineHeight(): Int = JBUI.getInt(
    "EditorGroupsTabs.underlineHeight",
    defaultTheme.underlineHeight
  )

  fun inactiveUnderlineColor(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.inactiveUnderlineColor")
    return named ?: defaultTheme.inactiveUnderlineColor
  }

  fun borderColor(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.borderColor")
    return named ?: defaultTheme.borderColor
  }

  fun background(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.background")
    return named ?: defaultTheme.background
  }

  fun hoverBackground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.hoverBackground")
    return named ?: defaultTheme.hoverBackground
  }

  fun hoverInactiveBackground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.hoverInactiveBackground")
    return named ?: defaultTheme.hoverInactiveBackground
  }

  fun hoverSelectedBackground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.hoverSelectedBackground")
    return named ?: defaultTheme.hoverSelectedBackground
  }

  fun hoverSelectedInactiveBackground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.hoverSelectedInactiveBackground")
    return named ?: defaultTheme.hoverSelectedInactiveBackground
  }

  fun hoverBorderColor(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.hoverBorderColor")
    return named ?: defaultTheme.hoverBorderColor
  }

  fun underlinedTabBackground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.underlinedTabBackground")
    return named ?: defaultTheme.underlinedTabBackground
  }

  fun underlinedTabForeground(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.underlinedTabForeground")
    return named ?: defaultTheme.underlinedTabForeground
  }

  fun underlinedTabBorderColor(): Color {
    val named = UIManager.getColor("EditorGroupsTabs.underlinedTabBorderColor")
    return named ?: defaultTheme.underlinedTabBorderColor
  }

  fun underlineArc(): Int = JBUI.getInt(
    "EditorGroupsTabs.underlineArc",
    defaultTheme.underlineArc
  )

  fun compactTabHeight(): Int = JBUI.getInt(
    "EditorGroupsTabs.compactTabHeight",
    defaultTheme.compactTabHeight
  )

  fun tabHeight(): Int = JBUI.getInt(
    "EditorGroupsTabs.tabHeight",
    defaultTheme.tabHeight
  )

  fun fontSizeOffset(): Int = JBUI.getInt(
    "EditorGroupsTabs.fontSizeOffset",
    defaultTheme.fontSizeOffset
  )

  fun roundTabArc(): Int = JBUI.getInt(
    "EditorGroupsTabs.roundTabArc",
    defaultTheme.roundTabArc
  )

  fun backgroundOpacity(): Float = JBUI.getFloat(
    "EditorGroupsTabs.backgroundOpacity",
    defaultTheme.roundTabOpacity
  )

  fun roundTabBorderWidth(): Float = JBUI.getFloat(
    "EditorGroupsTabs.roundTabBorderWidth",
    defaultTheme.roundTabBorderWidth
  )

  fun font(): Font {
    val isCustomFont = EditorGroupsSettings.instance.isCustomFont
    val customFont = EditorGroupsSettings.instance.customFont

    val font = when {
      isCustomFont && customFont != null -> JBFont.create(Font(customFont, Font.PLAIN, defaultSize), false)
      else                               -> defaultFont()
    }

    return font
      .biggerOn(fontSizeOffset().toFloat())
  }

  private fun defaultFont(): JBFont {
    val font = UIManager.getFont("EditorGroupsTabs.font") ?: defaultTheme.font
    return JBFont.create(font, false)
  }
}
