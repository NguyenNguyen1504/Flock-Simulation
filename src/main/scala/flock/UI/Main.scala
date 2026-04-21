package flock.UI

import flock.logic.{Flock, FlockFileIO}
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success}

/** Application entry point. Initializes the flock, builds the UI, and runs the simulation loop.
 *
 *  The simulation is driven by a JavaFX [[AnimationTimer]] which fires each frame.
 *  FPS is smoothed over a rolling 30-frame window to avoid display jitter.
 *  All user interactions are wired here by registering callbacks on [[SimulationScene]].
 */
object Main extends JFXApp3:

  def start() =

    // Attempt to load a default flock; fall back to an empty flock on failure.
    val flock = FlockFileIO.loadFlockFromFile("data/testui.json") match
      case Success(f) => f
      case Failure(e) =>
        println(s"Error in loading file: ${e.getMessage}")
        new Flock(ArrayBuffer.empty)

    // Snapshot of the original boid state, used by the RESET action.
    var originalBoids = flock.boids

    val constants        = flock.constants
    val initialFlockSize = flock.boids.size

    stage = new JFXApp3.PrimaryStage():
      title     = "Flock Simulation"
      height    = 840
      width     = 840
      minWidth  = 800
      minHeight = 800
      resizable = true
      
    val mainScene        = new SimulationScene(initialFlockSize, constants, stage)
    stage.scene = mainScene

    mainScene.sync(flock)
    mainScene.render(flock)
    mainScene.updateHud(flock.boids.size, 0, isRunning = false)

    var lastTime  = 0L
    var isRunning = false
    var isDirty = false

    // Rolling FPS buffer: summed and averaged each frame to smooth out spikes.
    val fpsBuffer = Array.fill(30)(0.0)
    var fpsIndex  = 0

    // Each frame: compute delta time, step the simulation, re-render, and update the HUD.
    val timer = AnimationTimer { now =>
      if lastTime != 0L then
        val dt  = (now - lastTime) / 1_000_000_000.0  // Convert nanoseconds to seconds
        flock.update(dt)
        mainScene.render(flock)

        val rawFps = if dt > 0 then 1.0 / dt else 0.0
        fpsBuffer(fpsIndex % fpsBuffer.length) = rawFps
        fpsIndex += 1
        val avgFps = fpsBuffer.sum / fpsBuffer.length

        mainScene.updateHud(flock.boids.size, avgFps, isRunning)
      lastTime = now
    }

    // ── Wire UI → Logic ───────────────────────────────────────────────────

    mainScene.onStart {
      isRunning = true
      mainScene.updateHud(flock.boids.size, 0, isRunning)
      timer.start()
    }

    mainScene.onPause {
      isRunning = false
      timer.stop()
      lastTime = 0L  // Reset so the next start doesn't compute a huge delta from the pause gap.
      mainScene.updateHud(flock.boids.size, 0, isRunning)
    }

    mainScene.onReset {
      flock.resetWith(originalBoids)
      mainScene.sync(flock)
      mainScene.updateFlockSize(originalBoids.size)
      mainScene.updateHud(flock.boids.size, 0, isRunning)
    }

    mainScene.onQuit { stage.close() }

    mainScene.onFlockSizeChange { n =>
      flock.setSize(n)
      mainScene.sync(flock)
      mainScene.updateHud(n, 0, isRunning)
    }

    mainScene.onToggleHud { _ => mainScene.toggleHud() }

    // Theme swapping is handled inside SimulationScene; no additional logic needed here.
    mainScene.onThemeChange { _ => }

    mainScene.onOpen { path =>
      FlockFileIO.loadFlockFromFile(path) match
        case Success(f) =>
          originalBoids = f.boids
          flock.resetWith(originalBoids)
          mainScene.sync(flock)
          mainScene.render(flock)
          mainScene.updateFlockSize(originalBoids.size)
          mainScene.updateHud(flock.boids.size, 0, isRunning)
          isDirty = false
        case Failure(e) =>
          mainScene.showError(e.getMessage)
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

    mainScene.onWorldSizeChange        { flock.updateWorldSize(_, _) }

  end start

end Main