# Editor Groups

Add an additional tab panel for files belonging to the same logic group.

This is an implementation of <https://github.com/SomewhatCloudy/phpstorm-related-proposal>

[![Build Status](https://travis-ci.org/krasa/EditorGroups.svg?branch=master)](https://travis-ci.org/krasa/EditorGroups)
[![GitHub release](https://img.shields.io/github/release/krasa/EditorGroups.svg)](

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/15290-editor-groups.svg)](https://plugins.jetbrains.com/plugin/15290-editor-groups)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/d/15290-editor-groups.svg)](https://plugins.jetbrains.com/plugin/15290-editor-groups)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/r/rating/15290-editor-groups.svg)](https://plugins.jetbrains.com/plugin/15290-editor-groups)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/r/stars/15290-editor-groups.svg)](https://plugins.jetbrains.com/plugin/15290-editor-groups)

## Features

### Group by File Name

Displays all files with the same name (without extension) in the project.

![Group by File Name](docs/assets/sameFile.png)

### Group by Folder

Displays all files in the same folder.

![Group by Folder](docs/assets/currentFolder.png)

### Group by Bookmarked Files

Show all files and line bookmarks from your bookmark groups.

![Group by Bookmarked Files](docs/assets/bookmarks.png)

### Group by Regex (Advanced)

Define regex groups and group all files matching the regex.

#### Predefined Regex Groups

The plugin comes with preset regex groups for popular frameworks and patterns, each with its own icon displayed in the settings table, the editor tab panel, and the group switcher:

- **Spring** — Spring Boot components
- **NestJS** — NestJS controllers, services, modules
- **Vue** — Vue components, views, stores
- **Rails** — Ruby on Rails models, controllers, views
- **Django** — Django models, views, templates
- **Laravel** — Laravel controllers, models, migrations
- **Go** — Go structs and interfaces
- **Rust** — Rust modules and tests
- **HTML** — Web components
- **Tests** — Source files with their test counterparts

You can enable or disable each preset, adjust its regex pattern, scope, and other settings in the Regex Groups table.

#### Setting Icons

Each regex group can have its own icon.

**Picking an icon**: In the Regex Groups settings table, click the icon column for any group to browse and select an SVG file from your disk. Clear the field to fall back to the default icon.

**Extension point**: Plugin developers contributing predefined groups via the `krasa.editorGroups.regexGroup` extension point can set an icon using the `icon` attribute:

```xml
<regexGroup id="my.group"
            name="My Group"
            regex=".*"
            icon="/icons/my-icon.svg"/>
```

The `icon` value is a classpath resource path (starting with `/`) or an absolute disk path to an SVG file. Icons are clamped to 16×16 to preserve layout.

### Group Switcher

Quickly switch between groups using the group switcher.

![Group Switcher](docs/assets/switchEditorGroup.png)

### Switch current opened file

You can switch the current opened file with another file from the same group

- From the tab panel
- From the inline popup at the top right

![Switch files](docs/assets/switchFile.png)

You can also open the file in a new tab, window, or split window:

- `Ctrl + Click` - open a file in a new tab
- `Shift + Click` - open a file in a new window
- `Alt + Click` - open a file in a vertical split window
- `Alt + Shift + Click` - open a file in a horizontal split window

### Tool Window

The plugin also provides a tool window to display all the groups and files.

![Tool Window](docs/assets/toolwindow.png)

### Shortcuts

- `Ctrl + Alt + Q` - Refresh
- `Ctrl + Alt + W` - Switch Group popup
- `Ctrl + Alt + Shift + W` - Switch File popup

- `Ctrl + Mouse Wheel` or `Ctrl + ; or '`  - switch file (closing the previous one)
- `Ctrl + Alt + Mouse Wheel` or `Ctrl + Alt + ; or '` - switch file (leaving the previous one opened)
- `Ctrl + Shift + Mouse Wheel` - switch file into a new window

----

### Settings

The plugin includes a vast choice of settings to customize the behavior, performance and appearance of the plugin.

You can enter the settings either by looking for `Editor Groups` in the settings search bar, or by right clicking on the panel and selecting
`Open Editor Groups Settings`.

### Global

- **Show Tabs Panel**: Whether to disable the tabs panel globally

### Behavior

#### Fallback behavior

By default, the plugin will try to match the best suited group for the current file:

- If it belongs to a bookmark group

If no match is found, it will fall back to the following cases, in this order:

* Fallback to "Regex Group": Will try to match to one of the defined _Regex Groups_
* Fallback to "Same Name" group: Will try to match all files with the same name (without extension)
* Fallback to "Current Folder" group: Will try to match all files within the same folder

If no match is found (e.g. there is only one file in this group, the current file), then it will execute the following check:

* If **Hide Panel If Empty** is enabled, the panel will be hidden
* Otherwise it will display the current file

#### Remember Last Group

When enabled, the plugin will keep in memory the last selected group for each opened file.

----

### Appearance

#### Rounded Tabs

Display the tabs in a new style, similar to the one used within the new "Islands Layout"

![Tabs Position](docs/assets/islands.png)

#### Tabs Position

You can customize the position of the tab panel (`TOP` or `BOTTOM`)

![Tabs Position](docs/assets/customFontAndPosition.png)

#### Custom Font

You can change the font of the tabs.

![Tabs Position](docs/assets/customFontAndPosition.png)

#### Display Group Size

Displays the size of the group.

![Display Group Size](docs/assets/switchEditorGroup.png)

There's also an icon that displays the size of the group in the tab panel.

![Display Group Size](docs/assets/counter.png)

#### Compact Tabs

Reduce the size of the tabs.

![Compact Tabs](docs/assets/compactTabs.png)

#### Colorize Tabs

With this setting, opened tabs belonging to the same group will be reflected in the main tab panel. This is useful to get a quick glance of
all the related opened tabs for quicker switching.

![Colorize Tabs](docs/assets/compactTabs.png)

Note: The default groups (`Same Name`, `Current Folder`, `Bookmarks`) have their own predefined color that you can customize in the _File
Colors Settings_.

![Custom File Colors](docs/assets/customScopes.png)

#### Third-Party Theme Compatibility

The tab panel colors fall back to the IDE's native `EditorTabs` colors when a third-party theme does not explicitly define
`EditorGroupsTabs.*` keys. This ensures the panel blends seamlessly with any theme, even those that use wildcard UI rules.

----

### Performance

You can also improve the performance of the plugins with the following options

### Initialize groups synchronously

By default, the plugin will match files belonging to the same group asynchronously, which results in showing a "Loading..." pane until all
files are indexed. You can disable this behavior to show the files immediately.

### Group Size Limit

The maximum number of files that can be matched within a group. If the group size exceeds this limit, the group will not be displayed.

### Tab Limit

The maximum number of tabs that can be displayed in the tab panel. If the number of tabs exceeds this limit, the exceeding tabs will not be
displayed.
