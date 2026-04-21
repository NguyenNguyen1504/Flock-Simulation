package flock.logic

/** A steering behavior computes a force vector that steers a boid toward a desired motion.
 *  Concrete implementations (Separation, Alignment, Cohesion) are combined in [[Flock.update]]
 *  by weighting and summing their outputs.
 */
trait SteeringBehavior:

  /** Computes a raw steering force for the given boid based on the rest of the flock.
   *
   *  @param boid     The boid being steered.
   *  @param allBoids Every boid in the flock, including the boid itself (filtered internally).
   *  @param c        Simulation constants.
   *  @return         A force vector (not yet weighted) to apply to the boid.
   */
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D

  /** Returns the weight that scales this behavior's force when combining multiple behaviors. */
  def weight(c: Constants): Double

  /** Returns the subset of boids that are within this boid's perception cone.
   *  A neighbor qualifies if it is:
   *  - not the boid itself,
   *  - within perceptionRadius (compared via squared distance to avoid square roots), and
   *  - within the half-angle of perceptionAngle relative to the boid's heading.
   */
  protected def perceptionNeighbors(boid: Boid, allBoids: Seq[Boid], c: Constants): Seq[Boid] =
    val rSquared = c.perceptionRadius * c.perceptionRadius
    val maxAngle = c.perceptionAngle / 2
    allBoids.filter(b =>
      b != boid &&
      b.distanceSquared(boid) <= rSquared &&
      boid.angle(b) <= maxAngle
    )


/** Steers a boid away from neighbors that are too close, preventing crowding.
 *
 *  For each neighbor within minSeparationDistance, a repulsion vector is computed
 *  pointing away from that neighbor and scaled inversely by the squared distance,
 *  so nearer neighbors produce a stronger push.
 */
object Separation extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val minDSquared = c.minSeparationDistance * c.minSeparationDistance
    val closeNeighbors = allBoids.filter(b => b != boid && b.distanceSquared(boid) <= minDSquared)
    if closeNeighbors.isEmpty then Vector2D(0, 0)
    else
      // Sum repulsion vectors: direction away from neighbor, weighted by 1/distanceSquared
      closeNeighbors.map(another =>
        val toAnother = boid.position - another.position
        toAnother * (1.0 / toAnother.magnitudeSquared())
      ).reduce(_ + _)

  def weight(c: Constants): Double = c.separationWeight
end Separation


/** Steers a boid toward the average velocity of its perceived neighbors, promoting coordinated movement. */
object Alignment extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val neighbors = perceptionNeighbors(boid, allBoids, c)
    if neighbors.isEmpty then Vector2D(0, 0)
    else
      // Compute average velocity, then return the error (desired - current) as the steering force
      val avgVelocity = neighbors.map(_.velocity).reduce(_ + _) * (1.0 / neighbors.size)
      avgVelocity - boid.velocity

  def weight(c: Constants): Double = c.alignmentWeight
end Alignment


/** Steers a boid toward the center of mass of its perceived neighbors, keeping the flock together. */
object Cohesion extends SteeringBehavior:
  def apply(boid: Boid, allBoids: Seq[Boid], c: Constants): Vector2D =
    val neighbors = perceptionNeighbors(boid, allBoids, c)
    if neighbors.isEmpty then Vector2D(0, 0)
    else
      // Compute the center of mass and return a vector pointing toward it
      val centerOfMass = neighbors.map(_.position).reduce(_ + _) * (1.0 / neighbors.size)
      centerOfMass - boid.position

  def weight(c: Constants): Double = c.cohesionWeight
end Cohesion