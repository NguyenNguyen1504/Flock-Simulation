package flock.logic

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

object Constants:
  val default = Constants()


