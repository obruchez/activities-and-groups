import scala.util.Random

case class Activity(name: String, durationInMinutes: Int, costIfAtSameTime: Int)

object Activity {
  val activities = Seq(
    Activity(name = "Chasse trésor", durationInMinutes = 30, costIfAtSameTime = 10),
    Activity(name = "Sketch", durationInMinutes = 30, costIfAtSameTime = 1),
    Activity(name = "Jeu langue", durationInMinutes = 45, costIfAtSameTime = 1),
    Activity(name = "Jeu société", durationInMinutes = 45, costIfAtSameTime = 1),
    Activity(name = "Jeu adresse", durationInMinutes = 45, costIfAtSameTime = 2))

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
