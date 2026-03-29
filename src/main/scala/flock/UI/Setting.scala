package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.{HBox, Priority}
import scalafx.Includes.*

trait Setting extends HBox:

  spacing = 15
  alignment = Pos.CenterLeft
  maxWidth = Double.MaxValue

  val labelName: String
  // Tạo label với chiều rộng cố định để thẳng hàng
  lazy val titleLabel = new Label(labelName):
    minWidth = 150
    maxWidth = 150
    alignment = Pos.CenterRight // Căn chữ sang phải để sát vào Slider


end Setting

class SliderSetting(val labelName: String, minValue: Double, maxValue: Double, initial: Double) extends Setting:

  val slider = new Slider(minValue, maxValue, initial)

  HBox.setHgrow(slider, Priority.Always) // Force slider to span
  slider.maxWidth = Double.MaxValue

  val valueLabel = new Label(f"$initial%.1f"):
    minWidth = 45
    alignment = Pos.CenterLeft
  slider.value.onChange((_, _, v) =>
    valueLabel.text = f"${v.doubleValue()}%.1f"
  )

  children = Seq(titleLabel, slider, valueLabel)

  def onChange(action: Double => Unit) =
    slider.value.onChange((_, _, v) => action(v.doubleValue()))

end SliderSetting

class SpinnerSetting(val labelName: String, minValue: Int, maxValue: Int, initial: Int) extends Setting:

  val spinner = new Spinner[Int](minValue, maxValue, initial):
    editable = true
  // Update when focus lost
  spinner.focusedProperty().onChange((_, _, isFocused) => {
    if (!isFocused) spinner.increment(0)
  })

  HBox.setHgrow(spinner, Priority.Always)
  spinner.maxWidth = Double.MaxValue

  children = Seq(titleLabel, spinner)

  def onChange(action: Int => Unit) = spinner.valueProperty().onChange((_, _, v) => action(v))

end SpinnerSetting
