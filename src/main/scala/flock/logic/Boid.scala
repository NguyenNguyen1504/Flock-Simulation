package flock.logic

case class Boid(var position: Vector2D, var velocity: Vector2D):

  private var acceleration = Vector2D(0,0)
  private var steeringForce = Vector2D(0,0)

  def distanceSquared(another: Boid): Double =
    val toAnother = another.position - this.position
    toAnother.magnitudeSquared()

  def angle(another: Boid): Double =
    val heading = this.velocity
    val toAnother = another.position - this.position
    math.abs(heading.angle(toAnother))

  def applyForce(force: Vector2D): Unit =
    this.steeringForce += force

  def update(deltaTime: Double): Unit =
    // Applying simple vehicle model
    val limitedSteeringForce = this.steeringForce.truncate(Constants.maxSteeringForce)
    val newAcceleration = limitedSteeringForce * (1.0/Constants.mass)
    this.acceleration = newAcceleration
    this.velocity = (this.velocity + this.acceleration * deltaTime).truncate(Constants.maxSpeed)
    this.position += (this.velocity * deltaTime)
    // Reset acceleration and steering force to their default values, ready for next loop
    this.acceleration = Vector2D(0,0)
    this.steeringForce = Vector2D(0,0)
    
    // Wrap-around
    this.position = Vector2D(
      (this.position.x + Constants.worldWidth) % Constants.worldWidth,
      (this.position.y + Constants.worldHeight) % Constants.worldHeight
    )

end Boid





