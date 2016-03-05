case class Group(number: Int, personCount: Int) {
  def name: String =
    s"Groupe $number"
}

object Group {
  val MorningGroups = Seq(
    Group(number = 9, personCount = 6),
    Group(number = 10, personCount = 4),
    Group(number = 11, personCount = 5),
    Group(number = 12, personCount = 4),
    Group(number = 13, personCount = 4),
    Group(number = 14, personCount = 5),
    Group(number = 15, personCount = 5),
    Group(number = 16, personCount = 4))

  val AfternoonGroups = Seq(
    Group(number = 1, personCount = 4),
    Group(number = 2, personCount = 4),
    Group(number = 3, personCount = 4),
    Group(number = 4, personCount = 4),
    Group(number = 5, personCount = 4),
    Group(number = 6, personCount = 6),
    Group(number = 7, personCount = 4),
    Group(number = 8, personCount = 6))

  def group(number: Int): Group =
    (MorningGroups ++ AfternoonGroups).find(_.number == number).get
}
