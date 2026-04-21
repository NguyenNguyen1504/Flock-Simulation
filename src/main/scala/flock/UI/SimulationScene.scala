package flock.UI

import flock.logic.{Constants, Flock}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/** The top-level [[Scene]] for the simulation window.
 *
 *  Composes three UI regions in a [[BorderPane]]:
 *  - Top: [[FlockMenuBar]] — file and view menus.
 *  - Center: [[FlockWindow]] — the boid canvas with HUD overlay.
 *  - Bottom: [[ControlPanel]] — sliders, spinner, and action buttons.
 *
 *  Acts as a facade: all callback-registration and display-update calls from [[Main]]
 *  go through this class, which delegates to the appropriate subcomponent. This keeps
 *  [[Main]] decoupled from the internal widget structure.
 *
 *  @param initialFlockSize Passed to [[ControlPanel]] to initialize the flock size spinner.
 *  @param constants        Passed to [[ControlPanel]] and [[FlockWindow]] for initial values.
 */
class SimulationScene(initialFlockSize: Int, constants: Constants, owner: Stage) extends Scene:

  /** Resolves a CSS file path, preferring the classpath resource over a fallback file path. */
  private def cssPath(filename: String): String =
    Option(getClass.getResource(s"/css/$filename")) match
      case Some(url) => url.toExternalForm
      case None      => s"file:data/$filename"

  private val darkThemePath  = cssPath("dark-theme.css")
  private val lightThemePath = cssPath("light-theme.css")

  private val mainLayout = new BorderPane():
    padding = Insets(20, 20, 20, 20)

  private val menuBar      = new FlockMenuBar
  private val flockWindow  = new FlockWindow(constants)
  private val controlPanel = new ControlPanel(initialFlockSize, constants)

  mainLayout.top    = menuBar
  mainLayout.center = flockWindow
  mainLayout.bottom = controlPanel

  this.stylesheets.add(darkThemePath)
  this.root = mainLayout

  // ── Flock display ─────────────────────────────────────────────────────────

  /** Syncs the shape pool with the flock's current boid count. See [[FlockWindow.sync]]. */
  def sync(flock: Flock): Unit = flockWindow.sync(flock.boids)

  /** Updates every boid shape's position and rotation. See [[FlockWindow.render]]. */
  def render(flock: Flock): Unit = flockWindow.render(flock.boids)

  /** Forwards HUD data to [[FlockWindow.updateHud]]. */
  def updateHud(boidCount: Int, fps: Double, isRunning: Boolean): Unit =
    flockWindow.updateHud(boidCount, fps, isRunning)

  // ── Menu bar callbacks ────────────────────────────────────────────────────

  /** Shows the open-file dialog, then calls action with the chosen path.
   *  Nothing happens if the user cancels. */
  def onOpen(action: String => Unit): Unit =
    menuBar.onOpen {
      FlockFileDialog.showOpen(owner).foreach { file =>
        action(file.getPath)
      }
    }

  /** Shows a save-overwrite confirmation, then calls action if the user confirms. */
  def onSave(action: => Unit): Unit =
    menuBar.onSave {
      if FlockFileDialog.showConfirmation(
        owner,
        titleStr = "Save",
        header   = "Overwrite save.json?",
        content  = "This will overwrite data/save.json. Continue?"
      ) then action
    }
  def onSaveAs(action: => Unit): Unit            = menuBar.onSaveAs(action)
  def onToggleHud(action: Boolean => Unit): Unit = menuBar.onToggleHud(action)

  def toggleHud(): Unit = flockWindow.toggleHud()

  /** Handles theme switching by swapping the active stylesheet, then forwarding to the caller's action. */
  def onThemeChange(action: String => Unit): Unit =
    menuBar.onThemeChange { theme =>
      this.stylesheets.clear()
      theme match
        case "light" => this.stylesheets.add(lightThemePath)
        case "dark"  => this.stylesheets.add(darkThemePath)
      action(theme)
    }

  // ── Control panel callbacks ───────────────────────────────────────────────

  def onStart(action: => Unit): Unit = controlPanel.onStart(action)
  def onPause(action: => Unit): Unit = controlPanel.onPause(action)
  def onReset(action: => Unit): Unit = controlPanel.onReset(action)
  def onQuit(action: => Unit): Unit  = controlPanel.onQuit(action)

  def onFlockSizeChange(action: Int => Unit): Unit           = controlPanel.onFlockSizeChange(action)
  def onSeparationWeightChange(action: Double => Unit): Unit = controlPanel.onSeparationWeightChange(action)
  def onAlignmentWeightChange(action: Double => Unit): Unit  = controlPanel.onAlignmentWeightChange(action)
  def onCohesionWeightChange(action: Double => Unit): Unit   = controlPanel.onCohesionWeightChange(action)

  /** Fires whenever the [[FlockWindow]] is resized, passing the new width and height to the callback.
   *  Used to keep the logical world size in sync with the rendered canvas size.
   */
  def onWorldSizeChange(action: (Double, Double) => Unit): Unit =
    flockWindow.width.onChange((_, _, w) => action(w.doubleValue, flockWindow.height.value))
    flockWindow.height.onChange((_, _, h) => action(flockWindow.width.value, h.doubleValue))

  /** Programmatically updates the flock size spinner (e.g. after a file load or reset). */
  def updateFlockSize(n: Int): Unit = controlPanel.setFlockSize(n)

end SimulationScene