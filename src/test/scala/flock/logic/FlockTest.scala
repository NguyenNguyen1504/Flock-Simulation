package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable.ArrayBuffer

class FlockTest extends AnyFlatSpec with Matchers:

  "Flock" should "correctly add a new boid" in {
    val boid = Boid(Vector2D(1, 2), Vector2D(3, 4))
    val flock = new Flock(ArrayBuffer.empty)
    flock.addBoid(boid)
    flock.boids.exists(b => b.position == boid.position) shouldBe true
  }

  it should "correctly remove an existing boid" in {
    val boid = Boid(Vector2D(1, 2), Vector2D(3, 4))
    val flock = new Flock(ArrayBuffer(boid))
    flock.removeBoid(boid)
    flock.boids shouldBe empty
  }

  "SteeringBehaviors" should "correctly calculate separation" in {
    val c = Constants.default.copy(minSeparationDistance = 50.0)
    val target = Boid(Vector2D(0, 0), Vector2D(1, 0))
    val boidA = Boid(Vector2D(20, 0), Vector2D(0, 0))
    val boidB = Boid(Vector2D(0, 10), Vector2D(0, 0))

    val result = Separation.apply(target, Seq(target, boidA, boidB), c)
    result.y should be < 0.0
  }

  it should "correctly calculate alignment" in {
    val c = Constants.default.copy(perceptionRadius = 100.0, perceptionAngle = 360.toRadians)
    val target = Boid(Vector2D(0, 0), Vector2D(10, 0))
    val neighbor = Boid(Vector2D(20, 0), Vector2D(10, 20))

    val result = Alignment.apply(target, Seq(target, neighbor), c)
    result.x shouldBe 0.0 +- 0.0001
    result.y shouldBe 20.0 +- 0.0001
  }

  it should "correctly calculate cohesion" in {
    val c = Constants.default.copy(perceptionRadius = 100.0, perceptionAngle = 360.toRadians)
    val target = Boid(Vector2D(10, 10), Vector2D(1, 0))
    val neighbor = Boid(Vector2D(20, 10), Vector2D(0, 0))

    val result = Cohesion.apply(target, Seq(target, neighbor), c)
    result.x shouldBe 10.0 +- 0.0001
    result.y shouldBe 0.0 +- 0.0001
  }

  "Flock.update" should "move boids correctly based on velocity" in {
    val boid = Boid(Vector2D(10, 10), Vector2D(5, -2))
    val flock = new Flock(ArrayBuffer(boid))
    flock.updateConstants(flock.constants.copy(maxSteeringForce = 0.0))

    flock.update(2.0)
    flock.boids.head.position.x shouldBe 20.0
    flock.boids.head.position.y shouldBe 6.0
  }

  it should "strictly limit velocity to Constants.maxSpeed" in {
    val boid = Boid(Vector2D(0, 0), Vector2D(10, 10))
    val flock = new Flock(ArrayBuffer(boid))
    val maxS = 5.0
    flock.updateConstants(flock.constants.copy(maxSpeed = maxS))

    flock.update(0.1)
    flock.boids.head.velocity.magnitude() should be <= (maxS + 1e-9)
  }

  it should "handle edge case: multiple boids at the exact same position" in {
    val b1 = Boid(Vector2D(10, 10), Vector2D(0, 0))
    val b2 = Boid(Vector2D(10, 10), Vector2D(0, 0))
    val flock = new Flock(ArrayBuffer(b1, b2))

    noException should be thrownBy flock.update(1.0)
  }

  "Flock management" should "increase the flock size by n" in {
    val flock = new Flock(ArrayBuffer.empty)
    flock.addRandomBoids(10)
    flock.boids.size shouldBe 10
  }

  it should "not exceed Constants.maxFlockSize" in {
    val c = Constants.default.copy(maxFlockSize = 200)
    val flock = new Flock(ArrayBuffer.fill(198)(Boid(Vector2D(0, 0), Vector2D(1, 1))), _constants = c)
    flock.addRandomBoids(5)
    flock.boids.size shouldBe 200
  }

  it should "not remove boids below Constants.minFlockSize" in {
    val c = Constants.default.copy(minFlockSize = 10)
    val flock = new Flock(ArrayBuffer.fill(15)(Boid(Vector2D(0, 0), Vector2D(1, 1))), _constants = c)
    flock.removeRandomBoids(10)
    flock.boids.size shouldBe 10
  }

  "Flock.resetWith" should "replace all current boids with a new set" in {
    val flock = new Flock(ArrayBuffer(Boid(Vector2D(10, 10), Vector2D(1, 1))))
    val newBoids = Seq(Boid(Vector2D(100, 100), Vector2D(2, 2)))
    flock.resetWith(newBoids)
    flock.boids.size shouldBe 1
    flock.boids.head.position shouldBe Vector2D(100, 100)
  }

end FlockTest