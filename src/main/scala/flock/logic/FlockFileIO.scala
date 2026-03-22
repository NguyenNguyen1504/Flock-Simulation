package flock.logic
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.{File, FileNotFoundException, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.{ Try, Using}

object FlockFileIO:

  def loadFlockFromFile(filename: String): Try[Flock] =
    for
      // Read input to JSON string
      jsonString <- Using(Source.fromFile(filename))(_.mkString)
      // Parse input and decode into List[Boid], turn to Try
      boids <- parse(jsonString).flatMap(_.as[List[Boid]]).toTry
    yield
      new Flock(ArrayBuffer.from(boids))

  def saveFlockToFile2(flock: Flock, filename: String): Try[Unit] =
    Try(flock.boids.asJson.spaces2).                                  // Try turning list to JSON string
      flatMap(flockData =>
        Using(new PrintWriter(new File(filename)))(_.write(flockData))// Use a writer to write data into file
    )





