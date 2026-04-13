package flock.UI

import flock.logic.{Constants, Flock}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane

class SimulationScene(initialFlockSize: Int, constants: Constants) extends Scene:

  val mainLayout = new BorderPane():
    padding = Insets(20, 20, 20, 20)

  val flockWindow  = new FlockWindow(constants)
  val controlPanel = new ControlPanel(initialFlockSize, constants)

  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.root = mainLayout

  // Flock display

  def sync(flock: Flock): Unit =
    flockWindow.sync(flock.boids)

  def render(flock: Flock): Unit =
    flockWindow.render(flock.boids)

  // Button callbacks

  def onStart(action: => Unit): Unit =
    controlPanel.startButton.onAction = _ => action

  def onPause(action: => Unit): Unit =
    controlPanel.pauseButton.onAction = _ => action

  def onReset(action: => Unit): Unit =
    controlPanel.resetButton.onAction = _ => action

  def onQuit(action: => Unit): Unit =
    controlPanel.quitButton.onAction = _ => action

  def onSave(action: => Unit): Unit =
    controlPanel.saveButton.onAction = _ => action

  // Setting callbacks

  def onFlockSizeChange(action: Int => Unit): Unit =
    controlPanel.flockSizeSetting.onChange(action)

  def onSeparationWeightChange(action: Double => Unit): Unit =
    controlPanel.separationWeightSetting.onChange(action)

  def onAlignmentWeightChange(action: Double => Unit): Unit =
    controlPanel.alignmentWeightSetting.onChange(action)

  def onCohesionWeightChange(action: Double => Unit): Unit =
    controlPanel.cohesionWeightSetting.onChange(action)

  def updateFlockSize(n: Int): Unit = controlPanel.setFlockSize(n)
end SimulationScene