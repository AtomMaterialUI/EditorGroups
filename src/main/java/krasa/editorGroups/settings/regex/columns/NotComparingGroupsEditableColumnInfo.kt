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

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import krasa.editorGroups.messages.EditorGroupsBundle.message
import krasa.editorGroups.model.RegexGroupModel
import javax.swing.table.TableCellEditor

/** Editable column info for [krasa.editorGroups.model.RegexGroupModel] notComparingGroups. */
class NotComparingGroupsEditableColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<RegexGroupModel, String>(message("RegexEditorConfigurable.columns.notComparingGroups")) {
  /**
   * The value of the column is the notComparingGroups
   *
   * @param item the [RegexGroupModel]
   * @return [RegexGroupModel] notComparingGroups
   */
  override fun valueOf(item: RegexGroupModel): String = item.myNotComparingGroups

  /**
   * Set [RegexGroupModel]'s scope
   *
   * @param item the [RegexGroupModel]
   * @param value the new scope
   */
  override fun setValue(item: RegexGroupModel, value: String) {
    item.myNotComparingGroups = value
    item.touched = true
  }

  /**
   * Creates an editor for the scope, with empty value validation
   *
   * @param item the [RegexGroupModel]
   * @return the [TableCellEditor]
   */
  override fun getEditor(item: RegexGroupModel): TableCellEditor {
    val cellEditor = ExtendableTextField()
    return StatefulValidatingCellEditor(cellEditor, parent)
  }

  override fun isCellEditable(item: RegexGroupModel): Boolean = editable && !item.isFromExtension
}
