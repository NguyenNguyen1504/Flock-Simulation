package flock.UI

import scalafx.scene.control.{CheckMenuItem, Menu, MenuBar, MenuItem, RadioMenuItem, SeparatorMenuItem, ToggleGroup}

class FlockMenuBar extends MenuBar:

  // ── File menu ─────────────────────────────────────────────────────────────

  private val openItem   = new MenuItem("Open...")
  private val saveItem   = new MenuItem("Save")
  private val saveAsItem = new MenuItem("Save As...")

  private val fileMenu = new Menu("File"):
    items = Seq(openItem, saveItem, new SeparatorMenuItem, saveAsItem)

  // ── View menu ─────────────────────────────────────────────────────────────

  private val toggleHudItem = new CheckMenuItem("HUD"):
    selected = true

  private val themeToggleGroup = new ToggleGroup

  private val darkThemeItem = new RadioMenuItem("Dark"):
    toggleGroup = themeToggleGroup
    selected = true

  private val lightThemeItem = new RadioMenuItem("Light"):
    toggleGroup = themeToggleGroup

  private val themeMenu = new Menu("Theme"):
    items = Seq(darkThemeItem, lightThemeItem)

  private val viewMenu = new Menu("View"):
    items = Seq(toggleHudItem, new SeparatorMenuItem, themeMenu)

  // ── Layout ────────────────────────────────────────────────────────────────

  useSystemMenuBar = false
  styleClass += "flock-menubar"
  menus = Seq(fileMenu, viewMenu)

  // ── Callbacks ─────────────────────────────────────────────────────────────

  def onOpen(action: => Unit): Unit              = openItem.onAction   = _ => action
  def onSave(action: => Unit): Unit              = saveItem.onAction   = _ => action
  def onSaveAs(action: => Unit): Unit            = saveAsItem.onAction = _ => action
  def onToggleHud(action: Boolean => Unit): Unit =
    toggleHudItem.onAction = _ => action(toggleHudItem.selected.value)

  def onThemeChange(action: String => Unit): Unit =
    darkThemeItem.onAction  = _ => action("dark")
    lightThemeItem.onAction = _ => action("light")

end FlockMenuBar