package flock.UI

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii, HBox, Pane, VBox}
import scalafx.scene.paint.Color

object FlockScene extends Scene:
  val mainLayout = new BorderPane():
    padding = Insets(20,20,20,20)
  
  // Flock window
  val flockWindow = new Pane():
    minWidth = 800
    minHeight = 400
    maxHeight = 400
    background = new Background(Array(
  new BackgroundFill(Color.color(0.871, 0.871, 0.847), CornerRadii.Empty, Insets(0))
  ))
    
  

  // Control Panel
  val controlPanel = new HBox():
    minWidth = 800
    minHeight = 400

  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.root = mainLayout
