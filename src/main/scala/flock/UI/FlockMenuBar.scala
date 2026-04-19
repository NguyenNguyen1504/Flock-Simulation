package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.{CheckMenuItem, Label, Menu, MenuBar, MenuItem, SeparatorMenuItem}
import scalafx.scene.layout.HBox

class FlockMenuBar extends MenuBar:

  // ── Logo ────────────────────────────────────────────────────────────────

  private val logo = new Label("FLOCK"):
    styleClass += "menubar-logo"

  // ── File menu ────────────────────────────────────────────────────────────

  private val openItem   = new MenuItem("Open...")
  private val saveItem   = new MenuItem("Save")
  private val saveAsItem = new MenuItem("Save As...")

  private val fileMenu = new Menu("File"):
    styleClass += "menubar-menu"
    items = Seq(openItem, saveItem, new SeparatorMenuItem, saveAsItem)

  // ── View menu ────────────────────────────────────────────────────────────

  private val toggleHudItem = new CheckMenuItem("HUD"):
    selected = true

  private val viewMenu = new Menu("View"):
    styleClass += "menubar-menu"
    items = Seq(toggleHudItem)

  // ── Layout ───────────────────────────────────────────────────────────────

  useSystemMenuBar = false
  menus = Seq(fileMenu, viewMenu)

  // Logo on the left of menubar
  styleClass += "flock-menubar"

  private val logoMenu = new Menu():
    styleClass += "menubar-logo-menu"
    graphic = logo
    disable = true   // only for display

  menus = Seq(logoMenu, fileMenu, viewMenu)

  // ── Callbacks ────────────────────────────────────────────────────────────

  def onOpen(action: => Unit): Unit         = openItem.onAction      = _ => action
  def onSave(action: => Unit): Unit         = saveItem.onAction      = _ => action
  def onSaveAs(action: => Unit): Unit       = saveAsItem.onAction    = _ => action
  def onToggleHud(action: Boolean => Unit): Unit =
    toggleHudItem.onAction = _ => action(toggleHudItem.selected.value)

end FlockMenuBar