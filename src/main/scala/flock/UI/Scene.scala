package flock.UI

import flock.logic.Boid
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii, HBox, Pane, VBox}
import scalafx.scene.paint.Color

object FlockScene extends Scene:
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
