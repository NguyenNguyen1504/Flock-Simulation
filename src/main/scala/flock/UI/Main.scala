package flock.UI

import flock.logic.{Flock, FlockFileIO}
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

    var originalBoids = flock.boids

    val constants = flock.constants
    val initialFlockSize = flock.boids.size
    val mainScene = new SimulationScene(initialFlockSize, constants)

    stage = new JFXApp3.PrimaryStage():
      title = "Flock Simulation"
      height = 840
      width = 840
      minWidth = 800
      minHeight = 800
      resizable = true
      scene = mainScene
    
    mainScene.sync(flock)
    mainScene.render(flock)

    var lastTime = 0L
    val timer = AnimationTimer( now =>
      if lastTime != 0L then
        val dt = (now - lastTime) / 1000000000.0
        flock.update(dt)
        mainScene.render(flock)
      lastTime = now
    )

    // Wire UI → Logic

    mainScene.onStart { timer.start() }

    mainScene.onPause {
      timer.stop()
      lastTime = 0L
    }

    mainScene.onReset {
      flock.resetWith(originalBoids)
      mainScene.sync(flock)
      mainScene.updateFlockSize(originalBoids.size)
    }

    mainScene.onQuit { stage.close() }

    mainScene.onFlockSizeChange { n =>
      flock.setSize(n)
      mainScene.sync(flock)
    }

    mainScene.onOpen {
      FlockFileDialog.showOpen(stage).foreach { file =>
        FlockFileIO.loadFlockFromFile(file.getPath) match
          case Success(f) =>
            originalBoids = f.boids
            flock.resetWith(originalBoids)
            mainScene.sync(flock)
            mainScene.render(flock)
            mainScene.updateFlockSize(originalBoids.size)
          case Failure(e) => println(s"Load failed: ${e.getMessage}")
      }
    }

    mainScene.onSave {
      FlockFileIO.saveFlockToFile(flock, "data/save.json") match
        case Success(_) => println("Saved successfully")
        case Failure(e) => println(s"Save failed: ${e.getMessage}")
    }

    mainScene.onSaveAs {
      FlockFileDialog.showSaveAs(stage).foreach { file =>
        FlockFileIO.saveFlockToFile(flock, file.getPath) match
          case Success(_) => println("Saved successfully")
          case Failure(e) => println(s"Save failed: ${e.getMessage}")
      }
    }

    mainScene.onSeparationWeightChange { flock.updateSeparationWeight(_) }
    mainScene.onAlignmentWeightChange  { flock.updateAlignmentWeight(_) }
    mainScene.onCohesionWeightChange   { flock.updateCohesionWeight(_) }


  end start

end Main