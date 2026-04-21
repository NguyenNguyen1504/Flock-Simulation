package flock.logic

/** Simulation parameters that control boid behavior, world dimensions, and flock size limits.
 *  All fields have sensible defaults; use `copy(...)` to create modified variants.
 *
 *  @param perceptionRadius      Maximum distance at which a boid can sense its neighbors.
 *  @param perceptionAngle       Full field-of-view angle (in radians) for neighbor detection.
 *                               Divided by two to form the half-angle used in the angle check.
 *  @param minSeparationDistance Boids closer than this distance trigger the separation force.
 *  @param maxSpeed              Maximum speed a boid can travel (units per second).
 *  @param maxSteeringForce      Maximum steering force magnitude applied per update.
 *  @param separationWeight      Multiplier for the separation steering force.
 *  @param alignmentWeight       Multiplier for the alignment steering force.
 *  @param cohesionWeight        Multiplier for the cohesion steering force.
 *  @param mass                  Boid mass used to convert force to acceleration (F = ma).
 *  @param worldWidth            Horizontal extent of the simulated world; boids wrap around at the edges.
 *  @param worldHeight           Vertical extent of the simulated world; boids wrap around at the edges.
 *  @param minFlockSize          Minimum number of boids allowed in the flock.
 *  @param maxFlockSize          Maximum number of boids allowed in the flock.
 */
case class Constants(
  perceptionRadius: Double = 100.0,
  perceptionAngle: Double = 120.toRadians,
  minSeparationDistance: Double = 25.0,
  maxSpeed: Double = 80.0,
  maxSteeringForce: Double = 4.0,
  separationWeight: Double = 5.0,
  alignmentWeight: Double = 2.0,
  cohesionWeight: Double = 0.8,
  mass: Double = 1.0,
  worldWidth: Double = 800.0,
  worldHeight: Double = 400.0,
  minFlockSize: Int = 10,
  maxFlockSize: Int = 200
)

/** Companion object providing a default Constants instance. */
object Constants:
  val default = Constants()