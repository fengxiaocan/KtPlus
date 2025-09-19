package java_kt

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val SEC_MILLIS = 1000
const val MIN_MILLIS = 60 * SEC_MILLIS
const val HOUR_MILLIS = 60 * MIN_MILLIS
const val DAY_MILLIS = 24 * HOUR_MILLIS
const val WEEK_MILLIS = 7 * DAY_MILLIS

fun runTime(block: () -> Unit): Long {
  val startTime = System.currentTimeMillis()
  block()
  val endTime = System.currentTimeMillis()
  return endTime - startTime
}

fun nowTime() = System.currentTimeMillis()

/**
 * 秒数转毫秒数
 */
fun Long.secsToMillis(): Long {
  return this * 1000
}

/**
 * 获取当前时间的秒数
 */
fun nowTimeSecs(): Long {
  return System.currentTimeMillis() / 1000
}

fun Long.timeEra(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.ERA, locale)

fun Long.timeYear(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.YEAR, locale)

fun Long.timeMonth(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.MONTH, locale)

fun Long.timeWeekOfYear(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.WEEK_OF_YEAR, locale)

fun Long.timeWeekOfMonth(locale: Locale = Locale.CHINA) =
  timeCalendar(Calendar.WEEK_OF_MONTH, locale)

fun Long.timeWeekDate(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.DATE, locale)

fun Long.timeDayOfMonth(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.DAY_OF_MONTH, locale)

fun Long.timeDayOfYear(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.DAY_OF_YEAR, locale)

fun Long.timeDayOfWeek(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.DAY_OF_WEEK, locale)

fun Long.timeDayOfWeakInMonth(locale: Locale = Locale.CHINA) =
  timeCalendar(Calendar.DAY_OF_WEEK_IN_MONTH, locale)

fun Long.timeAmOrPm(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.AM_PM, locale)

fun Long.timeHour(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.HOUR, locale)

fun Long.timeHourOfDay(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.HOUR_OF_DAY, locale)

fun Long.timeMinute(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.MINUTE, locale)

fun Long.timeSecond(locale: Locale = Locale.CHINA) = timeCalendar(Calendar.SECOND, locale)

fun Long.timeCalendar(calendarField: Int = Calendar.ERA, locale: Locale = Locale.CHINA): Int {
  val calendar = Calendar.getInstance(locale)
  calendar.timeInMillis = this
  return calendar[calendarField]
}

fun Long.isThisYear(locale: Locale = Locale.CHINA): Boolean {
  return this.timeYear(locale) == nowTime().timeYear(locale)
}

fun Long.isSameDate(targetTime: Long = nowTime(), locale: Locale = Locale.CHINA): Boolean {
  val cal1 = Calendar.getInstance(locale)
  cal1.timeInMillis = this
  val cal2 = Calendar.getInstance(locale)
  cal1.timeInMillis = targetTime
  val isSameYear = cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
  return isSameYear && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
}

fun Long.formatTime(pattern: String = "yyyyMMdd HH:mm:ss", locale: Locale = Locale.CHINA): String {
  val sdf = SimpleDateFormat(pattern, locale)
  val da = Date(this)
  return sdf.format(da)
}

fun String.formatTime(
    pattern: String = "yyyyMMdd HH:mm:ss",
    locale: Locale = Locale.CHINA,
): Result<Long> {
  return runCatching {
    val sdf = SimpleDateFormat(pattern, locale)
    val date = sdf.parse(this)
    date.time
  }
}

/**
 * 判断是否是闰年
 */
fun Long.isLeapYear(locale: Locale = Locale.CHINA): Boolean {
  val year = timeYear(locale)
  return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
}

/**
 * 是否是今天
 */
fun Long.isToday(locale: Locale = Locale.CHINA): Boolean {
  return nowTime().isSameDate(this, locale)
}

/**
 * 是否是昨天
 */
fun Long.isYesterday(locale: Locale = Locale.CHINA): Boolean {
  return (nowTime() - DAY_MILLIS).isSameDate(this, locale)
}

/**
 * 是否是明天
 */
fun Long.isTomorrow(locale: Locale = Locale.CHINA): Boolean {
  return (nowTime() + DAY_MILLIS).isSameDate(this, locale)
}

/**
 * 是否是同一年
 */
fun Long.isSameYear(targetTime: Long = nowTime(), locale: Locale = Locale.CHINA): Boolean {
  return targetTime.timeYear(locale) == this.timeYear(locale)
}

/**
 * 是否是同一月
 */
fun Long.isSameMonth(targetTime: Long = nowTime(), locale: Locale = Locale.CHINA): Boolean {
  val cal1 = Calendar.getInstance(locale)
  cal1.timeInMillis = this
  val cal2 = Calendar.getInstance(locale)
  cal1.timeInMillis = targetTime
  val isSameYear = cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
  return isSameYear && cal1[Calendar.MONTH] == cal2[Calendar.MONTH]
}

/**
 * 是否是同一周
 */
fun Long.isSameWeek(targetTime: Long = nowTime(), locale: Locale = Locale.CHINA): Boolean {
  val cal1 = Calendar.getInstance(locale)
  cal1.timeInMillis = this
  val cal2 = Calendar.getInstance(locale)
  cal1.timeInMillis = targetTime
  val isSameYear = cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
  return isSameYear && cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]
}