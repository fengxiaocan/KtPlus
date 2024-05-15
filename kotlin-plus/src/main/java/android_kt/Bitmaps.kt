package android_kt

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File


fun Bitmap.saveToFile(
  file: File, format: CompressFormat = CompressFormat.PNG, quality: Int = 100
): Result<Boolean> {
  return runCatching {
    file.parentFile.mkdirs()
    file.outputStream().use {
      compress(format, quality, it)
    }
  }
}

/**
 * 压缩图片
 * @return
 */
fun Bitmap.compressImage(
  maxSize: Int, format: CompressFormat = CompressFormat.JPEG, decreasingLevel: Int = 5
): Result<Bitmap> {
  return runCatching {
    ByteArrayOutputStream().let { baos ->
      var options = 100
      do {
        baos.reset()
        compress(format, options, baos)
        options -= decreasingLevel
      } while (baos.toByteArray().size > maxSize)
      BitmapFactory.decodeStream(ByteArrayInputStream(baos.toByteArray()))
    }
  }
}

fun Bitmap.toByteArray(
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100
): ByteArray {
  return toOutputStream(format, quality).toByteArray()
}

fun Bitmap.toOutputStream(
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100
): ByteArrayOutputStream {
  return ByteArrayOutputStream().apply { compress(format, quality, this) }
}

fun Bitmap.copy(
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100
): Bitmap {
  return BitmapFactory.decodeStream(ByteArrayInputStream(toByteArray(format, quality)))
}

fun Bitmap.createScaleBitmap(scale: Float): Bitmap {
  return createScaledBitmap(this, (scale * width).toInt(), (scale * height).toInt(), false)
}

/**
 * 把多个位图覆盖合成为一个位图，左右拼接
 *
 * @param isBaseMax   是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
 * @return
 */
fun Array<Bitmap>.mergeBitmapHorizontal(
  isBaseMax: Boolean = true, config: Config = Config.RGB_565
): Result<Bitmap> {
  return runCatching {
    val target = (if (isBaseMax) maxOfOrNull { it.height } else minOfOrNull { it.height }) ?: 0
    val bitmap =
      Bitmap.createBitmap(sumOf { ((target * 1f / it.height) * it.width).toInt() }, target, config)
    val canvas = Canvas(bitmap)
    val rect = Rect(0, 0, 0, target)
    forEach {
      val temp = it.createScaleBitmap(target * 1f / it.height)
      rect.left = rect.right
      rect.right = rect.left + temp.width
      canvas.drawBitmap(temp, rect, rect, null)
      temp.recycle()
    }
    bitmap
  }
}

/**
 * 把多个位图覆盖合成为一个位图，上下拼接
 *
 * @param isBaseMax   是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
 * @return
 */
fun Array<Bitmap>.mergeBitmapVertical(
  isBaseMax: Boolean = true, config: Config = Config.RGB_565
): Result<Bitmap> {
  return runCatching {
    val target = (if (isBaseMax) maxOfOrNull { it.width } else minOfOrNull { it.width }) ?: 0
    val bitmap =
      Bitmap.createBitmap(target, sumOf { ((target * 1f / it.width) * it.height).toInt() }, config)
    val canvas = Canvas(bitmap)
    val rect = Rect(0, 0, target, 0)
    forEach {
      val temp = it.createScaleBitmap(target * 1f / it.width)
      rect.top = rect.bottom
      rect.bottom = rect.top + temp.height
      canvas.drawBitmap(temp, rect, rect, null)
      temp.recycle()
    }
    bitmap
  }
}

fun Drawable.toBitmap(): Bitmap {
  if (this is BitmapDrawable) {
    return bitmap
  } else {
    val bitmap = Bitmap.createBitmap(
      intrinsicWidth,
      intrinsicHeight,
      if (opacity != PixelFormat.OPAQUE) Config.ARGB_8888 else Config.RGB_565
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(canvas)
    return bitmap
  }
}
