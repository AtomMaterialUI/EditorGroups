/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package krasa.editorGroups.settings.regex.columns

import com.intellij.icons.AllIcons
import com.intellij.ide.setToolTipText
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.ColumnInfo
import krasa.editorGroups.messages.EditorGroupsBundle.message
import krasa.editorGroups.model.RegexGroupModel
import java.awt.Component
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

/** Column info for extension origin badge. */
class ExtensionColumnInfo : ColumnInfo<RegexGroupModel, Icon?>(message("RegexEditorConfigurable.columns.extension")) {
  private val renderer = object : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
      table: JTable?,
      value: Any?,
      isSelected: Boolean,
      hasFocus: Boolean,
      row: Int,
      column: Int
    ): Component {
      val label = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column) as JLabel
      label.horizontalAlignment = CENTER

      when (value) {
        is Icon -> {
          label.icon = value
          label.setToolTipText(HtmlChunk.text(message("RegexEditorConfigurable.extension.tooltip")))
        }

        else    -> {
          label.icon = null
          label.toolTipText = null
        }
      }
      return label
    }
  }

  override fun getColumnClass(): Class<Icon> = Icon::class.java

  override fun getName(): String = ""

  override fun getWidth(table: JTable?): Int = JBUIScale.scale(24)

  override fun valueOf(item: RegexGroupModel): Icon? {
    if (!item.isFromExtension) return null
    return item.icon ?: AllIcons.Nodes.Plugin
  }

  override fun getRenderer(item: RegexGroupModel?): TableCellRenderer = renderer
}
