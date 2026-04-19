package flock.logic

trait SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D
  def weight(c: Constants): Double 

  protected def perceptionNeighbors(boid: Boid, allBoids: Seq[Boid], c: Constants): Seq[Boid] =
    val rSquared = c.perceptionRadius * c.perceptionRadius
    val maxAngle = c.perceptionAngle / 2
    allBoids.filter(b =>
      b != boid &&
      b.distanceSquared(boid) <= rSquared &&
      boid.angle(b) <= maxAngle
    )

object Separation extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val minDSquared = c.minSeparationDistance * c.minSeparationDistance
    val closeNeighbors = allBoids.filter(b => b != boid && b.distanceSquared(boid) <= minDSquared)
    if closeNeighbors.isEmpty then Vector2D(0, 0)
    else
      closeNeighbors.map(another =>
        val toAnother = boid.position - another.position
        toAnother * (1.0 / toAnother.magnitudeSquared())
      ).reduce(_ + _)
      
  def weight(c: Constants): Double = c.separationWeight
end Separation



object Alignment extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val neighbors = perceptionNeighbors(boid, allBoids, c)
    if neighbors.isEmpty then Vector2D(0, 0)
    else
      val avgVelocity = neighbors.map(_.velocity).reduce(_ + _) * (1.0 / neighbors.size)
      avgVelocity - boid.velocity
      
  def weight(c: Constants): Double = c.alignmentWeight
end Alignment


object Cohesion extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val neighbors = perceptionNeighbors(boid, allBoids, c)
    if neighbors.isEmpty then Vector2D(0, 0)
    else
      val centerOfMass = neighbors.map(_.position).reduce(_ + _) * (1.0 / neighbors.size)
      centerOfMass - boid.position
      
  def weight(c: Constants): Double = c.cohesionWeight
end Cohesion
