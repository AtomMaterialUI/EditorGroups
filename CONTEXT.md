# EditorGroups

A plugin for JetBrains IDEs that groups related editor tabs together based on custom criteria, folder structure, or filename patterns.

## Language

### Regex Groups

**Regex Group**:
A dynamic editor group resolved by matching filenames against a regular expression and comparing non-ignored capture groups.
_Avoid_: Pattern group, regex tab, file matcher

**Comparing Group**:
A capture group in a regular expression whose extracted substring must match identically between the reference file and candidate files for them to be grouped together.
_Avoid_: Key group, identity group, matching group

**Ignored Group**:
A capture group in a regular expression excluded from equality checks across files, allowing varying roles, suffixes, or extensions (e.g. `Service` vs `Controller`, `.ts` vs `.html`) within the same group.
_Avoid_: Not-comparing group, skipped group, variable group

**Scope**:
The file search boundary in the project hierarchy where candidate files are discovered for a group (`CURRENT_FOLDER`, `INCLUDING_SUBFOLDERS`, `WHOLE_PROJECT`).
_Avoid_: Search depth, folder mode

**Extension Group**:
A predefined regex group declared declaratively via the `krasa.editorGroups.regexGroup` extension point in `plugin.xml`.
_Avoid_: Built-in regex, bundled rule, plugin preset

**Reference File**:
The active file in the editor used as the baseline against which capture groups of other files are evaluated.
_Avoid_: Current tab, source file, anchor file

**Candidate File**:
A file discovered within the designated scope whose filename is tested against the regular expression and reference file.
_Avoid_: Target file, matching file

**Live Tester**:
An interactive settings panel component that evaluates sample filenames against regex capture groups in real time.
_Avoid_: Regex validator, pattern tester, debugger panel
