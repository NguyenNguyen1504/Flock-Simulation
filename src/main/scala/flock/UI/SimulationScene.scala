package flock.UI

import flock.logic.Flock
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, HBox}

object SimulationScene extends Scene:
  
  def loadFlock(flock: Flock): Unit =
    flockWindow.sync(flock.boids)
    flockWindow.render(flock.boids)

  val mainLayout = new BorderPane():
    padding = Insets(20,20,20,20)

  // Flock window
  val flockWindow = new FlockWindow()

  // Control Panel
  val controlPanel = new HBox():
    minWidth = 800
    minHeight = 400

  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.root = mainLayout
