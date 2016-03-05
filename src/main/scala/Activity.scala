import scala.util.Random

case class Activity(name: String, durationInMinutes: Int, costIfAtSameTime: Int)

object Activity {
  val TreasureHunt = Activity(name = "Chasse au trésor", durationInMinutes = 30, costIfAtSameTime = 10)
  val Workshops = Activity(name = "Ateliers", durationInMinutes = 30, costIfAtSameTime = 1)
  val LanguageGames = Activity(name = "Jeux de langue", durationInMinutes = 45, costIfAtSameTime = 1)
  val BoardGames = Activity(name = "Jeux de société", durationInMinutes = 45, costIfAtSameTime = 1)
  val SkillGames = Activity(name = "Jeux d'adresse", durationInMinutes = 45, costIfAtSameTime = 2)

  val Activities = Seq(TreasureHunt, Workshops, LanguageGames, BoardGames, SkillGames)

  def randomOrder(activities: Seq[Activity]): Seq[Activity] =
    Random.shuffle(activities)

  def activitiesWithPause(activities: Seq[Activity], pauseInMinutesBetweenActivities: Int): Seq[Activity] = {
    val pauseActivity = Activity(
      name = "Pause",
      durationInMinutes = pauseInMinutesBetweenActivities,
      costIfAtSameTime = 0)

    val lastActivityIndex = activities.size - 1

    for {
      (activity, index) <- activities.zipWithIndex
      lastActivity = index == lastActivityIndex
      activityWithPause <- if (lastActivity) Seq(activity) else Seq(activity, pauseActivity)
    } yield activityWithPause
  }
}
