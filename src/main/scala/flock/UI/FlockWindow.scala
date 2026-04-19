package flock.UI

import flock.logic.{Boid, Constants, Vector2D}
import scalafx.geometry.Insets
import scalafx.scene.Group
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, Pane}
import scalafx.scene.shape.{Polygon, Rectangle}
import scalafx.scene.paint.Color

import scala.collection.mutable.ArrayBuffer

class FlockWindow(constants: Constants) extends Pane:
  // Frame
  minWidth = 800
  minHeight = 400
  background = new Background(Array(
               new BackgroundFill(Color.web("#0B0E14"), CornerRadii.Empty, Insets(0))
               ))
  style = "-fx-border-color: #4C566A; -fx-border-width: 1; -fx-border-style: solid;"
  // Boids
  private val boidShapes = ArrayBuffer[Polygon]()
  private val boidGroup = new Group()
  this.children.add(boidGroup)

  private def createBoidShapes(): Polygon =
    new Polygon:
      points ++= Seq(-6.0, -5.0, 6.0, 0.0, -6.0, 5.0)
      fill = Color.web("#4ECDC4")

  def sync(boids: Seq[Boid]): Unit =
    val diff = boids.size - boidShapes.size
    if diff > 0 then
      val newShapes = ArrayBuffer.fill(diff){createBoidShapes()}
      boidShapes ++= newShapes
      boidGroup.children.addAll(newShapes.map(_.delegate))
    else if diff < 0 then
      val count = -diff
      for (_ <- 1 to count) do
        if boidShapes.nonEmpty then
          val lastShape = boidShapes.remove(boidShapes.size - 1)
          boidGroup.children.remove(lastShape.delegate)

  def render(boids: Seq[Boid]): Unit =
    require(
    boids.size == boidShapes.size,
    s"render() called with ${boids.size} boids but ${boidShapes.size} shapes. Call sync() first"
    )
    boids.zip(boidShapes).foreach { (boid, shape) =>
      shape.translateX = boid.position.x
      shape.translateY = boid.position.y
      shape.rotate = math.toDegrees(math.atan2(boid.velocity.y, boid.velocity.x))
    }

  private val clipRectangle = new Rectangle()
  clipRectangle.width  <== this.width
  clipRectangle.height <== this.height
  this.clip = clipRectangle


end FlockWindow

