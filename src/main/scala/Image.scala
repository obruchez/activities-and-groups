import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio._
import org.joda.time.LocalTime
import scala.util.Try

object Image {
  def saveImage(solution: Solution, file: File): Try[Unit] = Try {
    val bufferedImage =
      new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    val g = bufferedImage.getGraphics

    val groupCount = solution.candidate.activitiesByGroup.size

    for {
      ((group, activities), groupIndex) <- solution.candidate.activitiesByGroup.toSeq
        .sortBy(_._1.number)
        .zipWithIndex
      minX = 0
      maxX = bufferedImage.getWidth
      minY = (bufferedImage.getHeight.toDouble / groupCount * groupIndex).round.toInt
      maxY = (bufferedImage.getHeight.toDouble / groupCount * (groupIndex + 1)).round.toInt
    } {
      drawGroup(
        g,
        minX,
        maxX,
        minY,
        maxY,
        solution.startTime,
        group,
        Activity.activitiesWithPause(activities,
                                     solution.pauseInMinutesBetweenActivities))
    }

    ImageIO.write(bufferedImage, "png", file)
  }

  def drawGroup(g: Graphics,
                minX: Int,
                maxX: Int,
                minY: Int,
                maxY: Int,
                startTime: LocalTime,
                group: Group,
                activities: Seq[Activity]): Unit = {
    // Group label
    g.setColor(Color.WHITE)
    g.setFont(smallFont)
    g.drawString(group.name, (maxX - minX) / 100, (minY + maxY) / 2)

    val activityMinX = (maxX - minX) / 10
    val activityMaxX = maxX

    val totalDurationInMinutes = activities.map(_.durationInMinutes).sum

    var startInMinutes = 0

    def activityX(minutes: Int): Int =
      (activityMinX + (minutes.toDouble / totalDurationInMinutes.toDouble) * (activityMaxX - activityMinX)).round.toInt

    for (activity <- activities) {
      val endInMinutes = startInMinutes + activity.durationInMinutes
      val activityFromX = activityX(startInMinutes)
      val activityToX = activityX(endInMinutes)

      g.setColor(colors.getOrElse(activity, Color.GRAY))
      g.fillRect(activityFromX, minY, activityToX - activityFromX, maxY - minY)

      if (!activity.pause) {
        g.setColor(Color.BLACK)
        g.setFont(bigFont)
        g.drawString(
          activity.asString(Time.minutesLater(startTime, startInMinutes)),
          activityFromX + 10,
          (minY + maxY) / 2)
      }

      startInMinutes = endInMinutes
    }
  }

  private val colors: Map[Activity, Color] = Map(
    Activity.TreasureHunt -> Color.RED,
    Activity.Workshops -> Color.GREEN,
    Activity.LanguageGames -> Color.CYAN,
    Activity.BoardGames -> Color.ORANGE,
    Activity.SkillGames -> Color.YELLOW
  )

  private val smallFont = new Font("TimesRoman", Font.PLAIN, 14)
  private val bigFont = new Font("TimesRoman", Font.PLAIN, 20)
}
