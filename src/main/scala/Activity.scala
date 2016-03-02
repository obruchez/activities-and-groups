case class Activity(name: String, durationInMinutes: Int, costIfAtSameTime: Int)

object Activity {
  val activities = Seq(
    Activity(name = "Chasse trésor", durationInMinutes = 30, costIfAtSameTime = 10),
    Activity(name = "Sketch", durationInMinutes = 30, costIfAtSameTime = 1),
    Activity(name = "Jeu langue", durationInMinutes = 45, costIfAtSameTime = 1),
    Activity(name = "Jeu société", durationInMinutes = 45, costIfAtSameTime = 1),
    Activity(name = "Jeu adresse", durationInMinutes = 45, costIfAtSameTime = 2))

  def randomOrder(activities: Seq[Activity]): Seq[Activity] = {
    // @todo
    Seq()
  }
}
