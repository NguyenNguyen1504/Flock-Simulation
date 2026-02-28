package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Vector2DTest extends AnyFlatSpec with Matchers:

  "Vector2D addition" should "correctly add Vector2Ds" in {
    val a = Vector2D(1.2, 3.4)
    val b = Vector2D(-3, 0)
    (a + b).x shouldBe -1.8 +- 0.0001
    (a + b).y shouldBe 3.4 +- 0.0001
  }

  it should "return the same vector when adding a zero vector" in {
    val a = Vector2D(1.2, 3.4)
    val b = Vector2D(0, 0)
    (a + b) shouldBe Vector2D(1.2, 3.4)
  }

  "Vector2D substraction" should "correctly subtract Vector2Ds" in {
    val a = Vector2D(1.2, 3.4)
    val b = Vector2D(-3, 0)
    (a - b).x shouldBe 4.2 +- 0.0001
    (a - b).y shouldBe 3.4 +- 0.0001
  }

  "Vector2D multiplication" should "scale the vector correctly" in {
    val a = Vector2D(1.5, -2.0)
    (a * 2.0) shouldBe Vector2D(3.0, -4.0)
    (a * 0.5) shouldBe Vector2D(0.75, -1.0)
  }

  "Vector2D dot production" should "correctly dot product Vector2Ds" in {
    val a = Vector2D(1.2, 3.4)
    val b = Vector2D(-3, 0)
    a.dot(b) shouldBe -3.6 +- 0.0001
  }

  "A Vector2D" should "correctly calculate its magnitude and squared magnitude" in {
    val a = Vector2D(3.0, 4.0)
    a.magnitude() shouldBe 5.0 +- 0.0001
    a.magnitudeSquared() shouldBe 25.0 +- 0.0001
  }

  "Vector2D normalization" should "return the normalized vector" in {
    val a = Vector2D(3.0, 4.0)
    val normalized = a.normalize()
    normalized.x shouldBe 0.6 +- 0.0001
    normalized.y shouldBe 0.8 +- 0.0001
    normalized.magnitude() shouldBe 1.0 +- 0.0001
  }

  it should "work correctly with negative coordinates" in {
    val a = Vector2D(-3.0, -4.0)
    val normalized = a.normalize()
    normalized.x shouldBe -0.6 +- 0.0001
    normalized.y shouldBe -0.8 +- 0.0001
  }

  it should "return the same vector if it is a zero vector" in {
    val a = Vector2D(0, 0)
    a.normalize() shouldBe a
  }

  "Vector2D truncation" should "limit the magnitude if it exceeds maxLength" in {
    val a = Vector2D(10.0, 0.0)
    val truncated = a.truncate(5.0)
    truncated.magnitude() shouldBe 5.0 +- 0.0001
    truncated.x shouldBe 5.0 +- 0.0001
  }

  it should "not change the vector if its magnitude is already smaller than maxLength" in {
    val a = Vector2D(3.0, 4.0) // magnitude là 5.0
    a.truncate(10.0) shouldBe a
  }

  "Vector2D angle" should "calculate the correct angle between vectors in radians" in {
    val a = Vector2D(1, 0)
    val b = Vector2D(0, 1)
    a.angle(b) shouldBe (scala.math.Pi / 2) +- 0.0001

    val c = Vector2D(1, 1)
    a.angle(c) shouldBe (scala.math.Pi / 4) +- 0.0001
  }

  it should "return 0 for the same vector" in {
    val a = Vector2D(1.2, 3.4)
    a.angle(a) shouldBe 0.0 +- 0.0001
  }
  it should "return 0 when one or both vectors are zero vectors" in {
    val zero = Vector2D(0, 0)
    val a = Vector2D(5, 5)

    zero.angle(a) shouldBe 0.0
    a.angle(zero) shouldBe 0.0
    zero.angle(zero) shouldBe 0.0
  }

end Vector2DTest