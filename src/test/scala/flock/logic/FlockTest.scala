package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ArrayBuffer

class FlockTest extends AnyFlatSpec with Matchers:

  "Flock" should "correctly add a new boid" in {
    val boid0 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid1 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid2 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid3 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid4 = Boid(Vector2D(1,2), Vector2D(3,4))
    val flock = Flock(ArrayBuffer(boid0, boid1, boid2, boid3))
    flock.addBoid(boid4)
    flock.boids.contains(boid4) shouldBe true
  }
  it should "correctly remove an existing boid" in {
    val boid0 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid1 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid2 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid3 = Boid(Vector2D(0,0), Vector2D(1,1))
    val boid4 = Boid(Vector2D(1,2), Vector2D(3,4))
    val flock = Flock(ArrayBuffer(boid0, boid1, boid2, boid3, boid4))
    flock.removeBoid(boid4)
    flock.boids.contains(boid4) shouldBe false
  }
  it should "correctly return the correct boids in the neighborhood of the target boid" in {
    Constants.perceptionRadius = 75.0
    Constants.perceptionAngle = 120.toRadians
    val target = Boid(Vector2D(0,0),   Vector2D(1,0))
    val boid1  = Boid(Vector2D(30,0),  Vector2D(0,0))
    val boid2  = Boid(Vector2D(80,0),  Vector2D(0,0))
    val boid3  = Boid(Vector2D(-10,0), Vector2D(0,0))
    val boid4  = Boid(Vector2D(40,40), Vector2D(0,0))
    val flock  = Flock(ArrayBuffer(target, boid1, boid2, boid3, boid4))
    val neighbors = flock.findNeighbors(target)
    neighbors should contain theSameElementsAs ArrayBuffer(boid1, boid4)
    neighbors should not contain boid2
    neighbors should not contain boid3
  }
  it should "return an empty list if the flock only contains the target boid" in {
    Constants.perceptionRadius = 75.0
    Constants.perceptionAngle = 120.toRadians
    val target = Boid(Vector2D(0,0), Vector2D(1,1))
    val flock = Flock(ArrayBuffer(target))
    flock.findNeighbors(target) shouldBe empty
  }
  "Flock.calculateSeparation" should "correctly calculate the separation weight from the neighborhood of the target boid" in {
    Constants.perceptionRadius = 100.0
    Constants.perceptionAngle = 140.toRadians
    Constants.minSeparationDistance = 50.0

    val target = Boid(Vector2D(0, 0), Vector2D(1, 0))

    val boidA = Boid(Vector2D(20, 0),  Vector2D(0, 0))
    val boidB = Boid(Vector2D(0, 10),  Vector2D(0, 0))
    val boidC = Boid(Vector2D(-10, -10), Vector2D(0, 0)) // r=5 but is behind the target boid -> not counted

    val flock = Flock(ArrayBuffer(target, boidA, boidB, boidC))
    val result = flock.calculateSeparation(target)

    result.x should be (0.0 +- 0.0001)
    result.y should be (-0.05 +- 0.0001)
  }
  it should "return zero vector if no neighbors are within minSeparationDistance" in {
  Constants.minSeparationDistance = 10.0
  val target = Boid(Vector2D(0, 0), Vector2D(1, 0))
  val boidFar = Boid(Vector2D(100, 100), Vector2D(0, 0))

  val flock = Flock(ArrayBuffer(target, boidFar))
  flock.calculateSeparation(target) shouldBe Vector2D(0, 0)
  }
  "Flock.calculateAlignment" should "correctly calculate the alignment weight from the neighborhood of the target boid" in {
  Constants.perceptionRadius = 100.0
  Constants.perceptionAngle = 120.toRadians

  val target = Boid(Vector2D(0, 0), Vector2D(10, 10))

  val boid1 = Boid(Vector2D(20, 20), Vector2D(0, 20))
  val boid2 = Boid(Vector2D(0, 10), Vector2D(20, 0))
  val boid3 = Boid(Vector2D(100, 100), Vector2D(50, 50))
  val boid4 = Boid(Vector2D(-20, -20), Vector2D(10, 10))

  val flock = Flock(ArrayBuffer(target, boid1, boid2, boid3, boid4))
  val result = flock.calculateAlignment(target)

  result.x should be (0.0 +- 0.0001)
  result.y should be (0.0 +- 0.0001)
  }
  it should "return zero vector when no neighbors are present (avoiding division by zero)" in {
  val target = Boid(Vector2D(0, 0), Vector2D(5, 5))
  val flock = Flock(ArrayBuffer(target))

  flock.calculateAlignment(target) shouldBe Vector2D(0, 0)
  }
  "Flock.calculateCohesion" should "correctly calculate the cohesion weight from the neighborhood of the target boid" in {
  Constants.perceptionRadius = 50.0
  Constants.perceptionAngle = 120.toRadians

  val target = Boid(Vector2D(10, 10), Vector2D(1, 0))

  val boidA = Boid(Vector2D(20, 15), Vector2D(0, 0))
  val boidB = Boid(Vector2D(20, 5), Vector2D(0, 0))
  val boidC = Boid(Vector2D(0, 10), Vector2D(0, 0))
  val boidD = Boid(Vector2D(100, 100), Vector2D(0, 0))

  val flock = Flock(ArrayBuffer(target, boidA, boidB, boidC, boidD))
  val result = flock.calculateCohesion(target)

  result.x should be (10.0 +- 0.0001)
  result.y should be (0.0 +- 0.0001)
  }
  it should "return zero vector for cohesion when there are no other boids in the flock" in {
  val target = Boid(Vector2D(50, 50), Vector2D(1, 1))
  val flock = Flock(ArrayBuffer(target))

  val result = flock.calculateCohesion(target)

  result shouldBe Vector2D(0, 0)
 }
  "Flock.update" should "move boids correctly based on velocity and deltaTime" in {
    Constants.maxSpeed = 100.0
    Constants.maxSteeringForce = 0.0

    val boid = Boid(Vector2D(10, 10), Vector2D(5, -2))
    val flock = Flock(ArrayBuffer(boid))

    flock.update(2.0)

    boid.position.x shouldBe 20.0
    boid.position.y shouldBe 6.0
  }

  it should "apply combined steering forces from neighbors within perception" in {
    Constants.perceptionRadius = 50.0
    Constants.perceptionAngle = 360.0
    Constants.separationWeight = 1.0
    Constants.alignmentWeight = 1.0
    Constants.cohesionWeight = 1.0
    Constants.mass = 1.0
    Constants.maxSteeringForce = 50.0

    val target = Boid(Vector2D(0, 0), Vector2D(0, 0))
    val neighbor = Boid(Vector2D(10, 0), Vector2D(0, 10))
    val flock = Flock(ArrayBuffer(target, neighbor))

    flock.update(1.0)

    target.velocity.x should not be 0.0
    target.velocity.y shouldBe > (0.0)
  }

  it should "strictly limit velocity to Constants.maxSpeed" in {
    Constants.maxSpeed = 5.0
    Constants.maxSteeringForce = 100.0

    val boid = Boid(Vector2D(0, 0), Vector2D(10, 10))
    val flock = Flock(ArrayBuffer(boid))

    flock.update(0.1)

    boid.velocity.magnitude() shouldBe <= (5.0 + 1e-9)
  }

  it should "handle edge case: multiple boids at the exact same position" in {
    Constants.minSeparationDistance = 10.0
    Constants.separationWeight = 1.0

    val b1 = Boid(Vector2D(10, 10), Vector2D(0, 0))
    val b2 = Boid(Vector2D(10, 10), Vector2D(0, 0))
    val flock = Flock(ArrayBuffer(b1, b2))

    noException should be thrownBy flock.update(1.0)
  }

  "Flock.addRandomBoid" should "increase the flock size by 1" in {
    val flock = Flock(ArrayBuffer())
    val initialSize = flock.boids.size
    flock.addRandomBoid()
    flock.boids.size shouldBe initialSize + 1
  }

  it should "place the boid within or on the boundaries of the world" in {
    Constants.worldWidth = 800.0
    Constants.worldHeight = 600.0
    val flock = Flock(ArrayBuffer())
    flock.addRandomBoid()
    val boid = flock.boids.head

    boid.position.x should (be >= 0.0 and be <= Constants.worldWidth)
    boid.position.y should (be >= 0.0 and be <= Constants.worldHeight)
  }

  "Flock.removeRandomBoid" should "remove exactly one boid from a non-empty flock" in {
    val boid1 = Boid(Vector2D(1, 1), Vector2D(1, 1))
    val boid2 = Boid(Vector2D(2, 2), Vector2D(2, 2))
    val flock = Flock(ArrayBuffer(boid1, boid2))

    flock.removeRandomBoid()

    flock.boids.size shouldBe 1
    (flock.boids.contains(boid1) || flock.boids.contains(boid2)) shouldBe true
  }

  it should "do nothing and not throw an exception when the flock is empty" in {
    val flock = Flock(ArrayBuffer())

    noException should be thrownBy flock.removeRandomBoid()
    flock.boids.size shouldBe 0
  }

  it should "eventually remove all boids if called repeatedly" in {
    val flock = Flock(ArrayBuffer.fill(3)(Boid(Vector2D(0,0), Vector2D(1,1))))

    flock.removeRandomBoid()
    flock.removeRandomBoid()
    flock.removeRandomBoid()

    flock.boids shouldBe empty
  }

  "Flock.addRandomBoids" should "increase the flock size by n" in {
    val flock = Flock(ArrayBuffer())
    val n = 10
    flock.addRandomBoids(n)
    flock.boids.size shouldBe n
  }
  it should "not exceed Constants.maxFlockSize" in {
    val flock = Flock(ArrayBuffer.fill(198)(Boid(Vector2D(0,0), Vector2D(1,1))))

    flock.addRandomBoids(5)

    flock.boids.size shouldBe Constants.maxFlockSize
  }

end FlockTest



