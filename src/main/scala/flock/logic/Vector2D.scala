package flock.logic
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import scala.math.{acos, hypot}

/** A two-dimensional vector with standard arithmetic and geometric operations.
 *  Used throughout the simulation to represent positions, velocities, and forces.
 */
case class Vector2D(val x: Double, val y: Double):

  /** Returns the vector sum of this and another vector. */
  def +(another: Vector2D): Vector2D =
    Vector2D(this.x + another.x, this.y + another.y)

  /** Returns the vector difference of this minus another vector. */
  def -(another: Vector2D): Vector2D =
    Vector2D(this.x - another.x, this.y - another.y)

  /** Returns this vector scaled by a scalar factor. */
  def *(factor: Double): Vector2D =
    Vector2D(this.x * factor, this.y * factor)

  /** Returns the Euclidean length of this vector. */
  def magnitude(): Double = hypot(this.x, this.y)

  /** Returns a unit vector in the same direction, or the zero vector if magnitude is zero. */
  def normalize(): Vector2D =
    val m = this.magnitude()
    if m > 0 then
      this * (1 / m)
    else
      this

  /** Returns this vector truncated to a maximum length.
   *  If the vector's magnitude exceeds maxLength, it is scaled down proportionally.
   */
  def truncate(maxLength: Double): Vector2D =
    val m = this.magnitude()
    if m > maxLength then
      val coefficient = maxLength / m
      this * coefficient
    else
      this

  /** Returns the dot product of this and another vector. */
  def dot(another: Vector2D): Double =
    this.x * another.x + this.y * another.y

  /** Returns the angle in radians between this and another vector.
   *  Returns 0 if either vector is effectively zero (magnitude < 1e-10) to avoid division by zero.
   *  The result is clamped to [-1, 1] before applying acos to guard against floating-point errors.
   */
  def angle(another: Vector2D): Double =
    val m = this.magnitude() * another.magnitude()
    if m < 1e-10 then
      0
    else
      val cosAngle = (this.dot(another) / m).max(-1.0).min(1.0)
      acos(cosAngle)

  /** Returns the squared Euclidean length. Prefer this over magnitude() for distance comparisons
   *  to avoid the cost of a square root.
   */
  def magnitudeSquared(): Double =
    this.x * this.x + this.y * this.y

end Vector2D

/** Companion object providing JSON codec instances for Vector2D via Circe. */
object Vector2D:
  given Decoder[Vector2D] = deriveDecoder[Vector2D]
  given Encoder[Vector2D] = deriveEncoder[Vector2D]