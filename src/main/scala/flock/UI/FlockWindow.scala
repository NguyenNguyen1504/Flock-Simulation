package flock.UI

import flock.logic.{Boid, Vector2D}
import scalafx.geometry.Insets
import scalafx.scene.Group
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, Pane}
import scalafx.scene.shape.Polygon
import scalafx.scene.paint.Color

import scala.collection.mutable.ArrayBuffer

class FlockWindow extends Pane:
  // Frame
  minWidth = 800
  minHeight = 400
  maxHeight = 400
  background = new Background(Array(
               new BackgroundFill(Color.color(0.871, 0.871, 0.847), CornerRadii.Empty, Insets(0))
               ))
  // Boids
  private val boidShapes = ArrayBuffer[Polygon]()
  private val boidGroup = new Group()
  this.children.add(boidGroup)

  def createBoidShapes(): Polygon =
    new Polygon:
      points ++= Seq(0.0, 0.0, 12.0, 5.0, 0.0, 10.0)
      fill = Color.White

  def sync(boids: ArrayBuffer[Boid]): Unit =
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

  def render(boids: ArrayBuffer[Boid]): Unit =
    boids.zip(boidShapes).foreach { (boid, shape) =>
      shape.translateX = boid.position.x
      shape.translateY = boid.position.y
      shape.rotate = math.toDegrees(boid.velocity.angle(Vector2D(1,0)))
    }


end FlockWindow

