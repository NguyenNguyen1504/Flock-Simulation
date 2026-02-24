package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BoidTest extends AnyFlatSpec with Matchers:

  "A Boid" should "correctly calculate the squared distance to another boid" in {
    val boid1 = Boid(Vector2D(4,3), Vector2D(3,3))
    val boid2 = Boid(Vector2D(0,0), Vector2D(2,1))
    boid1.distanceSquared(boid2) shouldBe 25
  }
  it should "return 0 when calculating the squared distance of 2 boids at the same position" in {
    val boid1 = Boid(Vector2D(6,3), Vector2D(3,3))
    val boid2 = Boid(Vector2D(6,3), Vector2D(1,2))
    boid1.distanceSquared(boid2) shouldBe 0
  }
  it should "correctly calculate the view angle to another boid" in {
    val boid1 = Boid(Vector2D(4,3), Vector2D(1,1))
    val boid2 = Boid(Vector2D(5,3), Vector2D(2,1))
    boid1.angle(boid2) should be ((45.toRadians: Double) +- 0.0001)
  }
  it should "return a large angle for a boid behind it" in {
  val boid1 = Boid(Vector2D(4, 3), Vector2D(1, 0))
  val boid2 = Boid(Vector2D(3, 3), Vector2D(0, 0))
  boid1.angle(boid2) should be ((180.toRadians: Double) +- 0.0001)
  }
  it should "return 0 when calculating the view angle to a boid in its flying direction" in {
    val boid1 = Boid(Vector2D(4,3), Vector2D(1,1))
    val boid2 = Boid(Vector2D(5,4), Vector2D(2,1))
    boid1.angle(boid2) shouldBe ((0: Double) +- 0.0001)
  }
  it should "limit its velocity to maxSpeed in update" in {
    val boid = new Boid(Vector2D(0, 0), Vector2D(10, 0))
    boid.update(0.1) // deltaTime
    boid.velocity.magnitude() should be <= Constants.maxSpeed
  }
  it should "correctly update velocity and position when a force is applied" in {
    // Assume mass = 1.0, deltaTime = 1.0 for easy calculating
    val initialPos = Vector2D(0, 0)
    val initialVel = Vector2D(1, 0)
    val boid = Boid(initialPos, initialVel)

    val force = Vector2D(2, 0)
    boid.applyForce(force)

    // Step 1: force = truncate(2, 0.15) = 0.15, mass = 1 => acc = 0.15
    // Step 2: v_new = v_old + a * dt = 1 + 0.15 * 1.0 = 1.15
    // Step 3: p_new = p_old + v_new * dt = 0 + 1.15 * 1.0 = 1.15
    boid.update(1.0)

    boid.velocity.x shouldBe (1.15 +- 0.001)
    boid.position.x shouldBe (1.15 +- 0.001)
  }
  it should "reset steering force to zero after update is called" in {
  val boid = Boid(Vector2D(0, 0), Vector2D(1, 0))
  boid.applyForce(Vector2D(1, 1))
  boid.update(0.1)

  // Indirect testing: calling update without applying a new force
  // Speed should not change because acceleration is now 0
  val velAfterFirstUpdate = boid.velocity
  boid.update(0.1)
  boid.velocity shouldBe velAfterFirstUpdate
  }

  it should "obey the maxForce limit even if a huge force is applied" in {
    val boid = Boid(Vector2D(0, 0), Vector2D(0, 0))
    val hugeForce = Vector2D(9999, 0)

    boid.applyForce(hugeForce)
    boid.update(1.0)

    // acceleration <= maxForce / mass
    // v_new <= maxSpeed (if v_old = 0 and mass = 1)
    boid.velocity.magnitude() should be <= Constants.maxSpeed
  }
  it should "not change position or velocity if deltaTime is zero" in {
  val boid = Boid(Vector2D(5, 5), Vector2D(2, 2))
  boid.applyForce(Vector2D(1, 1))
  boid.update(0.0)

  boid.position shouldBe Vector2D(5, 5)
  boid.velocity shouldBe Vector2D(2, 2)
  }

end BoidTest