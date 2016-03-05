import org.joda.time.LocalTime

case class Candidate(activitiesByGroup: Map[Group, Seq[Activity]]) {
  // Main cost function
  def cost(pauseInMinutesBetweenActivities: Int): Int = {
    // Insert the pauses between the activities
    var activitiesWithPauseByGroup =
      activitiesByGroup.toSeq map { groupAndActivities =>
        groupAndActivities._1 -> Activity.activitiesWithPause(groupAndActivities._2, pauseInMinutesBetweenActivities)
      }

    var totalCost = 0

    // Keep looping while at least one group has an activity
    while (activitiesWithPauseByGroup.exists(_._2.nonEmpty)) {
      // Compute the cost looking just at the first minute of all groups (i.e. the first activity of all groups)
      totalCost += costForFirstMinute(activitiesWithPauseByGroup)

      // Skip the first minute of each group, removing finished activities
      activitiesWithPauseByGroup = activitiesByGroupWithFirstMinuteRemoved(activitiesWithPauseByGroup)
    }

    totalCost
  }

  def asStrings(startTime: LocalTime, pauseInMinutesBetweenActivities: Int): Seq[String] =
    (for ((group, activities) <- activitiesByGroup.toSeq.sortBy(_._1.number)) yield {
      val activitiesWithPause = Activity.activitiesWithPause(activities, pauseInMinutesBetweenActivities)
      s"${group.name}:" +: Activity.asStrings(activitiesWithPause, startTime, withPauses = false).map(" " + _)
    }).flatten

  private def costForFirstMinute(activitiesByGroup: Seq[(Group, Seq[Activity])]): Int = {
    val concurrentActivities = activitiesByGroup.flatMap(_._2.headOption)

    val activityCounts = concurrentActivities.groupBy(activity => activity).map(kv => kv._1 -> kv._2.size)

    (for ((activity, count) <- activityCounts) yield {
      assert(count > 0)

      // Number of persons
      val personCount =
        (for {
          (group, groupActivities) <- activitiesByGroup
          groupActivity <- groupActivities
          if groupActivity == activity
        } yield group.personCount).sum

      // The idea here is that if the activity is done by one group only, the cost will be zero ; it will be positive
      // if several groups are doing the activity at the same time ; it will be more if more people are doing the
      // activity at the same time
      activity.costIfAtSameTime * (count - 1) * personCount
    }).sum
  }

  private def activitiesByGroupWithFirstMinuteRemoved(activitiesByGroup: Seq[(Group, Seq[Activity])]): Seq[(Group, Seq[Activity])] =
    for ((group, activities) <- activitiesByGroup) yield group -> activitiesWithFirstMinuteRemoved(activities)

  private def activitiesWithFirstMinuteRemoved(activities: Seq[Activity]): Seq[Activity] = {
    activities.toList match {
      case Nil =>
        Nil
      case head :: tail if head.durationInMinutes > 0 =>
        // First activity not tail => remove one minute from the remaining duration
        head.copy(durationInMinutes = head.durationInMinutes - 1)  :: tail
      case head :: tail =>
        // First activity finished => remove it
        tail
    }
  }
}

object Candidate {
  def randomCandidate(groups: Seq[Group], activities: Seq[Activity]): Candidate =
    Candidate(Map(groups.map(_ -> Activity.randomOrder(activities)): _*))

  import Activity._
  import Group.group

  val MorningBestManualCandidate =
    Candidate(
      activitiesByGroup = Map(
        Seq(
          group(9) -> Seq(SkillGames, Workshops, LanguageGames, TreasureHunt, BoardGames),
          group(10) -> Seq(BoardGames, SkillGames, Workshops, LanguageGames, TreasureHunt),
          group(11) -> Seq(Workshops, LanguageGames, SkillGames, BoardGames, TreasureHunt),
          group(12) -> Seq(LanguageGames, SkillGames, Workshops, BoardGames, TreasureHunt),
          group(13) -> Seq(BoardGames, TreasureHunt, Workshops, LanguageGames, SkillGames),
          group(14) -> Seq(LanguageGames, TreasureHunt, BoardGames, SkillGames, Workshops),
          group(15) -> Seq(Workshops, SkillGames, LanguageGames, TreasureHunt, BoardGames),
          group(16) -> Seq(TreasureHunt, LanguageGames, SkillGames, BoardGames, Workshops)): _*))

  val AfternoonBestManualCandidate =
      Candidate(
        activitiesByGroup = Map(
          Seq(
            group(1) -> Seq(BoardGames, SkillGames, Workshops, LanguageGames, TreasureHunt),
            group(2) -> Seq(Workshops, LanguageGames, SkillGames, BoardGames, TreasureHunt),
            group(3) -> Seq(TreasureHunt, SkillGames, Workshops, BoardGames, LanguageGames),
            group(4) -> Seq(BoardGames, TreasureHunt, Workshops, LanguageGames, SkillGames),
            group(5) -> Seq(LanguageGames, TreasureHunt, BoardGames, SkillGames, Workshops),
            group(6) -> Seq(Workshops, SkillGames, LanguageGames, TreasureHunt, BoardGames),
            group(7) -> Seq(TreasureHunt, LanguageGames, SkillGames, BoardGames, Workshops),
            group(8) -> Seq(SkillGames, Workshops, LanguageGames, TreasureHunt, BoardGames)): _*))
}
