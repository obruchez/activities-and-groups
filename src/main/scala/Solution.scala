import org.joda.time.LocalTime

object Solution {
  def main(args: Array[String]): Unit = {
    val pauseInMinutesBetweenActivities = 5

    lookForSolution(Group.firstGroups, Activity.activities, pauseInMinutesBetweenActivities, new LocalTime(8, 10))
    //lookForSolution(Group.secondGroups, Activity.activities, pauseInMinutesBetweenActivities, new LocalTime(12, 55))
  }

  def lookForSolution(groups: Seq[Group],
                      activities: Seq[Activity],
                      pauseInMinutesBetweenActivities: Int,
                      startTime: LocalTime): Unit = {
    var bestCandidateAndCost: Option[(Candidate, Int)] = None

    while (true) {
      val randomCandidate = Candidate.randomCandidate(groups, activities)
      val cost = randomCandidate.cost(pauseInMinutesBetweenActivities)

      val betterCandidate = bestCandidateAndCost.map(cost < _._2).getOrElse(true)
      if (betterCandidate) {
        bestCandidateAndCost = Some((randomCandidate, cost))
        println(s"Current cost: $cost")
        println(s"Current candidate: ${randomCandidate.asString(startTime)}")
      }
    }
  }
}
