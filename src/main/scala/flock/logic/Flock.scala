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
        val angle = target.angle(boid)
        if dSquared <= rSquared && angle <= maxAngle then
          neighbors += boid

    neighbors


  def calculateSeparation(target: Boid): Vector2D =
    def takeSeparateVector(another: Boid): Vector2D =     // Calculates a repulsion vector inversely proportional to the squared distance
      val toAnother = target.position - another.position
      val r = toAnother.magnitudeSquared()
      toAnother * (1.0 / r)

    // Take all surrounding boids, not only the ones in the perception angle
    val minDSquared = Constants.minSeparationDistance * Constants.minSeparationDistance
    val closeNeighbors = boids.filter(b => b != target && b.distanceSquared(target) <= minDSquared)

    // Avoid UnsupportedOperationException
    if closeNeighbors.isEmpty then
      Vector2D(0,0)
    else
      closeNeighbors.map(takeSeparateVector(_)).reduce(_+_)


  def calculateAlignment(target: Boid): Vector2D =
    val neighbors = findNeighbors(target)
    val numberOfNeighbors = neighbors.size
    if numberOfNeighbors == 0 then
      Vector2D(0,0)
    else
      val avgVelocity = (neighbors.map(_.velocity).reduce(_+_)) * (1.0/numberOfNeighbors)
      avgVelocity - target.velocity


  def calculateCohesion(target: Boid): Vector2D =
    val neighbors = findNeighbors(target)
    val numberOfNeighbors = neighbors.size
    if numberOfNeighbors == 0 then
      Vector2D(0,0)
    else
      val centerOfMass = (neighbors.map(_.position).reduce(_+_)) * (1.0/numberOfNeighbors)
      centerOfMass - target.position


  def update(deltaTime: Double): Unit =
    for boid <- this.boids do
      val s = calculateSeparation(boid)  // Separation weight calculated based on neighborhood
      val a = calculateAlignment(boid)   // Alignment weight calculated based on neighborhood
      val c = calculateCohesion(boid)    // Cohesion weight calculated based on neighborhood
      val steeringForce = s * Constants.separationWeight + a * Constants.alignmentWeight + c * Constants.cohesionWeight
      boid.applyForce(steeringForce)
    for boid <- this.boids do
      boid.update(deltaTime)

end Flock