package flock.UI
import flock.logic.{Flock, FlockFileIO}
import scalafx.application.JFXApp3

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success}

object Main extends JFXApp3:

  def start() =
    val flock = FlockFileIO.loadFlockFromFile("data/testui.json") match
      case Success(f) => f
      case Failure(e) =>
        println(s"Error in loading file: ${e.getMessage}")
        new Flock(ArrayBuffer.empty)

    stage = new JFXApp3.PrimaryStage():
      title = "Flock Simulation"
      height = 840
      width = 840
      minWidth = 800  // Giới hạn chiều rộng của Cửa sổ
      minHeight = 800
      resizable = true

    SimulationScene.loadFlock(flock)
    stage.scene = SimulationScene
  end start

end Main

