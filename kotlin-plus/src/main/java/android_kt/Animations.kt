package android_kt

import android.view.View
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
  block: TranslateAnimation.() -> Unit = {}
) {
  this.animation = TranslateAnimation(
    fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue
  ).also {
    it.duration = duration
    block(it)
  }
}