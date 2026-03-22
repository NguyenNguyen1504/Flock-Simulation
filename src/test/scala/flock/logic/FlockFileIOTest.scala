package flock.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable.ArrayBuffer
import java.io.File

class FlockFileIOTest extends AnyFlatSpec with Matchers:
  val path = "src/test/resources/test1.json"
  "FlockFileIO" should "load a list of boids correctly from a valid JSON file" in {
    val result = FlockFileIO.loadFlockFromFile(path)

    result.isSuccess shouldBe true
    val flock = result.get
    flock.boids shouldNot be (empty)
    flock.boids.length shouldBe 2
    flock.boids.head.position shouldBe Vector2D(100.0, 150.0)
  }
  it should "return a Failure when the file does not exist" in {
    val result = FlockFileIO.loadFlockFromFile("non_existent_file.json")
    result.isFailure shouldBe true
  }
  it should "return a Failure when loading a corrupted JSON file" in {
  val invalidFilePath = "src/test/resources/invalid.json"
  val result = FlockFileIO.loadFlockFromFile(invalidFilePath)

  result.isFailure shouldBe true
  }
  it should "successfully save a flock to a new file" in {
    val tempFile = "data/temp_save.json"
    val boids = ArrayBuffer(Boid(Vector2D(10, 20), Vector2D(1, 1)), Boid(Vector2D(20, 30), Vector2D(1.5, 1)))
    val flock = new Flock(boids)

    val saveResult = FlockFileIO.saveFlockToFile(flock, tempFile)
    saveResult.isSuccess shouldBe true

    val savedFlock = FlockFileIO.loadFlockFromFile(tempFile).get
    savedFlock.boids.head.position shouldBe Vector2D(10, 20)

    val f = new File(tempFile)
    f.exists() shouldBe true
    f.delete()
  }

end FlockFileIOTest



