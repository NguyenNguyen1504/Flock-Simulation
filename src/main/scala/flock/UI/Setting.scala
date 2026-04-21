package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.{HBox, Priority}
import scalafx.Includes.*

/** Base trait for a labelled control row displayed in the [[ControlPanel]].
 *  Subclasses extend this with a specific input widget (slider, spinner, etc.).
 *  Lays out children horizontally with a fixed-width title label on the left.
 */
trait Setting extends HBox:

  spacing   = 15
  alignment = Pos.CenterLeft
  maxWidth  = Double.MaxValue

  val labelName: String
  lazy val titleLabel = new Label(labelName):
    styleClass += "setting-label"
    minWidth = 150
    maxWidth = 150
    alignment = Pos.CenterRight

end Setting


/** A labelled horizontal slider for adjusting a Double-valued simulation parameter.
 *  Displays the current value as a formatted number to one decimal place beside the slider.
 *  The slider track fills with the accent color up to the current value (CSS gradient).
 *
 *  @param labelName  Display name shown in the title label.
 *  @param minValue   Minimum slider value.
 *  @param maxValue   Maximum slider value.
 *  @param initial    Starting value.
 */
class SliderSetting(val labelName: String, minValue: Double, maxValue: Double, initial: Double) extends Setting:

  private val slider = new Slider(minValue, maxValue, initial):
    styleClass += "flock-slider"
    maxWidth = Double.MaxValue

  HBox.setHgrow(slider, Priority.Always)

  /** Computes what percentage of the slider range the value v occupies, for the CSS gradient. */
  private def fillPercent(v: Double): Double =
    ((v - minValue) / (maxValue - minValue)) * 100.0

  /** Repaints the slider track as a two-color gradient split at the current value percentage.
   *  Applied inline because CSS files cannot reliably handle dynamic rgba/gradient values in ScalaFX.
   */
  private def updateTrack(v: Double): Unit =
    val pct = fillPercent(v)
    slider.style =
      s"-fx-background-color: transparent;" +
      s"-track-color: linear-gradient(to right, #4ECDC4 $pct%, #D8DEE9 $pct%);"

  updateTrack(initial)
  slider.value.onChange((_, _, v) => updateTrack(v.doubleValue()))

  private val valueLabel = new Label(f"$initial%.1f"):
    styleClass += "setting-value"
    minWidth = 45
    alignment = Pos.CenterLeft

  slider.value.onChange((_, _, v) =>
    valueLabel.text = f"${v.doubleValue()}%.1f"
  )

  children = Seq(titleLabel, slider, valueLabel)

  /** Registers a callback invoked whenever the slider value changes. */
  def onChange(action: Double => Unit): Unit =
    slider.value.onChange((_, _, v) => action(v.doubleValue()))

end SliderSetting


/** A labelled integer spinner for adjusting a bounded Int-valued parameter (e.g. flock size).
 *  The spinner is editable; typed values are committed when the field loses focus.
 *
 *  @param labelName  Display name shown in the title label.
 *  @param minValue   Minimum allowed integer value.
 *  @param maxValue   Maximum allowed integer value.
 *  @param initial    Starting value.
 */
class SpinnerSetting(val labelName: String, minValue: Int, maxValue: Int, initial: Int) extends Setting:

  private val spinner = new Spinner[Int](minValue, maxValue, initial):
    styleClass += "flock-spinner"
    editable = true
    maxWidth = 90
    minWidth = 90

  // Calling increment(0) with no delta forces the spinner to validate and commit
  // whatever text the user has typed, without actually changing the value.
  spinner.focusedProperty().onChange((_, _, isFocused) =>
    if !isFocused then spinner.increment(0)
  )

  children = Seq(titleLabel, spinner)

  /** Registers a callback invoked whenever the spinner value changes. */
  def onChange(action: Int => Unit): Unit =
    spinner.valueProperty().onChange((_, _, v) => action(v))

  /** Sets the spinner to a specific value (e.g. after a flock reset). */
  def setValue(n: Int): Unit =
    spinner.valueFactory().value() = n

end SpinnerSetting