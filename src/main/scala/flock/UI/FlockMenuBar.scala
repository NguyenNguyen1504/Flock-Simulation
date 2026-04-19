package flock.UI

import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}

class FlockMenuBar extends MenuBar:

  private val openItem   = new MenuItem("Open...")
  private val saveItem   = new MenuItem("Save")
  private val saveAsItem = new MenuItem("Save As...")

  private val fileMenu = new Menu("File"):
    items = Seq(openItem, saveItem, new SeparatorMenuItem, saveAsItem)

  menus = Seq(fileMenu)

  def onOpen(action: => Unit): Unit   = openItem.onAction   = _ => action
  def onSave(action: => Unit): Unit   = saveItem.onAction   = _ => action
  def onSaveAs(action: => Unit): Unit = saveAsItem.onAction = _ => action

end FlockMenuBar