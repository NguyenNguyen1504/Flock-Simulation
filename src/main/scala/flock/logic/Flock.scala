package flock.logic
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/** Companion object for [[Flock]], providing the default set of steering behaviors. */
object Flock:
  val defaultBehaviors: Seq[SteeringBehavior] = Seq(Separation, Alignment, Cohesion)

/** Manages a collection of boids and advances the simulation each tick.
 *
 *  On each call to [[update]], every boid's steering forces are computed by all registered
 *  behaviors (weighted and summed), then each boid integrates its own state. Boids are
 *  stored in a mutable buffer; external callers receive defensive copies via [[boids]].
 *
 *  @param _boids     Initial set of boids.
 *  @param behaviors  Steering behaviors applied every tick (defaults to Separation, Alignment, Cohesion).
 *  @param _constants Simulation parameters; can be updated at runtime via [[updateConstants]].
 */
class Flock(
  private val _boids: ArrayBuffer[Boid],
  private val behaviors: Seq[SteeringBehavior] = Flock.defaultBehaviors,
  private var _constants: Constants = Constants.default
):

  /** Returns a snapshot of all boids as immutable copies, so callers cannot mutate internal state. */
  def boids: Seq[Boid] = this._boids.map(b => Boid(b.position, b.velocity)).toSeq

  /** Appends a boid to the flock. No size check is performed here; use [[addRandomBoids]] for
   *  capped additions.
   */
  def addBoid(boid: Boid): Unit =
    this._boids += boid

  /** Removes the first occurrence of the given boid from the flock. */
  def removeBoid(boid: Boid): Unit =
    this._boids -= boid

  /** Spawns a single new boid at a random position on one of the four world edges,
   *  with an initial velocity pointing inward at maxSpeed.
   */
  def addRandomBoid(): Unit =
    val side = Random.nextInt(4) // 0: Top, 1: Right, 2: Bottom, 3: Left
    var x, y = 0.0
    var vx, vy = 0.0
    val width = this._constants.worldWidth
    val height = this._constants.worldHeight

    side match
      case 0 => // Top
        x = Random.nextDouble() * width
        y = 0
        vy = 1.0
      case 1 => // Right
        x = width
        y = Random.nextDouble() * height
        vx = -1.0
      case 2 => // Bottom
        x = Random.nextDouble() * width
        y = height
        vy = -1.0
      case 3 => // Left
        x = 0
        y = Random.nextDouble() * height
        vx = 1.0

    val randomPos = Vector2D(x, y)
    val randomVel = Vector2D(vx, vy).normalize() * this._constants.maxSpeed

    this.addBoid(Boid(randomPos, randomVel))

  /** Removes one randomly selected boid from the flock. Does nothing if the flock is empty. */
  def removeRandomBoid(): Unit =
    if this._boids.nonEmpty then
      val randomIndex = Random.nextInt(this._boids.size)
      this.removeBoid(this._boids(randomIndex))

  /** Adds up to n random boids, capped at maxFlockSize. */
  def addRandomBoids(n: Int): Unit =
    val currentCount = this._boids.size
    val spaceLeft = this._constants.maxFlockSize - currentCount
    val amountToAdd = if n > spaceLeft then spaceLeft else n
    for _ <- 1 to amountToAdd do
      addRandomBoid()

  /** Removes up to n random boids, but never below minFlockSize. */
  def removeRandomBoids(n: Int): Unit =
    val removableAmount = this._boids.size - this._constants.minFlockSize
    val amountToRemove = if n > removableAmount then removableAmount else n
    for _ <- 1 to amountToRemove do
      removeRandomBoid()

  /** Replaces the entire flock with the given boids, wrapping each one into the world bounds.
   *  Useful for loading a saved state.
   */
  def resetWith(newBoids: Seq[Boid]): Unit =
    this._boids.clear()
    this._boids ++= newBoids.map { b =>
      val newBoid = Boid(b.position, b.velocity)
      newBoid.wrapAround(this._constants)
      newBoid
    }

  /** Advances the simulation by one time step.
   *
   *  Two passes are made deliberately:
   *  1. Force pass: for each boid, all behaviors compute and accumulate forces using the
   *     positions/velocities from the start of this tick (preventing order-dependent artifacts).
   *  2. Integration pass: each boid applies its accumulated force and updates its state.
   *
   *  @param deltaTime Elapsed time in seconds since the last update.
   */
  def update(deltaTime: Double): Unit =
    val allBoids = this._boids.toSeq
    // Pass 1: accumulate weighted steering forces from all behaviors
    for boid <- this._boids do
      val totalForce = this.behaviors.map(behavior =>
          val force = behavior.apply(boid, allBoids, this._constants)
          val weight = behavior.weight(this._constants)
          force * weight
        ).reduce(_ + _)
      boid.applyForce(totalForce)
    // Pass 2: integrate each boid's state
    for boid <- this._boids do
      boid.update(deltaTime, this._constants)

  /** Returns the current simulation constants. */
  def constants: Constants = this._constants

  /** Replaces the simulation constants after validating key invariants.
   *
   *  @throws IllegalArgumentException if maxSpeed is not positive, or if minFlockSize >= maxFlockSize.
   */
  def updateConstants(newConstants: Constants): Unit =
    require(newConstants.maxSpeed > 0, "maxSpeed must be positive")
    require(newConstants.minFlockSize < newConstants.maxFlockSize)
    this._constants = newConstants

  /** Convenience method to update only the separation behavior weight. */
  def updateSeparationWeight(w: Double): Unit = this.updateConstants(this._constants.copy(separationWeight = w))

  /** Convenience method to update only the alignment behavior weight. */
  def updateAlignmentWeight(w: Double): Unit  = this.updateConstants(this._constants.copy(alignmentWeight  = w))

  /** Convenience method to update only the cohesion behavior weight. */
  def updateCohesionWeight(w: Double): Unit   = this.updateConstants(this._constants.copy(cohesionWeight   = w))

  /** Resizes the world and adjusts all boids to the new dimensions. */
  def updateWorldSize(w: Double, h: Double): Unit = this.updateConstants(this._constants.copy(worldWidth = w, worldHeight = h))

  /** Grows or shrinks the flock to exactly newSize boids by adding or removing random boids.
   *  The result is still subject to minFlockSize / maxFlockSize limits.
   */
  def setSize(newSize: Int): Unit =
    val currentSize = this._boids.size
    if newSize > currentSize then
      this.addRandomBoids(newSize - currentSize)
    else if newSize < currentSize then
      this.removeRandomBoids(currentSize - newSize)

end Flock