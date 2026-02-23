package flock.logic

class Boid(position: Vector2D, velocity: Vector2D, acceleration: Vector2D):

  var pos = this.position
  var v = this.velocity
  var a = this.acceleration

  def apply(force: Vector2D): Unit = ???
  def update(deltaTime: Double): Unit = ???

end Boid





