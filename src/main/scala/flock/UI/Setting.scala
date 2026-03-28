package flock.UI

import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox

trait Setting extends HBox:
  
  spacing = 10
  alignment = Pos.CenterLeft
  val labelName: String
  children = Seq(new Label(labelName))

end Setting

