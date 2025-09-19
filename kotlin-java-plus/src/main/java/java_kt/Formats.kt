package java_kt

import java.text.NumberFormat


/**
 * 保留小数点
 *
 * @param accuracy
 * @return
 */
fun Double.format(accuracy: Int): String {
  val nf = NumberFormat.getNumberInstance()
  nf.maximumFractionDigits = accuracy
  return nf.format(this)
}

/**
 * 格式化文件大小，保留末尾的2，达到长度一致
 * @return
 */
fun Long.formatFileSize(accuracy: Int = 2): String {
  val len = this
  return if (len < 1024) {
    "${len}B"
  } else if (len < 1024 * 1024) {
    (len.toDouble() / 1024).format(accuracy) + "KB"
  } else if (len < 1024 * 1024 * 1024) {
    (len.toDouble() / 1024 / 1024).format(accuracy) + "MB"
  } else {
    (len.toDouble() / 1024 / 1024 / 1024).format(accuracy) + "GB"
  }
}

/**
 * 格式化音乐时间: 120 000 --> 02:00
 *
 * @return
 */
fun Long.formatMusicTime(): String {
  var time = this / 1000
  var formatTime: String
  if (time < 10) {
    formatTime = "00:0$time"
    return formatTime
  } else if (time < 60) {
    formatTime = "00:$time"
    return formatTime
  } else {
    val i = time / 60
    formatTime = if (i < 10) "0$i:" else "$i:"
    val j = time % 60
    formatTime += if (j < 10) "0$j" else j
    return formatTime
  }
}