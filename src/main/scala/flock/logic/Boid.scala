package flock.logic

import io.circe.{Decoder, Encoder}

case class Boid(private var _position: Vector2D, private var _velocity: Vector2D):
  
  def position: Vector2D = this._position
  def velocity: Vector2D = this._velocity

  private var acceleration = Vector2D(0,0)
  private var steeringForce = Vector2D(0,0)

  def distanceSquared(another: Boid): Double =
    val toAnother = another._position - this._position
    toAnother.magnitudeSquared()

  def angle(another: Boid): Double =
    val heading = this._velocity
    val toAnother = another._position - this._position
    math.abs(heading.angle(toAnother))

  def applyForce(force: Vector2D): Unit =
    this.steeringForce += force

  def update(deltaTime: Double): Unit =
    // Applying simple vehicle model
    val limitedSteeringForce = this.steeringForce.truncate(Constants.maxSteeringForce)
    val newAcceleration = limitedSteeringForce * (1.0/Constants.mass)
    this.acceleration = newAcceleration
    this._velocity = (this._velocity + this.acceleration * deltaTime).truncate(Constants.maxSpeed)
    this._position += (this._velocity * deltaTime)
    // Reset acceleration and steering force to their default values, ready for next loop
    this.acceleration = Vector2D(0,0)
    this.steeringForce = Vector2D(0,0)
    
    // Wrap-around
    this._position = Vector2D(
      (this._position.x + Constants.worldWidth) % Constants.worldWidth,
      (this._position.y + Constants.worldHeight) % Constants.worldHeight
    )

end Boid

object Boid:
  given Decoder[Boid] = Decoder.forProduct2("position", "velocity")(Boid.apply)
  given Encoder[Boid] = Encoder.forProduct2("position", "velocity")(b => (b.position, b.velocity))




