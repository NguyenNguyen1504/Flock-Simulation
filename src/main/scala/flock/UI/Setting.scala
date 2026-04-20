package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.{HBox, Priority}
import scalafx.Includes.*

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

// ── Slider ────────────────────────────────────────────────────────────────────

class SliderSetting(val labelName: String, minValue: Double, maxValue: Double, initial: Double) extends Setting:

  private val slider = new Slider(minValue, maxValue, initial):
    styleClass += "flock-slider"
    maxWidth = Double.MaxValue

  HBox.setHgrow(slider, Priority.Always)

  private def fillPercent(v: Double): Double =
    ((v - minValue) / (maxValue - minValue)) * 100.0

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

  def onChange(action: Double => Unit): Unit =
    slider.value.onChange((_, _, v) => action(v.doubleValue()))

end SliderSetting

// ── Spinner ───────────────────────────────────────────────────────────────────

class SpinnerSetting(val labelName: String, minValue: Int, maxValue: Int, initial: Int) extends Setting:

  private val spinner = new Spinner[Int](minValue, maxValue, initial):
    styleClass += "flock-spinner"
    editable = true
    maxWidth = 90
    minWidth = 90

  // Commit on focus lost
  spinner.focusedProperty().onChange((_, _, isFocused) =>
    if !isFocused then spinner.increment(0)
  )

  children = Seq(titleLabel, spinner)

  def onChange(action: Int => Unit): Unit =
    spinner.valueProperty().onChange((_, _, v) => action(v))

  def setValue(n: Int): Unit =
    spinner.valueFactory().value() = n

end SpinnerSetting