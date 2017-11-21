import org.joda.time._
import org.joda.time.format.DateTimeFormat

object Time {
  private lazy val hhmmFormatter = DateTimeFormat.forPattern("HH:mm")

  def hhmmString(readablePartial: ReadablePartial): String =
    hhmmFormatter.print(readablePartial)

  def minutesLater(localTime: LocalTime, minutes: Int): LocalTime =
    localTime.withFieldAdded(DurationFieldType.minutes(), minutes)
}
