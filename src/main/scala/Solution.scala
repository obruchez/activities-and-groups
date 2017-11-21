import java.io.{File, PrintWriter}
import org.joda.time.LocalTime
import scala.util._

case class Solution(candidate: Candidate,
                    pauseInMinutesBetweenActivities: Int,
                    cost: Int,
                    startTime: LocalTime) {
  def randomMutation: Solution =
    copy(candidate = candidate.randomMutation)

  def asStrings: Seq[String] =
    s"Cost: $cost" +: candidate.asStrings(startTime,
                                          pauseInMinutesBetweenActivities)

  def dumpToFile(file: File): Try[Unit] = Try {
    val string = asStrings.mkString("\n")

    val writer = new PrintWriter(file)

    try {
      writer.write(string)
    } finally {
      writer.close()
    }
  }
}

object Solution {
  val PauseInMinutesBetweenActivities = 5
  val MorningTime = new LocalTime(8, 10)
  val AfternoonTime = new LocalTime(12, 55)

  def main(args: Array[String]): Unit = {
    val homeDirectory =
      new File(System.getProperty("user.home"), "ActivitiesAndGroups")

    if (!homeDirectory.exists()) {
      homeDirectory.mkdirs()
    }

    /*
     @todo implement constraints
      - BoardGames + Workshops + LanguageGames can last 5 minutes more
      - Workshops + BoardGames must not end after 11h30
     */

    val MorningArgument = "morning"
    val AfternoonArgument = "afternoon"
    val AcceptedArguments = Set(MorningArgument, AfternoonArgument)

    if (args.length == 1 && AcceptedArguments.contains(args.head)) {
      if (args.head == MorningArgument) {
        lookForMorningSolution(homeDirectory)
      } else if (args.head == AfternoonArgument) {
        lookForAfternoonSolution(homeDirectory)
      }
    } else {
      println(
        s"Usage: Solution ${AcceptedArguments.toSeq.sorted.mkString("|")}")
    }
  }

  def lookForMorningSolution(homeDirectory: File): Unit = {
    val referenceMorningSolution = Solution(
      Candidate.MorningBestManualCandidate,
      PauseInMinutesBetweenActivities,
      MorningTime)

    referenceMorningSolution.dumpToFile(
      new File(homeDirectory, "morning-reference.txt"))

    Image
      .saveImage(referenceMorningSolution,
                 new File(homeDirectory, "morning-reference.png"))
      .get

    lookForSolution(
      Group.MorningGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      MorningTime,
      new File(homeDirectory, "morning.txt"),
      new File(homeDirectory, "morning.png")
    )
  }

  def lookForAfternoonSolution(homeDirectory: File): Unit = {
    val referenceAfternoonSolution = Solution(
      Candidate.AfternoonBestManualCandidate,
      PauseInMinutesBetweenActivities,
      AfternoonTime)

    referenceAfternoonSolution.dumpToFile(
      new File(homeDirectory, "afternoon-reference.txt"))

    Image
      .saveImage(referenceAfternoonSolution,
                 new File(homeDirectory, "afternoon-reference.png"))
      .get

    lookForSolution(
      Group.AfternoonGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      AfternoonTime,
      new File(homeDirectory, "afternoon.txt"),
      new File(homeDirectory, "afternoon.png")
    )
  }

  def apply(candidate: Candidate,
            pauseInMinutesBetweenActivities: Int,
            startTime: LocalTime): Solution =
    Solution(
      candidate = candidate,
      pauseInMinutesBetweenActivities = pauseInMinutesBetweenActivities,
      cost = candidate.cost(pauseInMinutesBetweenActivities),
      startTime = startTime
    )

  def randomSolution(groups: Seq[Group],
                     activities: Seq[Activity],
                     pauseInMinutesBetweenActivities: Int,
                     startTime: LocalTime): Solution =
    Solution(Candidate.randomCandidate(groups, activities),
             pauseInMinutesBetweenActivities,
             startTime)

  // scalastyle:off method.length
  def lookForSolution(groups: Seq[Group],
                      activities: Seq[Activity],
                      pauseInMinutesBetweenActivities: Int,
                      startTime: LocalTime,
                      bestSolutionFile: File,
                      bestSolutionImageFile: File): Unit = {
    // Retrieve best cost from file
    var bestSolution: Option[Solution] =
      costFromFile(bestSolutionFile).toOption map { cost =>
        Solution(candidate = Candidate(activitiesByGroup = Map()),
                 pauseInMinutesBetweenActivities =
                   pauseInMinutesBetweenActivities,
                 cost = cost,
                 startTime = startTime)
      }

    var solutionCount = 0
    var solutionToCreateByMutationCount = 0

    val startTimeInMs = System.currentTimeMillis
    var lastStatTimeInMs = startTimeInMs

    val SolutionsToCreateByMutation = 1000000
    val StatFrequencyInMs = 60000

    while (true) {
      val randomSolution =
        if (solutionToCreateByMutationCount > 0) {
          assert(bestSolution.isDefined)

          solutionToCreateByMutationCount -= 1

          if (solutionToCreateByMutationCount == 0) {
            println("Back to random solutions mode")
          }

          // Mutatation from the currently best solution
          bestSolution.get.randomMutation
        } else {
          // Completely random solution
          this.randomSolution(groups,
                              activities,
                              pauseInMinutesBetweenActivities,
                              startTime)
        }

      solutionCount += 1

      // Statistics
      val currentTimeInMs = System.currentTimeMillis
      if (currentTimeInMs - lastStatTimeInMs > StatFrequencyInMs) {
        val timeSinceStartInSeconds = (currentTimeInMs - startTimeInMs).toDouble / 1000.0
        val solutionsPerSecond = solutionCount.toDouble / timeSinceStartInSeconds

        println(
          s"Solution count: $solutionCount ($solutionsPerSecond sol./sec)")

        lastStatTimeInMs = currentTimeInMs
      }

      val betterSolution =
        bestSolution.map(randomSolution.cost < _.cost).getOrElse(true)

      if (betterSolution) {
        bestSolution = Some(randomSolution)
        randomSolution.dumpToFile(bestSolutionFile).get
        Image.saveImage(randomSolution, bestSolutionImageFile).get

        // Spend some time mutating the currently best solution
        solutionToCreateByMutationCount = SolutionsToCreateByMutation

        println(s"New better solution (cost = ${randomSolution.cost})")
      }
    }
  }
  // scalastyle:on method.length

  private def costFromFile(file: File): Try[Int] = Try {
    (for {
      line <- scala.io.Source.fromFile(file).getLines().toSeq
      tokens = line.split(" ")
      if tokens.size == 2
      if tokens.head.startsWith("Cost")
      cost = tokens(1).toInt
    } yield cost).head
  }
}
