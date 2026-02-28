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
  val target = Boid(Vector2D(0,0), Vector2D(1,1))
  val flock = Flock(ArrayBuffer(target))
  flock.findNeighbors(target) shouldBe empty
}

end FlockTest



