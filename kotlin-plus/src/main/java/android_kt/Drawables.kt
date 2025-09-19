package android_kt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
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
import androidx.core.graphics.drawable.toDrawable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import kotlin.math.max
import kotlin.math.min

/**
 * 对于过大尺寸的drawable需要压缩一下，真的有超过1M的icon会导致滑动卡顿
 */
fun Drawable.compress(
  context: Context,
  maxWidth: Int,
  maxHeight: Int,
  config: Config? = null
): Drawable {
  if (maxWidth > 0) {
    //>0 为需要限制的宽/高，<=0 为不限制宽/高
    if (maxHeight > 0) {
      if (intrinsicWidth > maxWidth || intrinsicHeight > maxHeight) {
        val scaleW = intrinsicWidth * 1f / maxWidth
        val scaleH = intrinsicHeight * 1f / maxHeight
        val scale = max(scaleW, scaleH)
        val targetWidth = (intrinsicWidth / scale).toInt()
        val targetHeight = (intrinsicHeight / scale).toInt()
        return toBitmap(targetWidth, targetHeight, config).toDrawable(context.resources)
      }
    } else {
      if (intrinsicWidth > maxWidth) {
        val scale = intrinsicWidth * 1f / maxWidth
        val targetHeight = (intrinsicHeight / scale).toInt()
        return  toBitmap(maxWidth, targetHeight, config).toDrawable(context.resources)
      }
    }
  } else {
    if (maxHeight > 0) {
      if (intrinsicHeight > maxHeight) {
        val scale = intrinsicHeight * 1f / maxHeight
        val targetWidth = (intrinsicWidth / scale).toInt()
        return  toBitmap(targetWidth, maxHeight, config).toDrawable(context.resources)
      }
    }
  }
  return this
}

/**
 * 设置 Drawable 居中适应宽高按原比例缩放
 */
fun Drawable.setFixCenter(boundWidth: Float, boundHeight: Float) {
  val drawableWidth = intrinsicWidth
  val drawableHeight = intrinsicHeight
  val scale = min(boundWidth / drawableWidth, boundHeight / drawableHeight)
  val targetWidth = (drawableWidth * scale).toInt()
  val targetHeight = (drawableHeight * scale).toInt()
  val left = (bounds.left + (boundWidth - targetWidth) / 2).toInt()
  val top = (bounds.top + (boundHeight - targetHeight) / 2).toInt()
  val right = left + targetWidth
  val bottom = top + targetHeight
  setBounds(left, top, right, bottom)
}

/**
 * 设置 Drawable 居中适应宽高按原比例缩放
 */
fun Drawable.setFixCenter(bound: Rect) {
  setFixCenter(bound.width().toFloat(), bound.height().toFloat())
}

/**
 * 设置 Drawable 居中裁剪显示
 */
fun Drawable.setCenterCrop(
  viewWidth: Float,
  viewHeight: Float,
  leftOffset: Int = 0,
  topOffset: Int = 0
) {
  val scale: Float
  var dx = 0f
  var dy = 0f
  val drawableWidth = intrinsicWidth
  val drawableHeight = intrinsicHeight
  if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
    scale = viewHeight / drawableHeight
    dx = (viewWidth - drawableWidth * scale) * 0.5f
  } else {
    scale = viewWidth / drawableWidth
    dy = (viewHeight - drawableHeight * scale) * 0.5f
  }
  val left = leftOffset + dx.toInt()
  val top = topOffset + dy.toInt()
  val right = (left + drawableWidth * scale).toInt()
  val bottom = (top + drawableHeight * scale).toInt()
  setBounds(left, top, right, bottom)
}

/**
 * 设置 Drawable 居中裁剪显示
 */
fun Drawable.setCenterCrop(bounds: Rect) {
  setCenterCrop(bounds.width().toFloat(), bounds.height().toFloat(), bounds.left, bounds.top)
}

/**
 * 压缩 Drawable
 */
fun Drawable.compress(
  output: OutputStream,
  format: CompressFormat = CompressFormat.JPEG,
  quality: Int = 90,
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
): Boolean {
  return toBitmap(width, height, config).compress(format, quality, output)
}

/**
 * 压缩 Drawable
 */
fun Drawable.compressToFile(
  file: File,
  format: CompressFormat = CompressFormat.JPEG,
  quality: Int = 90,
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
): Result<Boolean> {
  return runCatching {
    file.outputStream().use { compress(it, format, quality, width, height, config) }
  }
}

fun Drawable.compressToBytes(
  format: CompressFormat = CompressFormat.JPEG,
  quality: Int = 90,
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
): ByteArray {
  val output = ByteArrayOutputStream()
  compress(output, format, quality, width, height, config)
  return output.toByteArray()
}

fun Drawable.compress(
  format: CompressFormat = CompressFormat.JPEG,
  quality: Int = 90,
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
): Drawable {
  val bytes = compressToBytes(format, quality, width, height, config)
  val inputStream = ByteArrayInputStream(bytes)
  return Drawable.createFromStream(inputStream, "CompressDrawable")!!
}

fun Drawable.toBitmap(
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
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

public fun Drawable.toBitmapOrNull(
  @Px width: Int = intrinsicWidth,
  @Px height: Int = intrinsicHeight,
  config: Config? = null,
): Bitmap? {
  if (this is BitmapDrawable && bitmap == null) {
    return null
  }
  return toBitmap(width, height, config)
}



public fun Drawable.updateBounds(
  @Px left: Int = bounds.left,
  @Px top: Int = bounds.top,
  @Px right: Int = bounds.right,
  @Px bottom: Int = bounds.bottom,
) {
  setBounds(left, top, right, bottom)
}