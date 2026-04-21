package flock.UI

import flock.logic.{Boid, Constants}
import scalafx.geometry.Insets
import scalafx.scene.Group
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox, Pane, VBox}
import scalafx.scene.shape.{Polygon, Rectangle}
import scalafx.scene.paint.Color

import scala.collection.mutable.ArrayBuffer

/** The canvas pane that renders boids and an optional HUD overlay.
 *
 *  Boid shapes are managed as a pool of [[Polygon]] nodes inside a [[Group]].
 *  Rendering is split into two explicit steps to keep synchronization and position
 *  updates separate:
 *  - [[sync]] — adds or removes polygon nodes to match the current boid count.
 *  - [[render]] — updates position and rotation of each polygon to match boid state.
 *
 *  A clipping rectangle bound to the pane's dimensions prevents boids from drawing
 *  outside the world boundary.
 *
 *  @param constants Used for initial world dimensions and styling.
 */
class FlockWindow(constants: Constants) extends Pane:

  minWidth = 800
  minHeight = 400
  background = new Background(Array(
    new BackgroundFill(Color.web("#0B0E14"), CornerRadii.Empty, Insets(0))
  ))
  style = "-fx-border-color: #4C566A; -fx-border-width: 1; -fx-border-style: solid;"

  // Pool of polygon shapes; kept in sync with the boid list by sync().
  private val boidShapes = ArrayBuffer[Polygon]()
  private val boidGroup  = new Group()
  this.children.add(boidGroup)

  /** Creates a single arrow-shaped polygon representing one boid, pointing right by default. */
  private def createBoidShapes(): Polygon =
    new Polygon:
      points ++= Seq(-6.0, -5.0, 6.0, 0.0, -6.0, 5.0)
      fill = Color.web("#4ECDC4")

  // ── HUD ──────────────────────────────────────────────────────────────────

  private def makeLabel(text: String, cssClass: String): Label =
    new Label(text):
      styleClass += cssClass

  private val boidsLabel  = makeLabel("0",        "hud-value")
  private val fpsLabel    = makeLabel("0",        "hud-value")
  private val statusLabel = makeLabel("● PAUSED", "hud-value-muted")

  private val hudBox = new HBox(20):
    padding = Insets(8, 14, 8, 14)
    // rgba background is set inline because CSS files don't handle rgba reliably in ScalaFX.
    style = "-fx-background-color: rgba(13,17,23,0.78);" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #2E3440;" +
            "-fx-border-width: 0.5;" +
            "-fx-border-radius: 8;"
    children = Seq(
      new VBox(2, makeLabel("BOIDS",  "hud-label"), boidsLabel),
      new VBox(2, makeLabel("FPS",    "hud-label"), fpsLabel),
      new VBox(2, makeLabel("STATUS", "hud-label"), statusLabel)
    )

  hudBox.layoutX = 14
  hudBox.layoutY = 14
  this.children.add(hudBox)

  /** Updates the HUD labels. The status indicator also changes CSS class to reflect running state. */
  def updateHud(boidCount: Int, fps: Double, isRunning: Boolean): Unit =
    boidsLabel.text = boidCount.toString
    fpsLabel.text   = fps.toInt.toString
    if isRunning then
      statusLabel.text = "● RUNNING"
      statusLabel.styleClass.setAll("hud-value-running")
    else
      statusLabel.text = "● PAUSED"
      statusLabel.styleClass.setAll("hud-value-muted")

  /** Shows or hides the HUD overlay. */
  def toggleHud(): Unit =
    hudBox.visible = !hudBox.visible.value

  // ── Sync / Render ─────────────────────────────────────────────────────────

  /** Sync the shape pool with the current boid list.
   *  Adds new polygon nodes if the flock grew, removes trailing ones if it shrank.
   *  Must be called before [[render]] whenever the boid count changes.
   */
  def sync(boids: Seq[Boid]): Unit =
    val diff = boids.size - boidShapes.size
    if diff > 0 then
      val newShapes = ArrayBuffer.fill(diff){ createBoidShapes() }
      boidShapes ++= newShapes
      boidGroup.children.addAll(newShapes.map(_.delegate))
    else if diff < 0 then
      val count = -diff
      for (_ <- 1 to count) do
        if boidShapes.nonEmpty then
          val lastShape = boidShapes.remove(boidShapes.size - 1)
          boidGroup.children.remove(lastShape.delegate)

  /** Moves and rotates each polygon to match the corresponding boid's position and heading.
   *  Rotation is derived from the velocity vector using atan2, converted to degrees for JavaFX.
   *  Requires [[sync]] to have been called so that boids.size == boidShapes.size.
   */
  def render(boids: Seq[Boid]): Unit =
    require(
      boids.size == boidShapes.size,
      s"render() called with ${boids.size} boids but ${boidShapes.size} shapes. Call sync() first"
    )
    boids.zip(boidShapes).foreach { (boid, shape) =>
      shape.translateX = boid.position.x
      shape.translateY = boid.position.y
      shape.rotate     = math.toDegrees(math.atan2(boid.velocity.y, boid.velocity.x))
    }

  // Bind a clipping rectangle to the pane's size so boids are hidden outside the world boundary.
  private val clipRectangle = new Rectangle()
  clipRectangle.width  <== this.width
  clipRectangle.height <== this.height
  this.clip = clipRectangle

end FlockWindow