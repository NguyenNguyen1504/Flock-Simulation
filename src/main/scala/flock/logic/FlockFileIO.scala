package flock.logic
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.{File, FileNotFoundException, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.{Success, Try, Using}

object FlockFileIO:

  def loadFlockFromFile(filename: String): Flock =
    try
      val source = Source.fromFile(filename)
      val jsonStr =
        try source.mkString       // Always close file
        finally source.close()    // even if there is error in making string from source

      val result = parse(jsonStr)   // Read input
        .flatMap(_.as[List[Boid]])  // Decode input into list

      result match

        case Right(list: List[Boid]) =>
          new Flock(ArrayBuffer.from(list))

        case Left(error: Error) =>
          error match
            case ParsingFailure(message, rest) =>
              println(s"Error in parsing JSON file: $message")

            case DecodingFailure(message, history) =>
              println(s"Error in decoding JSON file: $message. Data structure error at: ${history.mkString}")

            case _ =>
              println(s"Unexpected error: ${error.getMessage}")

          new Flock(ArrayBuffer.empty)  // Currently, return an empty Flock


    catch
      case error: FileNotFoundException =>
        println(s"File error: ${error.getMessage}")
        new Flock(ArrayBuffer.empty)  // Currently, return an empty Flock

      case error: Exception =>
        println(s"Unexpected system error: ${error.getMessage}")
        new Flock(ArrayBuffer.empty)

  def saveFlockToFile(flock: Flock, filename: String): Try[Unit] =
    Try{

      val flockData = flock.boids.asJson.spaces2
      val file = new File(filename)

      Using.resource(new PrintWriter(file)){
        _.write(flockData)
      }

    }

  private def loadFlockFromFile2(filename: String): Try[Flock] =
    Try {
      val jsonString = Using.resource(Source.fromFile(filename)) {
                       source => source.mkString
                       }

      val boids = parse(jsonString)
                 .flatMap(_.as[List[Boid]])
                 .toTry.get
      new Flock(ArrayBuffer.from(boids))
    }

  private def loadFlockFromFile3(filename: String): Try[Flock] =
    for
      // Read input to JSON string
      jsonString <- Using(Source.fromFile(filename))(_.mkString)
      // Parse input and decode into List[Boid], turn to Try
      boids <- parse(jsonString).flatMap(_.as[List[Boid]]).toTry
    yield
      new Flock(ArrayBuffer.from(boids))

  private def saveFlockToFile2(flock: Flock, filename: String): Try[Unit] =
    Try(flock.boids.asJson.spaces2).                                  // Try turning list to JSON string
      flatMap(flockData =>
        Using(new PrintWriter(new File(filename)))(_.write(flockData))// Use a writer to write data into file
    )





