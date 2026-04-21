package flock.UI

import flock.logic.Constants
import scalafx.geometry.{Insets, Pos, VPos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority, RowConstraints, VBox}

/** The bottom panel of the simulation window containing parameter controls and action buttons.
 *
 *  Laid out as a two-column [[GridPane]]:
 *  - Left column (grows): four [[Setting]] rows for flock size and steering weights.
 *  - Right column (fixed): a vertically centred stack of START / PAUSE / RESET / QUIT buttons.
 *
 *  All interaction is exposed as callback-registration methods (`onXxx`) so that [[SimulationScene]]
 *  and ultimately [[Main]] can wire behaviour without coupling to the widget internals.
 *
 *  @param initialFlockSize Starting value for the flock size spinner.
 *  @param constants        Used to populate the initial values and bounds of each setting.
 */
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
  private val quitButton  = new Button("QUIT")  { prefWidth = 100; id = "quit-button" }

  private val actionButtons = new VBox:
    spacing = 15
    alignment = Pos.Center
    children = Seq(startButton, pauseButton, resetButton, quitButton)

  // Each row takes 25% of the panel height so the four settings are evenly distributed.
  private val rc = new RowConstraints:
    percentHeight = 25

  rowConstraints = Seq(rc, rc, rc, rc)

  // Left column stretches to fill available space; right column stays as narrow as its content.
  private val col0 = new ColumnConstraints:
    hgrow = Priority.Always

  private val col1 = new ColumnConstraints:
    hgrow = Priority.Never
    halignment = scalafx.geometry.HPos.Center

  columnConstraints = Seq(col0, col1)

  // Settings occupy column 0, one per row; buttons span all four rows in column 1.
  add(flockSizeSetting, 0, 0)
  add(separationWeightSetting, 0, 1)
  add(alignmentWeightSetting, 0, 2)
  add(cohesionWeightSetting, 0, 3)

  GridPane.setValignment(actionButtons, VPos.Center)
  add(actionButtons, 1, 0, 1, 4)

  // ── Callback registration ──────────────────────────────────────────────────

  def onStart(action: => Unit): Unit = startButton.onAction = _ => action
  def onPause(action: => Unit): Unit = pauseButton.onAction = _ => action
  def onReset(action: => Unit): Unit = resetButton.onAction = _ => action
  def onQuit(action: => Unit): Unit  = quitButton.onAction  = _ => action

  def onFlockSizeChange(action: Int => Unit): Unit           = flockSizeSetting.onChange(action)
  def onSeparationWeightChange(action: Double => Unit): Unit = separationWeightSetting.onChange(action)
  def onAlignmentWeightChange(action: Double => Unit): Unit  = alignmentWeightSetting.onChange(action)
  def onCohesionWeightChange(action: Double => Unit): Unit   = cohesionWeightSetting.onChange(action)

  /** Programmatically updates the flock size spinner (e.g. after a reset or file load). */
  def setFlockSize(n: Int): Unit = flockSizeSetting.setValue(n)

end ControlPanel