package flock.UI
import scalafx.application.JFXApp3

object Main extends JFXApp3:

  def start() =
    stage = new JFXApp3.PrimaryStage():
      title = "Flock Simulation"
      height = 840
      width = 840
      minWidth = 800  // Giới hạn chiều rộng của Cửa sổ
      minHeight = 650
      resizable = true

    stage.scene = FlockScene
  end start

end Main

