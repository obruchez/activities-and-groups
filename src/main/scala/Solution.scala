import org.joda.time.LocalTime

import java.io.{ File, PrintWriter }
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

    val referenceMorningSolution = Solution(
      Candidate.MorningBestManualCandidate,
      PauseInMinutesBetweenActivities,
      MorningTime)

    referenceMorningSolution.dumpToFile(new File(homeDirectory, "morning-reference.txt"))

    /*lookForSolution(
      Group.MorningGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      MorningTime,
      new File(homeDirectory, "morning.txt"))

    val referenceAfternoonSolution = Solution(
      Candidate.AfternoonBestManualCandidate,
      PauseInMinutesBetweenActivities,
      AfternoonTime)

    referenceAfternoonSolution.dumpToFile(new File(homeDirectory, "afternoon-reference.txt"))

    lookForSolution(
      Group.AfternoonGroups,
      Activity.Activities,
      PauseInMinutesBetweenActivities,
      AfternoonTime,
      new File(homeDirectory, "afternoon.txt"))*/
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
                      bestSolutionFile: File): Unit = {
    var bestSolution: Option[Solution] = None

    while (true) {
      val randomSolution = Solution(
        Candidate.randomCandidate(groups, activities),
        pauseInMinutesBetweenActivities,
        startTime)

      val betterSolution = bestSolution.map(randomSolution.cost < _.cost).getOrElse(true)

      if (betterSolution) {
        bestSolution = Some(randomSolution)
        randomSolution.dumpToFile(bestSolutionFile).get
      }
    }
  }
}
