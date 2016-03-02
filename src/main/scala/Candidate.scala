import org.joda.time.LocalTime

case class Candidate(activitiesByGroup: Map[Group, Seq[Activity]]) {
  def cost(pauseInMinutesBetweenActivities: Int): Int = {
    // @todo
    0
  }

  def asString(startTime: LocalTime): String = {
    // @todo
    ""
  }
}

object Candidate {
  def randomCandidate(groups: Seq[Group], activities: Seq[Activity]): Candidate = {
    // @todo
    Candidate(Map())
  }
}