package flock.UI

import flock.logic.{Constants, Flock}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane

class SimulationScene(initialFlockSize: Int, constants: Constants) extends Scene:

  private val mainLayout = new BorderPane():
    padding = Insets(20, 20, 20, 20)

  private val menuBar      = new FlockMenuBar
  private val flockWindow  = new FlockWindow(constants)
  private val controlPanel = new ControlPanel(initialFlockSize, constants)

  mainLayout.top    = menuBar
  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.stylesheets.add("file:data/dark-theme.css")
  this.root = mainLayout

  // Flock display

  def sync(flock: Flock): Unit =
    flockWindow.sync(flock.boids)

  def render(flock: Flock): Unit =
    flockWindow.render(flock.boids)
  
  def onOpen(action: => Unit): Unit   = menuBar.onOpen(action)
  def onSave(action: => Unit): Unit   = menuBar.onSave(action)
  def onSaveAs(action: => Unit): Unit = menuBar.onSaveAs(action)

  def onStart(action: => Unit): Unit = controlPanel.onStart(action)
  def onPause(action: => Unit): Unit = controlPanel.onPause(action)
  def onReset(action: => Unit): Unit = controlPanel.onReset(action)
  def onQuit(action: => Unit): Unit  = controlPanel.onQuit(action)

  def onFlockSizeChange(action: Int => Unit): Unit           = controlPanel.onFlockSizeChange(action)
  def onSeparationWeightChange(action: Double => Unit): Unit = controlPanel.onSeparationWeightChange(action)
  def onAlignmentWeightChange(action: Double=> Unit): Unit   = controlPanel.onAlignmentWeightChange(action)
  def onCohesionWeightChange(action: Double => Unit): Unit   = controlPanel.onCohesionWeightChange(action)

  def onWorldSizeChange(action: (Double, Double) => Unit): Unit =
    flockWindow.width.onChange((_, _, w) => action(w.doubleValue, flockWindow.height.value))
    flockWindow.height.onChange((_, _, h) => action(flockWindow.width.value, h.doubleValue))

  def updateFlockSize(n: Int): Unit = controlPanel.setFlockSize(n)

end SimulationScene