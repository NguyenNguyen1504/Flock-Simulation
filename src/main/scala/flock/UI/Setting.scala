package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.HBox
import scalafx.Includes._

trait Setting extends HBox:

  spacing = 10
  alignment = Pos.CenterLeft
  val labelName: String
  children = Seq(new Label(labelName))

end Setting

class SliderSetting(val labelName: String, minValue: Double, maxValue: Double, initial: Double) extends Setting:

  val slider = new Slider(minValue, maxValue, initial)
  children.add(slider)
  def onChange(action: Double => Unit) =
    slider.value.onChange((_, _, v) => action(v.doubleValue()))

end SliderSetting

class SpinnerSetting(val labelName: String, minValue: Int, maxValue: Int, initial: Int) extends Setting:

  val spinner = new Spinner[Int](minValue, maxValue, initial):
    editable = true // Cho phép gõ số bằng tay

  // Trick để cập nhật giá trị ngay khi người dùng nhấn Enter hoặc mất focus
  spinner.focusedProperty().onChange((_, _, isFocused) => {
    if (!isFocused) spinner.increment(0)
  })
  children.add(spinner)
  def onChange(action: Int => Unit) = spinner.valueProperty().onChange((_, _, v) => action(v))

end SpinnerSetting
