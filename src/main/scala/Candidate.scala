import org.joda.time.LocalTime

case class Candidate(activitiesByGroup: Map[Group, Seq[Activity]]) {
  def cost(pauseInMinutesBetweenActivities: Int): Int = {
    // @todo
    0
  }

  def asStrings(startTime: LocalTime, pauseInMinutesBetweenActivities: Int): Seq[String] =
    (for ((group, activities) <- activitiesByGroup.toSeq.sortBy(_._1.number)) yield {
      val activitiesWithPause = Activity.activitiesWithPause(activities, pauseInMinutesBetweenActivities)
      s"${group.name}:" +: Activity.asStrings(activitiesWithPause, startTime, withPauses = false).map(" " + _)
    }).flatten
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
