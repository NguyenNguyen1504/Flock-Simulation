package flock.UI

import flock.logic.Constants
import scalafx.geometry.{Insets, Pos, VPos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority, RowConstraints, VBox}

class ControlPanel(initialFlockSize: Int, constants: Constants) extends GridPane:

  minWidth = 800
  minHeight = 400
  hgap = 40
  vgap = 20
  padding = Insets(30)

  private val flockSizeSetting        = SpinnerSetting("Flock size", constants.minFlockSize, constants.maxFlockSize, initialFlockSize)
  private val separationWeightSetting = SliderSetting("Separation weight", 0.0, 15.0, constants.separationWeight)
  private val alignmentWeightSetting  = SliderSetting("Alignment weight", 0.0, 15.0, constants.alignmentWeight)
  private val cohesionWeightSetting   = SliderSetting("Cohesion weight", 0.0, 15.0, constants.cohesionWeight)

  flockSizeSetting.maxWidth        = Double.MaxValue
  separationWeightSetting.maxWidth = Double.MaxValue
  alignmentWeightSetting.maxWidth  = Double.MaxValue
  cohesionWeightSetting.maxWidth   = Double.MaxValue

  private val startButton = new Button("START") { prefWidth = 100 }
  private val pauseButton = new Button("PAUSE") { prefWidth = 100 }
  private val resetButton = new Button("RESET") { prefWidth = 100 }
  private val quitButton  = new Button("QUIT")  { prefWidth = 100; style = "-fx-base: #ff4444;" }
  private val saveButton  = new Button("SAVE")  { prefWidth = 100 }

  private val actionButtons = new VBox:
    spacing = 15
    alignment = Pos.Center
    children = Seq(startButton, pauseButton, resetButton, quitButton, saveButton)

  private val rc = new RowConstraints:
    percentHeight = 25

  rowConstraints = Seq(rc, rc, rc, rc)

  private val col0 = new ColumnConstraints:
    hgrow = Priority.Always

  private val col1 = new ColumnConstraints:
    hgrow = Priority.Never
    halignment = scalafx.geometry.HPos.Center

  columnConstraints = Seq(col0, col1)

  add(flockSizeSetting, 0, 0)
  add(separationWeightSetting, 0, 1)
  add(alignmentWeightSetting, 0, 2)
  add(cohesionWeightSetting, 0, 3)

  GridPane.setValignment(actionButtons, VPos.Center)
  add(actionButtons, 1, 0, 1, 4)

  def onStart(action: => Unit): Unit  = startButton.onAction = _ => action
  def onPause(action: => Unit): Unit  = pauseButton.onAction = _ => action
  def onReset(action: => Unit): Unit  = resetButton.onAction = _ => action
  def onQuit(action: => Unit): Unit   = quitButton.onAction  = _ => action
  def onSave(action: => Unit): Unit   = saveButton.onAction  = _ => action

  def onFlockSizeChange(action: Int => Unit): Unit           = flockSizeSetting.onChange(action)
  def onSeparationWeightChange(action: Double => Unit): Unit = separationWeightSetting.onChange(action)
  def onAlignmentWeightChange(action: Double => Unit): Unit  = alignmentWeightSetting.onChange(action)
  def onCohesionWeightChange(action: Double => Unit): Unit   = cohesionWeightSetting.onChange(action)

  def setFlockSize(n: Int): Unit = flockSizeSetting.setValue(n)
    
end ControlPanel