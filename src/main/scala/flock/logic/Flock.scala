package flock.logic
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Flock:
  val defaultBehaviors: Seq[SteeringBehavior] = Seq(Separation, Alignment, Cohesion)

class Flock(
  private val _boids: ArrayBuffer[Boid],
  private val behaviors: Seq[SteeringBehavior] = Flock.defaultBehaviors,
  private var _constants: Constants = Constants.default
):
  
  def boids: Seq[Boid] = this._boids.toSeq

  def addBoid(boid: Boid): Unit =
    this._boids += boid

  def removeBoid(boid: Boid): Unit =
    this._boids -= boid

  def addRandomBoid(): Unit =
    val side = Random.nextInt(4) // 0: Top, 1: Right, 2: Bottom, 3: Left
    var x, y = 0.0
    var vx, vy = 0.0

    side match
      case 0 => // Top
        x = Random.nextDouble() * this._constants.worldWidth
        y = 0; vy = 1.0
      case 1 => // Right
        x = this._constants.worldWidth
        y = Random.nextDouble() * this._constants.worldHeight
        vx = -1.0
      case 2 => // Bottom
        x = Random.nextDouble() * this._constants.worldWidth
        y = this._constants.worldHeight; vy = -1.0
      case 3 => // Left
        x = 0
        y = Random.nextDouble() * this._constants.worldHeight
        vx = 1.0

    val randomPos = Vector2D(x, y)
    val randomVel = Vector2D(vx, vy).normalize() * this._constants.maxSpeed

    this.addBoid(Boid(randomPos, randomVel))

  def removeRandomBoid(): Unit =
    if this._boids.nonEmpty then
      val randomIndex = Random.nextInt(this._boids.size)
      this.removeBoid(this._boids(randomIndex))

  def addRandomBoids(n: Int): Unit =
    val currentCount = this._boids.size
    val spaceLeft = this._constants.maxFlockSize - currentCount
    val amountToAdd = if n > spaceLeft then spaceLeft else n
    for _ <- 1 to amountToAdd do
      addRandomBoid()

  def removeRandomBoids(n: Int): Unit =
    val removableAmount = this._boids.size - this._constants.minFlockSize
    val amountToRemove = if n > removableAmount then removableAmount else n
    for _ <- 1 to amountToRemove do
      removeRandomBoid()

  def resetWith(newBoids: Seq[Boid]): Unit =
    this._boids.clear()
    this._boids ++= newBoids.map(boid => Boid(boid.position, boid.velocity))

  def update(deltaTime: Double): Unit =
    val allBoids = this._boids.toSeq
    for boid <- this._boids do
      val totalForce = this.behaviors.map(behavior =>
          val force = behavior.apply(boid, allBoids, this._constants)
          val weight = behavior.weight(this._constants)
          force * weight
        ).reduce(_ + _)
      boid.applyForce(totalForce)
    for boid <- this._boids do
      boid.update(deltaTime, this._constants)

  def constants: Constants = this._constants

  def updateConstants(newConstants: Constants): Unit =
    require(newConstants.maxSpeed > 0, "maxSpeed must be positive")
    require(newConstants.minFlockSize < newConstants.maxFlockSize)
    this._constants = newConstants

  def updateSeparationWeight(w: Double): Unit = this.updateConstants(this._constants.copy(separationWeight = w))
  def updateAlignmentWeight(w: Double): Unit  = this.updateConstants(this._constants.copy(alignmentWeight  = w))
  def updateCohesionWeight(w: Double): Unit   = this.updateConstants(this._constants.copy(cohesionWeight   = w))

  def setSize(newSize: Int): Unit =
    val currentSize = this.boids.size
      if newSize > currentSize then
        this.addRandomBoids(newSize - currentSize)
      else if newSize < currentSize then
        this.removeRandomBoids(currentSize - newSize)

  def snapshot(): Seq[Boid] = this._boids.map(b => Boid(b.position, b.velocity)).toSeq

end Flock