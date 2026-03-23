package flock.UI
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{HBox, VBox}

object Main extends JFXApp3:

  def start() =
    stage = new JFXApp3.PrimaryStage():
      title = "Flock Simulation"
      height = 800
      width = 800
      resizable = true

  end start

end Main

