package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ArrayBuffer

class FlockTest extends AnyFlatSpec with Matchers:

  "A Flock" should "correctly add a new boid" in {
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
  "Flock separation calculator" should "correctly calculate the separation weight from the neighborhood of the target boid" in {
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
  "Flock alignment calculator" should "correctly calculate the alignment weight from the neighborhood of the target boid" in {
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
  "Flock cohesion calculator" should "correctly calculate the cohesion weight from the neighborhood of the target boid" in {
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

end FlockTest



