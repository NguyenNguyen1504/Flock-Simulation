package flock.logic
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.FileNotFoundException
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

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

  def saveFlockToFile(flock: Flock, filename: String): Unit = ???

end FlockFileIO






