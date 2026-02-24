package flock.logic

class Boid(var position: Vector2D, var velocity: Vector2D):

  private var acceleration = Vector2D(0,0)

  def distanceSquared(another: Boid): Double =
    val toAnother = another.position - this.position
    toAnother.magnitudeSquared()

  def angle(another: Boid): Double =
    val heading = this.velocity
    val toAnother = another.position - this.position
    heading.angle(toAnother)

  def apply(force: Vector2D): Unit = ???

  def update(deltaTime: Double): Unit = ???

end Boid





