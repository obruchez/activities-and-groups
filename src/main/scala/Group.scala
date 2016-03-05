case class Group(name: String, personCount: Int)

object Group {
  val AfternoonGroups = Seq(
    Group(name = "1", personCount = 4),
    Group(name = "2", personCount = 4),
    Group(name = "3", personCount = 4),
    Group(name = "4", personCount = 4),
    Group(name = "5", personCount = 4),
    Group(name = "6", personCount = 6),
    Group(name = "7", personCount = 4),
    Group(name = "8", personCount = 6))

  val MorningGroups = Seq(
    Group(name = "9", personCount = 6),
    Group(name = "10", personCount = 4),
    Group(name = "11", personCount = 5),
    Group(name = "12", personCount = 4),
    Group(name = "13", personCount = 4),
    Group(name = "14", personCount = 5),
    Group(name = "15", personCount = 5),
    Group(name = "16", personCount = 4))

  def group(name: String): Group =
    (AfternoonGroups ++ MorningGroups).find(_.name == name).get
}
