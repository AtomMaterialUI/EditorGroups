package krasa.editorGroups.settings.regex.columns

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import krasa.editorGroups.messages.EditorGroupsBundle.message
import krasa.editorGroups.model.RegexGroupModel
import java.awt.Component
import java.io.File
import javax.swing.AbstractCellEditor
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Editable column to pick an SVG icon for a regex group. The value is the
 * icon path.
 */
class IconEditableColumnInfo : EditableColumnInfo<RegexGroupModel, String>(message("RegexEditorConfigurable.columns.icon")) {
  override fun valueOf(item: RegexGroupModel): String = item.myIconPath

  override fun setValue(item: RegexGroupModel, value: String) {
    item.myIconPath = value.trim()
    item.touched = true
  }

  override fun isCellEditable(item: RegexGroupModel): Boolean = !item.isFromExtension

  override fun getWidth(table: JTable?): Int = JBUIScale.scale(WIDTH)

  override fun getRenderer(item: RegexGroupModel): TableCellRenderer = object : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
      table: JTable?,
      value: Any?,
      isSelected: Boolean,
      hasFocus: Boolean,
      row: Int,
      column: Int,
    ): Component {
      val path = value as? String ?: ""
      val label = super.getTableCellRendererComponent(
        /* table = */ table,
        /* value = */ File(path).name,
        /* isSelected = */ isSelected,
        /* hasFocus = */ hasFocus,
        /* row = */ row,
        /* column = */ column
      ) as JLabel
      label.icon = item.icon
      label.toolTipText = path.takeIf { it.isNotEmpty() }
      return label
    }
  }

  override fun getEditor(item: RegexGroupModel): TableCellEditor {
    val field = TextFieldWithBrowseButton()
    val descriptor = FileChooserDescriptor(
      /* chooseFiles = */ true,
      /* chooseFolders = */ false,
      /* chooseJars = */ false,
      /* chooseJarsAsFiles = */ false,
      /* chooseJarContents = */ false,
      /* chooseMultiple = */ false
    )
      .withExtensionFilter("svg", "svg")
      .withTitle(message("RegexEditorConfigurable.icon.chooser.title"))
      .withDescription(message("RegexEditorConfigurable.icon.chooser.description"))

    field.addActionListener {
      FileChooser.chooseFile(descriptor, null, null) { file -> field.text = file.path }
    }

    return object : AbstractCellEditor(), TableCellEditor {
      override fun getCellEditorValue(): Any = field.text

      override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        field.text = value as? String ?: ""
        return field
      }
    }
  }

  companion object {
    private const val WIDTH = 120
  }
}
