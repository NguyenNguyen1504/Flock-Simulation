package flock.logic
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Flock(val boids: ArrayBuffer[Boid], val constants: Constants = Constants.default):

  def addBoid(boid: Boid): Unit =
    this.boids += boid

  def removeBoid(boid: Boid): Unit =
    this.boids -= boid

  def addRandomBoid(): Unit =
    val side = Random.nextInt(4) // 0: Top, 1: Right, 2: Bottom, 3: Left
    var x, y = 0.0
    var vx, vy = 0.0

    side match
      case 0 => // Top
        x = Random.nextDouble() * constants.worldWidth
        y = 0; vy = 1.0
      case 1 => // Right
        x = constants.worldWidth
        y = Random.nextDouble() * constants.worldHeight
        vx = -1.0
      case 2 => // Bottom
        x = Random.nextDouble() * constants.worldWidth
        y = constants.worldHeight; vy = -1.0
      case 3 => // Left
        x = 0
        y = Random.nextDouble() * constants.worldHeight
        vx = 1.0

    val randomPos = Vector2D(x, y)
    val randomVel = Vector2D(vx, vy).normalize() * constants.maxSpeed

    this.addBoid(Boid(randomPos, randomVel))

  def removeRandomBoid(): Unit =
    if this.boids.nonEmpty then
      val randomIndex = Random.nextInt(this.boids.size)
      this.removeBoid(this.boids(randomIndex))

  def addRandomBoids(n: Int): Unit =
    val currentCount = this.boids.size
    val spaceLeft = constants.maxFlockSize - currentCount
    val amountToAdd = if n > spaceLeft then spaceLeft else n
    for _ <- 1 to amountToAdd do
      addRandomBoid()

  def removeRandomBoids(n: Int): Unit =
    val removableAmount = this.boids.size - constants.minFlockSize
    val amountToRemove = if n > removableAmount then removableAmount else n
    for _ <- 1 to amountToRemove do
      removeRandomBoid()

  def resetWith(newBoids: Seq[Boid]): Unit =
    this.boids.clear()
    this.boids ++= newBoids.map(boid => Boid(boid.position, boid.velocity))

  def findNeighbors(target: Boid): ArrayBuffer[Boid] =
    val neighbors = ArrayBuffer[Boid]()
    val rSquared = constants.perceptionRadius * constants.perceptionRadius
    val maxAngle = constants.perceptionAngle / 2

    for boid <- this.boids do
      if boid != target then
        val dSquared = boid.distanceSquared(target)
        val angle = target.angle(boid)
        if dSquared <= rSquared && angle <= maxAngle then
          neighbors += boid

    neighbors


  def calculateSeparation(target: Boid): Vector2D =
    def takeSeparateVector(another: Boid): Vector2D =     // Calculates a repulsion vector inversely proportional to the squared distance
      val toAnother = target.position - another.position
      val r = toAnother.magnitudeSquared()
      toAnother * (1.0 / r)

    // Take all surrounding boids, not only the ones in the perception angle
    val minDSquared = constants.minSeparationDistance * constants.minSeparationDistance
    val closeNeighbors = boids.filter(b => b != target && b.distanceSquared(target) <= minDSquared)

    // Avoid UnsupportedOperationException
    if closeNeighbors.isEmpty then
      Vector2D(0,0)
    else
      closeNeighbors.map(takeSeparateVector(_)).reduce(_+_)


  def calculateAlignment(target: Boid): Vector2D =
    val neighbors = findNeighbors(target)
    val numberOfNeighbors = neighbors.size
    if numberOfNeighbors == 0 then
      Vector2D(0,0)
    else
      val avgVelocity = (neighbors.map(_.velocity).reduce(_+_)) * (1.0/numberOfNeighbors)
      avgVelocity - target.velocity


  def calculateCohesion(target: Boid): Vector2D =
    val neighbors = findNeighbors(target)
    val numberOfNeighbors = neighbors.size
    if numberOfNeighbors == 0 then
      Vector2D(0,0)
    else
      val centerOfMass = (neighbors.map(_.position).reduce(_+_)) * (1.0/numberOfNeighbors)
      centerOfMass - target.position


  def update(deltaTime: Double): Unit =
    for boid <- this.boids do
      val s = calculateSeparation(boid)  // Separation weight calculated based on neighborhood
      val a = calculateAlignment(boid)   // Alignment weight calculated based on neighborhood
      val c = calculateCohesion(boid)    // Cohesion weight calculated based on neighborhood
      val steeringForce = s * constants.separationWeight + a * constants.alignmentWeight + c * constants.cohesionWeight
      boid.applyForce(steeringForce)
    for boid <- this.boids do
      boid.update(deltaTime)

end Flock