import java.io.{ File, PrintWriter }
import org.joda.time.LocalTime
import scala.util._

case class Solution(candidate: Candidate, pauseInMinutesBetweenActivities: Int, cost: Int, startTime: LocalTime) {
  def asStrings: Seq[String] =
    s"Cost: $cost" +: candidate.asStrings(startTime, pauseInMinutesBetweenActivities)

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
    val homeDirectory = new File(System.getProperty("user.home"))

    // @todo implement constraint
    /*
    Workshops
    BoardGames
    must not end after 11h30
     */
    /*
      BoardGames
      Workshops
      LanguageGames
     can last 5 minutes more
     */

    val referenceMorningSolution = Solution(
      Candidate.MorningBestManualCandidate,
      PauseInMinutesBetweenActivities,
      MorningTime)

    referenceMorningSolution.dumpToFile(new File(homeDirectory, "morning-reference.txt"))

    Image.saveImage(referenceMorningSolution, new File(homeDirectory, "morning-reference.png")).get

    lookForSolution(
      Group.MorningGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      MorningTime,
      new File(homeDirectory, "morning.txt"),
      new File(homeDirectory, "morning.png"))

    /*val referenceAfternoonSolution = Solution(
      Candidate.AfternoonBestManualCandidate,
      PauseInMinutesBetweenActivities,
      AfternoonTime)

    referenceAfternoonSolution.dumpToFile(new File(homeDirectory, "afternoon-reference.txt"))

    Image.saveImage(referenceAfternoonSolution, new File(homeDirectory, "afternoon-reference.png")).get

    lookForSolution(
      Group.AfternoonGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      AfternoonTime,
      new File(homeDirectory, "afternoon.txt"),
      new File(homeDirectory, "afternoon.png"))*/
  }

  def apply(candidate: Candidate, pauseInMinutesBetweenActivities: Int, startTime: LocalTime): Solution =
    Solution(
      candidate = candidate,
      pauseInMinutesBetweenActivities = pauseInMinutesBetweenActivities,
      cost = candidate.cost(pauseInMinutesBetweenActivities),
      startTime = startTime)

  def lookForSolution(groups: Seq[Group],
                      activities: Seq[Activity],
                      pauseInMinutesBetweenActivities: Int,
                      startTime: LocalTime,
                      bestSolutionFile: File,
                      bestSolutionImageFile: File): Unit = {
    // Retrieve best cost from file
    var bestSolution: Option[Solution] =
      costFromFile(bestSolutionFile).toOption map { cost =>
        Solution(
          candidate = Candidate(activitiesByGroup = Map()),
          pauseInMinutesBetweenActivities = pauseInMinutesBetweenActivities,
          cost = cost,
          startTime = startTime)
      }

    while (true) {
      val randomSolution = Solution(
        Candidate.randomCandidate(groups, activities),
        pauseInMinutesBetweenActivities,
        startTime)

      val betterSolution = bestSolution.map(randomSolution.cost < _.cost).getOrElse(true)

      if (betterSolution) {
        bestSolution = Some(randomSolution)
        randomSolution.dumpToFile(bestSolutionFile).get
        Image.saveImage(randomSolution, bestSolutionImageFile).get
      }
    }
  }

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
