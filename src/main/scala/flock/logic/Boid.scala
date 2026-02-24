package flock.logic

class Boid(position: Vector2D, velocity: Vector2D, acceleration: Vector2D):

  var pos = this.position
  var v = this.velocity
  var a = this.acceleration

  def distanceSquared(another: Boid): Double =
    val vector = another.pos - this.pos
    vector.magnitudeSquared()
    
  def apply(force: Vector2D): Unit = ???
  
  def update(deltaTime: Double): Unit = ???

end Boid





