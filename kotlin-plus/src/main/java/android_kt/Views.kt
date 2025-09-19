package android_kt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.core.graphics.createBitmap


fun View.drawBitmap(
  config: Bitmap.Config = Bitmap.Config.ARGB_8888, defaultColor: Int = Color.WHITE,
): Bitmap {
  val ret = createBitmap(measuredWidth, measuredHeight, config)
  val canvas = Canvas(ret)
  background?.draw(canvas) ?: canvas.drawColor(defaultColor)
  draw(canvas)
  return ret
}