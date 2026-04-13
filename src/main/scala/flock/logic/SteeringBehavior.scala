package flock.logic

object Separation:
  def calculate(target: Boid, allBoids: Seq[Boid], constants: Constants): Vector2D =
    // Take all surrounding boids, not only the ones in the perception angle
    val minDSquared = constants.minSeparationDistance * constants.minSeparationDistance
    val closeNeighbors = allBoids.filter(b => b != target && b.distanceSquared(target) <= minDSquared)

    // Avoid UnsupportedOperationException
    if closeNeighbors.isEmpty then Vector2D(0,0)
    else
      closeNeighbors.map(another => {
        // Calculates a repulsion vector inversely proportional to the squared distance
        val toAnother = target.position - another.position
        toAnother * (1.0 / toAnother.magnitudeSquared())
      }).reduce(_+_)
end Separation


object Alignment:
  def calculate(target: Boid, neighbors: Seq[Boid], constants: Constants): Vector2D =
    if neighbors.isEmpty then Vector2D(0,0)
    else
      val avgVelocity = neighbors.map(_.velocity).reduce(_+_) * (1.0 / neighbors.size)
      avgVelocity - target.velocity
end Alignment


object Cohesion:
  def calculate(target: Boid, neighbors: Seq[Boid], constants: Constants): Vector2D =
    if neighbors.isEmpty then Vector2D(0,0)
    else
      val centerOfMass = neighbors.map(_.position).reduce(_+_) * (1.0 / neighbors.size)
      centerOfMass - target.position
end Cohesion
