package flock.UI

import flock.logic.Constants
import scalafx.geometry.{Insets, Pos, VPos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority, RowConstraints, VBox}

class ControlPanel(initialFlockSize: Int) extends GridPane:

  minWidth = 800
  minHeight = 400
  hgap = 40
  vgap = 20
  padding = Insets(30)

  val flockSizeSetting    = SpinnerSetting("Flock size", Constants.minFlockSize, Constants.maxFlockSize, initialFlockSize)
  val separationWeightSetting = SliderSetting("Separation weight", 0.0, 15.0, Constants.separationWeight)
  val alignmentWeightSetting  = SliderSetting("Alignment weight", 0.0, 15.0, Constants.alignmentWeight)
  val cohesionWeightSetting   = SliderSetting("Cohesion weight", 0.0, 15.0, Constants.cohesionWeight)

  flockSizeSetting.maxWidth        = Double.MaxValue
  separationWeightSetting.maxWidth = Double.MaxValue
  alignmentWeightSetting.maxWidth  = Double.MaxValue
  cohesionWeightSetting.maxWidth   = Double.MaxValue

  val startButton = new Button("START") { prefWidth = 100 }
  val pauseButton = new Button("PAUSE") { prefWidth = 100 }
  val resetButton = new Button("RESET") { prefWidth = 100 }
  val quitButton  = new Button("QUIT")  { prefWidth = 100; style = "-fx-base: #ff4444;" }
  val saveButton  = new Button("SAVE")  { prefWidth = 100 }

  val actionButtons = new VBox:
    spacing = 15
    alignment = Pos.Center
    children = Seq(startButton, pauseButton, resetButton, quitButton, saveButton)

  val rc = new RowConstraints:
    percentHeight = 25

  rowConstraints = Seq(rc, rc, rc, rc)

  val col0 = new ColumnConstraints:
    hgrow = Priority.Always

  val col1 = new ColumnConstraints:
    hgrow = Priority.Never
    halignment = scalafx.geometry.HPos.Center

  columnConstraints = Seq(col0, col1)

  add(flockSizeSetting, 0, 0)
  add(separationWeightSetting, 0, 1)
  add(alignmentWeightSetting, 0, 2)
  add(cohesionWeightSetting, 0, 3)

  GridPane.setValignment(actionButtons, VPos.Center)
  add(actionButtons, 1, 0, 1, 4)

end ControlPanel