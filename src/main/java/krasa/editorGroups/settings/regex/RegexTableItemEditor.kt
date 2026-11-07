package krasa.editorGroups.settings.regex

import com.intellij.util.Function
import com.intellij.util.ui.CollectionItemEditor
import com.intellij.util.ui.table.TableModelEditor.DialogItemEditor
import krasa.editorGroups.model.RegexGroupModel

/** Editor for the [RegexGroupModel] table cells. */
class RegexTableItemEditor : DialogItemEditor<RegexGroupModel>, CollectionItemEditor<RegexGroupModel> {
  /**
   * Apply changes to the edited item.
   *
   * @param oldItem the item to modify
   * @param newItem the changes
   */
  override fun applyEdited(oldItem: RegexGroupModel, newItem: RegexGroupModel): Unit = oldItem.apply(newItem)

  /**
   * Duplicate an item.
   *
   * @param item the [RegexGroupModel]
   * @param forInPlaceEditing if editing in place (true)
   * @return a clone of the association
   */
  override fun clone(item: RegexGroupModel, forInPlaceEditing: Boolean): RegexGroupModel {
    val regexModel = RegexGroupModel()

    with(regexModel) {
      name = item.name
      regex = item.regex
      scope = item.scope
      notComparingGroups = item.notComparingGroups
      isEnabled = item.isEnabled
      touched = item.touched
      priority = item.priority
      color = item.color
      isFromExtension = item.isFromExtension
      extensionId = item.extensionId
      description = item.description
    }

    return regexModel
  }

  /**
   * Edits an item.
   *
   * @param item the [RegexGroupModel]
   * @param mutator a function to mutate the item
   * @param isAdd if in add mode
   */
  override fun edit(item: RegexGroupModel, mutator: Function<in RegexGroupModel, out RegexGroupModel>, isAdd: Boolean) {
    val settings = clone(item, true)
    mutator.`fun`(item).apply(settings)
  }

  /** Class of the items. */
  override fun getItemClass(): Class<out RegexGroupModel> = RegexGroupModel::class.java

  /**
   * Do not allow editing empty items.
   *
   * @param item the [RegexGroupModel]
   * @return true if editable
   */
  override fun isEditable(item: RegexGroupModel): Boolean = !item.isEmpty

  /**
   * Determines what constitutes an empty [RegexGroupModel].
   *
   * @param item the [RegexGroupModel]
   * @return true if empty
   */
  override fun isEmpty(item: RegexGroupModel): Boolean = item.isEmpty
}
