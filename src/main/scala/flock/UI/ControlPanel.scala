package flock.UI

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{GridPane, VBox}

class ControlPanel extends GridPane:

  minWidth = 800
  minHeight = 400
  hgap = 40
  vgap = 10
  padding = Insets(20)

  // Settings
  val numberOfBirdsSetting = SpinnerSetting("Number of birds", 10, 200, 50)
  val separationWeightSetting = SliderSetting("Separation weight", 0.0, 15.0, 5.0)
  val alignmentWeightSetting = SliderSetting("Alignment weight", 0.0, 15.0, 3.0)
  val cohesionWeightSetting = SliderSetting("Cohesion weight", 0.0, 15.0, 3.0)

  // Buttons
  val actionButtons = new VBox:
    spacing = 10
    alignment = Pos.TopCenter
    children = Seq(
      new Button("START") { prefWidth = 100 },
      new Button("PAUSE") { prefWidth = 100 },
      new Button("RESET") { prefWidth = 100 },
      new Button("QUIT")  { prefWidth = 100; style = "-fx-base: #ff4444;" }, // Màu đỏ cho Quit
      new Button("SAVE")  { prefWidth = 100 }
    )

  add(numberOfBirdsSetting, 0, 0)
  add(separationWeightSetting, 0, 1)
  add(alignmentWeightSetting, 0, 2)
  add(cohesionWeightSetting, 0, 3)
  add(actionButtons, 1, 0, 1, 4)


end ControlPanel
