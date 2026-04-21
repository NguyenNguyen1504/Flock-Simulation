package flock.logic
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

import java.io.{File, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.{Try, Using}

/** Provides file I/O for persisting and restoring flock state as JSON.
 *
 *  Boids are serialized as a JSON array (position + velocity per boid).
 *  All operations return [[scala.util.Try]] so callers can handle I/O and
 *  parse errors without exceptions propagating.
 */
object FlockFileIO:

  /** Reads a flock from a JSON file and returns it wrapped in a [[scala.util.Try]].
   *
   *  The file must contain a JSON array of boid objects, each with "position" and "velocity" fields.
   *  Failures (file not found, malformed JSON, decode error) are captured in the returned Try.
   *
   *  @param filename Path to the JSON file to read.
   *  @return A [[scala.util.Try]] containing the reconstructed [[Flock]] on success.
   */
  def loadFlockFromFile(filename: String): Try[Flock] =
    for
      // Read the file contents into a single JSON string
      jsonString <- Using(Source.fromFile(filename))(_.mkString)
      // Parse the JSON string and decode into a list of boids; convert Either to Try for flatMap
      boids <- parse(jsonString).flatMap(_.as[List[Boid]]).toTry
    yield
      new Flock(ArrayBuffer.from(boids))

  /** Serializes the flock's boids to a pretty-printed JSON file.
   *
   *  Only position and velocity are written per boid (see [[Boid]] codec).
   *  File creation and write errors are captured in the returned Try.
   *
   *  @param flock    The flock whose boids will be saved.
   *  @param filename Path of the output file (created or overwritten).
   *  @return A [[scala.util.Try]] containing Unit on success.
   */
  def saveFlockToFile(flock: Flock, filename: String): Try[Unit] =
    Try(flock.boids.asJson.spaces2)                                   // Serialize boids to indented JSON string
      .flatMap(flockData =>
        Using(new PrintWriter(new File(filename)))(_.write(flockData)) // Write string to file; Using ensures the writer is closed
      )