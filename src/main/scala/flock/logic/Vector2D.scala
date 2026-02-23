package flock.logic
import scala.math.{acos, hypot}
case class Vector2D(val x: Double, val y: Double):

  def +(another: Vector2D): Vector2D =
    Vector2D(this.x + another.x, this.y + another.y)

  def -(another: Vector2D): Vector2D =
    Vector2D(this.x - another.x, this.y - another.y)

  def *(factor: Double): Vector2D =
    Vector2D(this.x * factor, this.y * factor)

  def magnitude(): Double = hypot(this.x, this.y)

  def normalize(): Vector2D =
    val m = this.magnitude()
    if m > 0 then
      this * (1 / this.magnitude())
    else
      this

  def truncate(maxLength: Double): Vector2D =
    val m = this.magnitude()
    if m > maxLength then
      val coefficient = maxLength / this.magnitude()
      this * coefficient
    else
      this

  def dot(another: Vector2D): Double =
    this.x * another.x + this.y * another.y

  def angle(another: Vector2D): Double =
    val m = this.magnitude() * another.magnitude()
    val cosAngle = ((this.dot(another)) / m).max(-1.0).min(1.0)
    acos(cosAngle)

  def magnitudeSquared(): Double =    // For faster calculation
    this.x * this.x + this.y * this.y

end Vector2D

