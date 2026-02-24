package flock.logic
import scala.collection.mutable.ArrayBuffer

class Flock(val boids: ArrayBuffer[Boid]):

  def addBoid(boid: Boid): Unit =
    this.boids += boid

  def removeBoid(boid: Boid): Unit =
    this.boids -= boid

  def findNeighbors(boid: Boid): ArrayBuffer[Boid] = ???

  def calculateSeparation(target: Boid): Vector2D = ???

  def calculateAlignment(target: Boid): Vector2D = ???

  def calculateCohesion(target: Boid): Vector2D = ???
  
  def update(deltaTime: Double): Unit = ???

end Flock