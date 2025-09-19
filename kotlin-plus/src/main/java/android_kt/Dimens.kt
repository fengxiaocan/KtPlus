package android_kt

import android.content.Context
import android.content.res.Resources


/**
 * dp转px
 * @return px值
 */
fun Float.dp2px(context: Context? = null): Int {
  val scale: Float = (context?.resources ?: Resources.getSystem()).displayMetrics.density
  return (this * scale + 0.5f).toInt()
}

/**
 * px转dp
 * @return dp值
 */
fun Float.px2dp(context: Context? = null): Int {
  val scale: Float = (context?.resources ?: Resources.getSystem()).displayMetrics.density
  return (this / scale + 0.5f).toInt()
}

/**
 * sp转px
 * @return px值
 */
fun Float.sp2px(context: Context? = null): Int {
  val fontScale: Float = (context?.resources ?: Resources.getSystem()).displayMetrics.scaledDensity
  return (this * fontScale + 0.5f).toInt()
}

/**
 * px转sp
 *
 * @return sp值
 */
fun Float.px2sp(context: Context? = null): Int {
  val fontScale: Float = (context?.resources ?: Resources.getSystem()).displayMetrics.scaledDensity
  return (this / fontScale + 0.5f).toInt()
}