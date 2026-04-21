package flock.logic

import io.circe.{Decoder, Encoder}

/** A single boid in the flock simulation.
 *
 *  Each boid maintains a position and velocity, accumulates steering forces during a simulation
 *  step, and updates its state via a simple vehicle model. Position and velocity are exposed
 *  read-only; mutation happens only through [[applyForce]] and [[update]].
 *
 *  @param _position Initial position of the boid.
 *  @param _velocity Initial velocity of the boid.
 */
case class Boid(private var _position: Vector2D, private var _velocity: Vector2D):

  def position: Vector2D = this._position
  def velocity: Vector2D = this._velocity

  // Internal state reset each tick after update.
  private var acceleration = Vector2D(0, 0)
  private var steeringForce = Vector2D(0, 0)

  /** Returns the squared Euclidean distance between this boid and another.
   *  Using the squared distance avoids a square root and is sufficient for comparisons.
   */
  def distanceSquared(another: Boid): Double =
    val toAnother = another._position - this._position
    toAnother.magnitudeSquared()

  /** Returns the absolute angle (radians) from this boid's heading toward another boid.
   *  Used by steering behaviors to determine whether another boid is within the field of view.
   */
  def angle(another: Boid): Double =
    val heading = this._velocity
    val toAnother = another._position - this._position
    math.abs(heading.angle(toAnother))

  /** Accumulates a steering force to be applied on the next [[update]] call.
   *  Multiple forces from different steering behaviors are summed before integration.
   */
  def applyForce(force: Vector2D): Unit =
    this.steeringForce += force

  /** Wraps the boid's position to stay within the world bounds using modular arithmetic,
   *  so boids that exit one edge re-enter from the opposite edge.
   */
  def wrapAround(constants: Constants): Unit =
    val width = constants.worldWidth
    val height = constants.worldHeight
    val wX = (this._position.x % width + width) % width
    val wY = (this._position.y % height + height) % height
    this._position = Vector2D(wX, wY)

  /** Advances the boid's state by one time step using a simple vehicle model:
   *  1. Truncate the accumulated steering force to maxSteeringForce.
   *  2. Derive acceleration via Newton's second law (a = F / mass).
   *  3. Integrate velocity (truncated to maxSpeed), then integrate position.
   *  4. Reset acceleration and steering force for the next tick.
   *  5. Wrap position around world boundaries.
   *
   *  @param deltaTime Elapsed time in seconds since the last update.
   *  @param constants Simulation parameters used for truncating and world size.
   */
  def update(deltaTime: Double, constants: Constants): Unit =
    val limitedSteeringForce = this.steeringForce.truncate(constants.maxSteeringForce)
    val newAcceleration = limitedSteeringForce * (1.0 / constants.mass)
    this.acceleration = newAcceleration
    this._velocity = (this._velocity + this.acceleration * deltaTime).truncate(constants.maxSpeed)
    this._position += (this._velocity * deltaTime)

    // Reset acceleration and steering force to their default values, ready for next loop
    this.acceleration = Vector2D(0, 0)
    this.steeringForce = Vector2D(0, 0)

    this.wrapAround(constants)

end Boid

/** Companion object providing JSON codec instances for Boid via Circe.
 *  Only position and velocity are serialized; transient fields (acceleration, steeringForce)
 *  are excluded.
 */
object Boid:
  given Decoder[Boid] = Decoder.forProduct2("position", "velocity")(Boid.apply)
  given Encoder[Boid] = Encoder.forProduct2("position", "velocity")(b => (b.position, b.velocity))