package java_kt

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 获得两点之间的直线距离
 *
 * @param p1 PointF
 * @param p2 PointF
 * @return 两点之间的直线距离 two point distance
 */
fun getTwoPointDistance(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Float {
  return sqrt((p1.first - p2.first).pow(2.0f) + (p1.second - p2.second).pow(2.0f))
}

/**
 * 根据两个点(x1,y1)(x2,y2)的坐标算出斜率
 *
 * @param x1 x1
 * @param x2 x2
 * @param y1 y1
 * @param y2 y2
 * @return 斜率 line slope
 */
fun getLineSlope(x1: Float, x2: Float, y1: Float, y2: Float): Float? {
  return if (x2 - x1 == 0f) {
    null
  } else (y2 - y1) / (x2 - x1)
}

/**
 * 根据传入的两点得到斜率
 *
 * @param p1 PointF
 * @param p2 PointF
 * @return 返回斜率 line slope
 */
fun getLineSlope(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Float? {
  return if (p2.first - p1.first == 0f) null
  else (p2.second - p1.second) / (p2.first - p1.first)
}

/**
 * Get middle point between p1 and p2.
 * 获得两点连线的中点
 *
 * @param p1 PointF
 * @param p2 PointF
 * @return 中点 middle point
 */
fun getMiddlePoint(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Pair<Float, Float> {
  return Pair((p1.first + p2.first) / 2.0f, (p1.second + p2.second) / 2.0f)
}

/**
 * Get the point of intersection between circle and line.
 * 获取 通过指定圆心，斜率为lineK的直线与圆的交点。
 *
 * @param pMiddle The circle center point.
 * @param radius  The circle radius.
 * @param lineK   The slope of line which cross the pMiddle.
 * @return point f [ ]
 */
fun getIntersectionPoints(
  pMiddle: Pair<Float, Float>, radius: Float, lineK: Float?
): Array<Pair<Float, Float>> {
  val points: Array<Pair<Float, Float>> = arrayOf(Pair(0f, 0f), Pair(0f, 0f))
  val radian: Float
  var xOffset = 0f
  var yOffset = 0f
  if (lineK != null) {
    radian = atan(lineK.toDouble()).toFloat()
    xOffset = (sin(radian.toDouble()) * radius).toFloat()
    yOffset = (cos(radian.toDouble()) * radius).toFloat()
  } else {
    xOffset = radius
    yOffset = 0f
  }
  points[0] = Pair(pMiddle.first + xOffset, pMiddle.second - yOffset)
  points[1] = Pair(pMiddle.first - xOffset, pMiddle.second + yOffset)
  return points
}

/**
 * 获取限定有最大高和最大宽的缩放比例
 *
 * @param maxWidth  the max width
 * @param maxHeight the max height
 * @param width     the width
 * @param height    the height
 * @return confine scaling
 */
fun getConfineScaling(maxWidth: Float, maxHeight: Float, width: Float, height: Float): Float {
  if (maxWidth == 0f || maxHeight == 0f || width == 0f || height == 0f) {
    return 0f
  }
  val a = maxWidth / maxHeight
  val b = width / height
  return if (a > b) {
    maxHeight / height
  } else {
    maxWidth / width
  }
}

/**
 * Gets confine scaling.
 * 获取限定有最大高和最大宽的缩放比例
 *
 * @param maxWidth  the max width
 * @param maxHeight the max height
 * @param width     the width
 * @param height    the height
 * @return the confine scaling
 */
fun getConfineScaling(maxWidth: Int, maxHeight: Int, width: Int, height: Int): Float {
  return getConfineScaling(
    maxWidth.toFloat(), maxHeight.toFloat(), width.toFloat(), height.toFloat()
  )
}


/**
 * @param value 误差值
 * @param scope 误差范围
 * @param equalScope 是否等于误差最大/小范围边界值
 */
fun Int.inToleranceScope(value: Int, scope: Int, equalScope: Boolean = true): Boolean {
  return if (equalScope) this >= (value - scope) && this <= (value + scope) else
    this > (value - scope) && this < (value + scope)
}

/**
 * @param value 误差值
 * @param scope 误差范围
 * @param equalScope 是否等于误差最大/小范围边界值
 */
fun Float.inToleranceScope(value: Float, scope: Float, equalScope: Boolean = true): Boolean {
  return if (equalScope) this >= (value - scope) && this <= (value + scope) else
    this > (value - scope) && this < (value + scope)
}

/**
 * @param value 误差值
 * @param scope 误差范围
 * @param equalScope 是否等于误差最大/小范围边界值
 */
fun Long.inToleranceScope(value: Long, scope: Long, equalScope: Boolean = true): Boolean {
  return if (equalScope) this >= (value - scope) && this <= (value + scope) else
    this > (value - scope) && this < (value + scope)
}

/**
 * @param value 误差值
 * @param scope 误差范围
 * @param equalScope 是否等于误差最大/小范围边界值
 */
fun Double.inToleranceScope(value: Double, scope: Double, equalScope: Boolean = true): Boolean {
  return if (equalScope) this >= (value - scope) && this <= (value + scope) else
    this > (value - scope) && this < (value + scope)
}
