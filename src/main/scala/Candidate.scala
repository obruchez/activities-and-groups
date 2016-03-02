import org.joda.time.LocalTime

case class Candidate(activitiesByGroup: Map[Group, Seq[Activity]]) {
  def cost(pauseInMinutesBetweenActivities: Int): Int = {
    // @todo
    0
  }

  def asString(startTime: LocalTime): String = {
    val groupStrings =
      for ((group, activities) <- activitiesByGroup.toSeq.sortBy(_._1.name)) yield {
        // @todo
      }

    // @todo
    ""
  }
}

object Candidate {
  def randomCandidate(groups: Seq[Group], activities: Seq[Activity]): Candidate =
    Candidate(Map(groups.map(_ -> Activity.randomOrder(activities)): _*))
}