package flock.UI

import scalafx.scene.control.{CheckMenuItem, Menu, MenuBar, MenuItem, RadioMenuItem, SeparatorMenuItem, ToggleGroup}

/** The application menu bar containing File and View menus.
 *
 *  Interaction is exposed as callback-registration methods (`onXxx`) so that [[SimulationScene]]
 *  can wire behaviour without depending on the internal menu item structure.
 */
class FlockMenuBar extends MenuBar:

  // ── File menu ─────────────────────────────────────────────────────────────

  private val openItem   = new MenuItem("Open...")
  private val saveItem   = new MenuItem("Save")
  private val saveAsItem = new MenuItem("Save As...")

  private val fileMenu = new Menu("File"):
    items = Seq(openItem, saveItem, new SeparatorMenuItem, saveAsItem)

  // ── View menu ─────────────────────────────────────────────────────────────

  /** Toggles the HUD overlay; checked by default. */
  private val toggleHudItem = new CheckMenuItem("HUD"):
    selected = true

  /** Radio group ensures only one theme can be active at a time. */
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

  // ── Callback registration ─────────────────────────────────────────────────

  def onOpen(action: => Unit): Unit   = openItem.onAction   = _ => action
  def onSave(action: => Unit): Unit   = saveItem.onAction   = _ => action
  def onSaveAs(action: => Unit): Unit = saveAsItem.onAction = _ => action

  /** Invokes action with the new checked state of the HUD toggle each time it is clicked. */
  def onToggleHud(action: Boolean => Unit): Unit =
    toggleHudItem.onAction = _ => action(toggleHudItem.selected.value)

  /** Invokes action with `"dark"` or `"light"` depending on which theme item was selected. */
  def onThemeChange(action: String => Unit): Unit =
    darkThemeItem.onAction  = _ => action("dark")
    lightThemeItem.onAction = _ => action("light")

end FlockMenuBar