package krasa.editorGroups.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.util.text.NaturalComparator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.tabs.JBTabsEx
import com.intellij.ui.tabs.TabInfo
import krasa.editorGroups.messages.EditorGroupsBundle.message
import krasa.editorGroups.services.ExternalGroupProvider
import krasa.editorGroups.support.getNameWithoutTypeAndExt

abstract class ReorderTabsAction(
  private val textKey: String,
  private val key: (VirtualFile, ExternalGroupProvider) -> String
) : EditorGroupsAction() {
  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

  override fun update(e: AnActionEvent) {
    val window = getWindow(e)
    e.presentation.text = message(textKey)
    e.presentation.isEnabled = window != null && window.tabCount > 1
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val window = getWindow(e) ?: return
    val externalGroupProvider = ExternalGroupProvider.getInstance(project)
    val keys = mutableMapOf<String, String>()

    (window.tabbedPane.tabs as? JBTabsEx)?.sortTabs(
      compareBy<TabInfo> {
        val file = it.`object` as? VirtualFile
        if (file == null) "\uFFFF" else keys.getOrPut(file.path) {
          key(file, externalGroupProvider)
        }
      }.thenComparator { first, second ->
        NaturalComparator.INSTANCE.compare(first.text, second.text)
      }
    )
  }

  private fun getWindow(e: AnActionEvent) =
    e.project?.let { FileEditorManagerEx.getInstanceEx(it).currentWindow }
}

class ReorderTabsByParentFolderAction : ReorderTabsAction(
  textKey = "action.krasa.editorGroups.ReorderTabsByParentFolder.text",
  key = { file, _ -> file.parent?.path ?: "" }
) {
  companion object {
    const val ID: String = "krasa.editorGroups.ReorderTabsByParentFolder"
  }
}

class ReorderTabsByFilenameAction : ReorderTabsAction(
  textKey = "action.krasa.editorGroups.ReorderTabsByFilename.text",
  key = { file, _ -> file.nameWithoutExtension }
) {
  companion object {
    const val ID: String = "krasa.editorGroups.ReorderTabsByFilename"
  }
}

class ReorderTabsByLogicalNameAction : ReorderTabsAction(
  textKey = "action.krasa.editorGroups.ReorderTabsByLogicalName.text",
  key = { file, _ -> getNameWithoutTypeAndExt(file.name).toString() }
) {
  companion object {
    const val ID: String = "krasa.editorGroups.ReorderTabsByLogicalName"
  }
}

class ReorderTabsByBookmarksAction : ReorderTabsAction(
  textKey = "action.krasa.editorGroups.ReorderTabsByBookmarks.text",
  key = { file, provider ->
    provider.findGroups(file)
      .map { it.name }
      .sortedWith(NaturalComparator.INSTANCE)
      .joinToString("\u0000")
      .ifEmpty { "\uFFFF" }
  }
) {
  companion object {
    const val ID: String = "krasa.editorGroups.ReorderTabsByBookmarks"
  }
}
