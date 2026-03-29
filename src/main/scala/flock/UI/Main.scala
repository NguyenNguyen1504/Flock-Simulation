package flock.UI

import flock.logic.{Constants, Flock, FlockFileIO}
import scalafx.animation.AnimationTimer
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
      minWidth = 800
      minHeight = 800
      resizable = true

    SimulationScene.sync(flock)
    stage.scene = SimulationScene

    var lastTime = 0L
    val timer = AnimationTimer( now =>
      if lastTime != 0L then
        val dt = (now - lastTime) / 1000000000.0
        flock.update(dt)
        SimulationScene.render(flock)
      lastTime = now
    )

    // Wire UI → Logic

    SimulationScene.onStart { timer.start() }

    SimulationScene.onPause {
      timer.stop()
      lastTime = 0L
    }

    SimulationScene.onReset {
      println("RESET: not implemented")
      SimulationScene.sync(flock)
    }

    SimulationScene.onQuit { stage.close() }

    SimulationScene.onSave {
      FlockFileIO.saveFlockToFile(flock, "data/save.json") match
        case Success(_) => println("Saved successfully")
        case Failure(e) => println(s"Save failed: ${e.getMessage}")
    }

    SimulationScene.onNumberOfBirdsChange { n =>
      println("RESIZE: not implemented")
      SimulationScene.sync(flock)
    }

    SimulationScene.onSeparationWeightChange { w => Constants.separationWeight = w }
    SimulationScene.onAlignmentWeightChange  { w => Constants.alignmentWeight  = w }
    SimulationScene.onCohesionWeightChange   { w => Constants.cohesionWeight   = w }

    timer.start()

  end start

end Main