package android_kt

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.drawable.toBitmap
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

fun Drawable.toBitmap(
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null
): Bitmap {
  if (this is BitmapDrawable) {
    if (bitmap == null) {
      // This is slightly better than returning an empty, zero-size bitmap.
      throw IllegalArgumentException("bitmap is null")
    }
    if (config == null || bitmap.config == config) {
      // Fast-path to return original. Bitmap.createScaledBitmap will do this check, but it
      // involves allocation and two jumps into native code so we perform the check ourselves.
      if (width == bitmap.width && height == bitmap.height) {
        return bitmap
      }
      return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
  }

  val (oldLeft, oldTop, oldRight, oldBottom) = bounds
  val bitmap = Bitmap.createBitmap(width, height, config ?: Config.ARGB_8888)
  setBounds(0, 0, width, height)
  draw(Canvas(bitmap))

  setBounds(oldLeft, oldTop, oldRight, oldBottom)
  return bitmap
}

/**
 * Returns a [Bitmap] representation of this [Drawable] or `null` if the drawable cannot be
 * represented as a bitmap.
 *
 * If this instance is a [BitmapDrawable] and the [width], [height], and [config] match, the
 * underlying [Bitmap] instance will be returned directly. If any of those three properties differ
 * then a new [Bitmap] is created. For all other [Drawable] types, a new [Bitmap] is created.
 *
 * If the result of [BitmapDrawable.getBitmap] is `null` or the drawable cannot otherwise be
 * represented as a bitmap, returns `null`.
 *
 * @param width Width of the desired bitmap. Defaults to [Drawable.getIntrinsicWidth].
 * @param height Height of the desired bitmap. Defaults to [Drawable.getIntrinsicHeight].
 * @param config Bitmap config of the desired bitmap. Null attempts to use the native config, if
 * any. Defaults to [Config.ARGB_8888] otherwise.
 * @see toBitmap
 */
public fun Drawable.toBitmapOrNull(
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null
): Bitmap? {
  if (this is BitmapDrawable && bitmap == null) {
    return null
  }
  return toBitmap(width, height, config)
}

/**
 * Updates this drawable's bounds. This version of the method allows using named parameters
 * to just set one or more axes.
 *
 * @see Drawable.setBounds
 */
public fun Drawable.updateBounds(
  @Px left: Int = bounds.left,
  @Px top: Int = bounds.top,
  @Px right: Int = bounds.right,
  @Px bottom: Int = bounds.bottom
) {
  setBounds(left, top, right, bottom)
}
