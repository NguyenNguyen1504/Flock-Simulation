package flock.UI

import flock.logic.Flock
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, HBox}

object SimulationScene extends Scene:

  val mainLayout = new BorderPane():
    padding = Insets(20,20,20,20)

  // Flock window
  val flockWindow = new FlockWindow()  
  
  def sync(flock: Flock): Unit =
    flockWindow.sync(flock.boids)
  
  def render(flock: Flock): Unit =
    flockWindow.render(flock.boids)

  // Control Panel
  val controlPanel = new ControlPanel()

  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.root = mainLayout
