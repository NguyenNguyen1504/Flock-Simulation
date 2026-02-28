package flock.logic
import scala.collection.mutable.ArrayBuffer

class Flock(val boids: ArrayBuffer[Boid]):

  def addBoid(boid: Boid): Unit =
    this.boids += boid

  def removeBoid(boid: Boid): Unit =
    this.boids -= boid

  def findNeighbors(target: Boid): ArrayBuffer[Boid] =
    val neighbors = ArrayBuffer[Boid]()
    val rSquared = Constants.perceptionRadius * Constants.perceptionRadius
    val maxAngle = Constants.perceptionAngle / 2

    for boid <- this.boids do
      if boid != target then
        val dSquared = boid.distanceSquared(target)
        val angle = boid.angle(target)
        if dSquared <= rSquared && angle <= maxAngle then
          neighbors += boid

    neighbors


  def calculateSeparation(target: Boid): Vector2D = ???

  def calculateAlignment(target: Boid): Vector2D = ???

  def calculateCohesion(target: Boid): Vector2D = ???
  
  def update(deltaTime: Double): Unit = ???

end Flock