package android_kt

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation


fun View.translateAnimation(
  duration: Long,
  fromXType: Int = Animation.RELATIVE_TO_SELF,
  fromXValue: Float = 0f,
  toXType: Int = Animation.RELATIVE_TO_SELF,
  toXValue: Float = 1f,
  fromYType: Int = Animation.RELATIVE_TO_SELF,
  fromYValue: Float = 0f,
  toYType: Int = Animation.RELATIVE_TO_SELF,
  toYValue: Float = 0f,
  block: TranslateAnimation.() -> Unit = {},
) {
  this.animation = TranslateAnimation(
    fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue
  ).also {
    it.duration = duration
    block(it)
  }
}

fun View.translationY( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"translationY", *values)
}

fun View.translationX( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"translationX", *values)
}

fun View.rotation( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"rotation", *values)
}

fun View.scaleX( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"scaleX", *values)
}

fun View.scaleY( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"scaleY", *values)
}

fun View.alpha( vararg values: Float): ObjectAnimator {
  return ObjectAnimator.ofFloat(this,"alpha", *values)
}

/**
 * 从控件所在位置移动到控件的底部
 *
 * @return
 */
fun translateToBottom(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    1.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件所在位置移动到控件的顶部
 *
 * @return
 */
fun translateToTop(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    -1.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件所在位置移动到控件的左边
 *
 * @return
 */
fun translateToLeft(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    -1.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件所在位置移动到控件的右边
 *
 * @return
 */
fun translateToRight(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    1.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件的底部移动到控件所在位置
 *
 * @return
 */
fun translateFromBottomToLocation(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    1.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件的上面移动到控件所在位置
 */
fun translateFromTopToLocation(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    -1.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件的左边移动到控件所在位置
 *
 * @return
 */
fun translateFromLeftToLocation(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    -1.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  ani.setDuration(duration)
  return ani
}

/**
 * 从控件的右边移动到当前位置
 *
 * @param duration
 * @param isFill
 * @return
 */
fun translateFromRightToLocation(duration: Long = 200, isFill: Boolean = false): TranslateAnimation {
  val relativeToSelf = Animation.RELATIVE_TO_SELF
  val ani = TranslateAnimation(
    relativeToSelf,
    1.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f,
    relativeToSelf,
    0.0f
  )
  ani.isFillEnabled = isFill //使其可以填充效果从而不回到原地
  ani.fillAfter = !isFill //不回到起始位置
  ani.setDuration(duration)
  return ani
}

/**
 * 隐藏
 */
fun hideAlpha(duration: Long): AlphaAnimation {
  val mHiddenAction = AlphaAnimation(1.0f, 0.0f)
  mHiddenAction.setDuration(duration)
  return mHiddenAction
}

/**
 * 隐藏
 */
fun showAlpha(duration: Long): AlphaAnimation {
  val mHiddenAction = AlphaAnimation(0.0f, 1.0f)
  mHiddenAction.setDuration(duration)
  return mHiddenAction
}