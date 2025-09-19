package android_kt

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import androidx.annotation.FloatRange
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


fun Bitmap.saveToFile(
  file: File, format: CompressFormat = CompressFormat.PNG, quality: Int = 100,
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
  maxSize: Int, format: CompressFormat = CompressFormat.JPEG, decreasingLevel: Int = 5,
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
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100,
): ByteArray {
  return toOutputStream(format, quality).toByteArray()
}

fun Bitmap.toOutputStream(
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100,
): ByteArrayOutputStream {
  return ByteArrayOutputStream().apply { compress(format, quality, this) }
}

fun Bitmap.copy(
  format: CompressFormat = CompressFormat.JPEG, quality: Int = 100,
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
  isBaseMax: Boolean = true, config: Config = Config.RGB_565,
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
  isBaseMax: Boolean = true, config: Config = Config.RGB_565,
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

/**
 * 旋转图片
 *
 * @param degrees 旋转角度
 * @param px      旋转点横坐标
 * @param py      旋转点纵坐标
 * @return 旋转后的图片
 */
fun Bitmap.rotate(degrees: Float = 180f, px: Float = width / 2f, py: Float = height / 2f): Bitmap {
  if (degrees == 0f) {
    return this
  }
  val matrix = Matrix()
  matrix.setRotate(degrees, px, py)
  val ret = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
  return ret
}

/**
 * 倾斜图片
 *
 * @param kx      倾斜因子x
 * @param ky      倾斜因子y
 * @param px      平移因子x
 * @param py      平移因子y
 * @return 倾斜后的图片
 */
fun Bitmap.skew(kx: Float, ky: Float, px: Float, py: Float): Bitmap {
  val matrix = Matrix()
  matrix.setSkew(kx, ky, px, py)
  val ret = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
  return ret
}

/**
 * 图片位移
 *
 * @param dx      倾斜因子x
 * @param dy      倾斜因子y
 * @return 倾斜后的图片
 */
fun Bitmap.translate(dx: Float, dy: Float): Bitmap {
  val matrix = Matrix()
  matrix.setTranslate(dx, dy)
  val ret = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
  return ret
}

/**
 * 转为圆角图片
 *
 * @param radius  圆角的度数
 * @return 圆角图片
 */
fun Bitmap.addCorner(
  radius: Float = 0f, strokeWidth: Float = 0f, color: Int = Color.TRANSPARENT,
): Bitmap {
  val ret = Bitmap.createBitmap(width, height, getConfig())
  val canvas = Canvas(ret)
  val rect = Rect(0, 0, width, height)
  val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  paint.isAntiAlias = true
  if (strokeWidth > 0) {
    paint.setColor(color)
    paint.style = Paint.Style.STROKE
    // setStrokeWidth是居中画的，所以要两倍的宽度才能画，否则有一半的宽度是空的
    paint.strokeWidth = strokeWidth
  }
  if (radius > 0) {
    canvas.drawRoundRect(RectF(rect), radius, radius, paint)
  } else {
    canvas.drawRect(rect, paint)
  }
  paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
  canvas.drawBitmap(this, rect, rect, paint)
  return ret
}

/**
 * 添加文字水印
 *
 * @param content  水印文本
 * @param textSize 水印字体大小
 * @param color    水印字体颜色
 * @param x        起始坐标x
 * @param y        起始坐标y
 * @return 带有文字水印的图片
 */
fun Bitmap.addTextWatermark(
  content: String, textSize: Float, color: Int, x: Float, y: Float,
): Bitmap? {
  val ret = this.copy(getConfig(), true)
  val canvas = Canvas(ret)
  val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  paint.setColor(color)
  paint.textSize = textSize
  val bounds = Rect()
  paint.getTextBounds(content, 0, content.length, bounds)
  canvas.drawText(content, x, y + textSize, paint)
  return ret
}

/**
 * 添加图片水印
 *
 * @param watermark 图片水印
 * @param x         起始坐标x
 * @param y         起始坐标y
 * @param alpha     透明度
 * @param recycle   是否回收
 * @return 带有图片水印的图片
 */
fun Bitmap.addImageWatermark(
  watermark: Bitmap, x: Int = 0, y: Int = 0, alpha: Int = 255,
): Bitmap {
  val ret = this.copy(this.getConfig(), true)
  val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  val canvas = Canvas(ret)
  paint.setAlpha(alpha)
  canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
  return ret
}

/**
 * 快速模糊图片
 *
 * 先缩小原图，对小图进行模糊，再放大回原先尺寸
 *
 * @param scale   缩放比例(0...1)
 * @param radius  模糊半径(0...25)
 * @param recycle 是否回收
 * @return 模糊后的图片
 */
fun Bitmap.blur(
  context: Context,
  @FloatRange(from = 0.toDouble(), to = 25.toDouble(), fromInclusive = false) radius: Float = 10f,
): Bitmap? {
  val ret = this.copy(this.getConfig(), true)
  val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
  val canvas = Canvas()
  val filter = PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP)
  paint.setColorFilter(filter)
  canvas.drawBitmap(ret, 0f, 0f, paint)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val node = RenderNode("BlurViewNode")
    val canvas: Canvas = node.beginRecording()
    canvas.drawBitmap(ret, 0f, 0f, null)
    node.endRecording()
    node.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP))
    return ret
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    return ret.renderScriptBlur(context, radius)
  } else {
    return ret.stackBlur(radius.toInt())
  }
}

/**
 * renderScript模糊图片
 *
 * API大于17
 *
 * @param src    源图片
 * @param radius 模糊半径(0...25)
 * @return 模糊后的图片
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Bitmap.renderScriptBlur(
  context: Context,
  @FloatRange(from = 0.toDouble(), to = 25.toDouble(), fromInclusive = false) radius: Float,
): Bitmap? {
  var rs: RenderScript? = null
  try {
    rs = RenderScript.create(context)
    rs.messageHandler = RenderScript.RSMessageHandler()
    val input = Allocation.createFromBitmap(
      rs, this, Allocation.MipmapControl.MIPMAP_NONE,
      Allocation.USAGE_SCRIPT
    )
    val output = Allocation.createTyped(rs, input.getType())
    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    blurScript.setInput(input)
    blurScript.setRadius(radius)
    blurScript.forEach(output)
    output.copyTo(this)
  } finally {
    rs?.destroy()
  }
  return this
}

/**
 * stack模糊图片
 *
 * @param src     源图片
 * @param radius  模糊半径
 * @param recycle 是否回收
 * @return stack模糊后的图片
 */
fun Bitmap.stackBlur(radius: Int): Bitmap? {
  val ret: Bitmap = this.copy(this.getConfig(), true)

  if (radius < 1) {
    return null
  }
  val w = ret.getWidth()
  val h = ret.getHeight()
  val pix = IntArray(w * h)
  ret.getPixels(pix, 0, w, 0, 0, w, h)
  val wm = w - 1
  val hm = h - 1
  val wh = w * h
  val div = radius + radius + 1
  val r = IntArray(wh)
  val g = IntArray(wh)
  val b = IntArray(wh)
  var rsum: Int
  var gsum: Int
  var bsum: Int
  var x: Int
  var y: Int
  var i: Int
  var p: Int
  var yp: Int
  var yi: Int
  var yw: Int
  val vmin = IntArray(max(w, h))
  var divsum = (div + 1) shr 1
  divsum *= divsum
  val dv = IntArray(256 * divsum)
  i = 0
  while (i < 256 * divsum) {
    dv[i] = (i / divsum)
    i++
  }

  yi = 0
  yw = yi
  val stack: Array<IntArray> = Array<IntArray>(div) { IntArray(3) }
  var stackpointer: Int
  var stackstart: Int
  var sir: IntArray
  var rbs: Int
  val r1 = radius + 1
  var routsum: Int
  var goutsum: Int
  var boutsum: Int
  var rinsum: Int
  var ginsum: Int
  var binsum: Int

  y = 0
  while (y < h) {
    bsum = 0
    gsum = bsum
    rsum = gsum
    boutsum = rsum
    goutsum = boutsum
    routsum = goutsum
    binsum = routsum
    ginsum = binsum
    rinsum = ginsum
    i = -radius
    while (i <= radius) {
      p = pix[yi + min(wm, max(i, 0))]
      sir = stack[i + radius]
      sir[0] = (p and 0xff0000) shr 16
      sir[1] = (p and 0x00ff00) shr 8
      sir[2] = (p and 0x0000ff)
      rbs = r1 - abs(i)
      rsum += sir[0] * rbs
      gsum += sir[1] * rbs
      bsum += sir[2] * rbs
      if (i > 0) {
        rinsum += sir[0]
        ginsum += sir[1]
        binsum += sir[2]
      } else {
        routsum += sir[0]
        goutsum += sir[1]
        boutsum += sir[2]
      }
      i++
    }
    stackpointer = radius

    x = 0
    while (x < w) {
      r[yi] = dv[rsum]
      g[yi] = dv[gsum]
      b[yi] = dv[bsum]

      rsum -= routsum
      gsum -= goutsum
      bsum -= boutsum

      stackstart = stackpointer - radius + div
      sir = stack[stackstart % div]

      routsum -= sir[0]
      goutsum -= sir[1]
      boutsum -= sir[2]

      if (y == 0) {
        vmin[x] = min(x + radius + 1, wm)
      }
      p = pix[yw + vmin[x]]

      sir[0] = (p and 0xff0000) shr 16
      sir[1] = (p and 0x00ff00) shr 8
      sir[2] = (p and 0x0000ff)

      rinsum += sir[0]
      ginsum += sir[1]
      binsum += sir[2]

      rsum += rinsum
      gsum += ginsum
      bsum += binsum

      stackpointer = (stackpointer + 1) % div
      sir = stack[(stackpointer) % div]

      routsum += sir[0]
      goutsum += sir[1]
      boutsum += sir[2]

      rinsum -= sir[0]
      ginsum -= sir[1]
      binsum -= sir[2]

      yi++
      x++
    }
    yw += w
    y++
  }
  x = 0
  while (x < w) {
    bsum = 0
    gsum = bsum
    rsum = gsum
    boutsum = rsum
    goutsum = boutsum
    routsum = goutsum
    binsum = routsum
    ginsum = binsum
    rinsum = ginsum
    yp = -radius * w
    i = -radius
    while (i <= radius) {
      yi = max(0, yp) + x

      sir = stack[i + radius]

      sir[0] = r[yi]
      sir[1] = g[yi]
      sir[2] = b[yi]

      rbs = r1 - abs(i)

      rsum += r[yi] * rbs
      gsum += g[yi] * rbs
      bsum += b[yi] * rbs

      if (i > 0) {
        rinsum += sir[0]
        ginsum += sir[1]
        binsum += sir[2]
      } else {
        routsum += sir[0]
        goutsum += sir[1]
        boutsum += sir[2]
      }

      if (i < hm) {
        yp += w
      }
      i++
    }
    yi = x
    stackpointer = radius
    y = 0
    while (y < h) {
      // Preserve alpha channel: ( 0xff000000 & pix[yi] )
      pix[yi] = (-0x1000000 and pix[yi]) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

      rsum -= routsum
      gsum -= goutsum
      bsum -= boutsum

      stackstart = stackpointer - radius + div
      sir = stack[stackstart % div]

      routsum -= sir[0]
      goutsum -= sir[1]
      boutsum -= sir[2]

      if (x == 0) {
        vmin[y] = min(y + r1, hm) * w
      }
      p = x + vmin[y]

      sir[0] = r[p]
      sir[1] = g[p]
      sir[2] = b[p]

      rinsum += sir[0]
      ginsum += sir[1]
      binsum += sir[2]

      rsum += rinsum
      gsum += ginsum
      bsum += binsum

      stackpointer = (stackpointer + 1) % div
      sir = stack[stackpointer]

      routsum += sir[0]
      goutsum += sir[1]
      boutsum += sir[2]

      rinsum -= sir[0]
      ginsum -= sir[1]
      binsum -= sir[2]

      yi += w
      y++
    }
    x++
  }
  ret.setPixels(pix, 0, w, 0, 0, w, h)
  return ret
}

/**
 * 转为alpha位图
 * @return alpha位图
 */
fun Bitmap.alpha(): Bitmap {
  return this.extractAlpha()
}

/**
 * 转为灰度图片
 *
 * @param src     源图片
 * @param recycle 是否回收
 * @return 灰度图
 */
fun Bitmap.toGray(): Bitmap {
  val grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(grayBitmap)
  val paint = Paint()
  val colorMatrix = ColorMatrix()
  colorMatrix.setSaturation(0f)
  val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
  paint.setColorFilter(colorMatrixColorFilter)
  canvas.drawBitmap(this, 0f, 0f, paint)
  return grayBitmap
}

/**
 * 把bitmap转换成base64
 */
fun Bitmap.toBase64FromBitmap(bitmapQuality: Int = 100): String {
  val bStream = ByteArrayOutputStream()
  this.compress(CompressFormat.PNG, bitmapQuality, bStream)
  val bytes = bStream.toByteArray()
  return Base64.encodeToString(bytes, Base64.DEFAULT)
}

/**
 * 把base64转换成bitmap
 */
fun String.base64ToBitmap(): Bitmap {
  var bitmapArray: ByteArray = Base64.decode(this, Base64.DEFAULT)
  return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
}

/**
 * 水平翻转处理
 *
 * @param bitmap 原图
 * @return 水平翻转后的图片
 */
fun Bitmap.reverseHorizontal(): Bitmap {
  val matrix = Matrix()
  matrix.preScale(-1f, 1f)
  return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

/**
 * 垂直翻转处理
 *
 * @param bitmap 原图
 * @return 垂直翻转后的图片
 */
fun Bitmap.reverseVertical(): Bitmap {
  val matrix = Matrix()
  matrix.preScale(1f, -1f)
  return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

/**
 * 更改图片色系，变亮或变暗
 *
 * @param delta 图片的亮暗程度值，越小图片会越亮，取值范围(0,24)
 * @return
 */
fun Bitmap.adjustTone(delta: Int): Bitmap? {
  if (delta >= 24 || delta <= 0) {
    return null
  }
  // 设置高斯矩阵
  val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
  val bitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  var idx = 0
  val pixels = IntArray(width * height)

  getPixels(pixels, 0, width, 0, 0, width, height)
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      idx = 0
      for (m in -1..1) {
        for (n in -1..1) {
          pixColor = pixels[(i + m) * width + k + n]
          pixR = Color.red(pixColor)
          pixG = Color.green(pixColor)
          pixB = Color.blue(pixColor)

          newR += (pixR * gauss[idx])
          newG += (pixG * gauss[idx])
          newB += (pixB * gauss[idx])
          idx++
        }
      }
      newR /= delta
      newG /= delta
      newB /= delta
      newR = min(255, max(0, newR))
      newG = min(255, max(0, newG))
      newB = min(255, max(0, newB))
      pixels[i * width + k] = Color.argb(255, newR, newG, newB)
      newR = 0
      newG = 0
      newB = 0
      k++
    }
    i++
  }
  bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return bitmap
}

/**
 * 将彩色图转换为黑白图
 *
 * @param bmp 位图
 * @return 返回转换好的位图
 */
fun Bitmap.toBlackWhite(): Bitmap {
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  val alpha = 0xFF shl 24 // 默认将bitmap当成24色图片
  for (i in 0..<height) {
    for (j in 0..<width) {
      var grey = pixels[width * i + j]
      val red = ((grey and 0x00FF0000) shr 16)
      val green = ((grey and 0x0000FF00) shr 8)
      val blue = (grey and 0x000000FF)

      grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
      grey = alpha or (grey shl 16) or (grey shl 8) or grey
      pixels[width * i + j] = grey
    }
  }
  val newBmp = Bitmap.createBitmap(width, height, Config.RGB_565)
  newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBmp
}

/**
 * 饱和度处理
 *
 * @param bitmap          原图
 * @param saturationValue 新的饱和度值
 * @return 改变了饱和度值之后的图片
 */
fun Bitmap.saturation(saturationValue: Int): Bitmap {
  // 计算出符合要求的饱和度值
  val newSaturationValue = saturationValue * 1.0f / 127
  // 创建一个颜色矩阵
  val saturationColorMatrix = ColorMatrix()
  // 设置饱和度值
  saturationColorMatrix.setSaturation(newSaturationValue)
  // 创建一个画笔并设置其颜色过滤器
  val paint = Paint()
  paint.setColorFilter(ColorMatrixColorFilter(saturationColorMatrix))
  // 创建一个新的图片并创建画布
  val newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
  val canvas = Canvas(newBitmap)
  // 将原图使用给定的画笔画到画布上
  canvas.drawBitmap(this, 0f, 0f, paint)
  return newBitmap
}

/**
 * 亮度处理
 *
 * @param bitmap   原图
 * @param lumValue 新的亮度值
 * @return 改变了亮度值之后的图片
 */
fun Bitmap.lum(lumValue: Int): Bitmap {
  // 计算出符合要求的亮度值
  val newlumValue = lumValue * 1.0f / 127
  // 创建一个颜色矩阵
  val lumColorMatrix = ColorMatrix()
  // 设置亮度值
  lumColorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
  // 创建一个画笔并设置其颜色过滤器
  val paint = Paint()
  paint.setColorFilter(ColorMatrixColorFilter(lumColorMatrix))
  // 创建一个新的图片并创建画布
  val newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
  val canvas = Canvas(newBitmap)
  // 将原图使用给定的画笔画到画布上
  canvas.drawBitmap(this, 0f, 0f, paint)
  return newBitmap
}

/**
 * 色相处理
 *
 * @param bitmap   原图
 * @param hueValue 新的色相值
 * @return 改变了色相值之后的图片
 */
fun Bitmap.hue(hueValue: Int): Bitmap {
  // 计算出符合要求的色相值
  val newHueValue = (hueValue - 127) * 1.0f / 127 * 180
  // 创建一个颜色矩阵
  val hueColorMatrix = ColorMatrix()
  // 控制让红色区在色轮上旋转的角度
  hueColorMatrix.setRotate(0, newHueValue)
  // 控制让绿红色区在色轮上旋转的角度
  hueColorMatrix.setRotate(1, newHueValue)
  // 控制让蓝色区在色轮上旋转的角度
  hueColorMatrix.setRotate(2, newHueValue)
  // 创建一个画笔并设置其颜色过滤器
  val paint = Paint()
  paint.setColorFilter(ColorMatrixColorFilter(hueColorMatrix))
  // 创建一个新的图片并创建画布
  val newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
  val canvas = Canvas(newBitmap)
  // 将原图使用给定的画笔画到画布上
  canvas.drawBitmap(this, 0f, 0f, paint)
  return newBitmap
}

/**
 * 亮度、色相、饱和度处理
 *
 * @param bitmap          原图
 * @param lumValue        亮度值
 * @param hueValue        色相值
 * @param saturationValue 饱和度值
 * @return 亮度、色相、饱和度处理后的图片
 */
fun Bitmap.lumAndHueAndSaturation(
  lumValue: Int, hueValue: Int, saturationValue: Int,
): Bitmap {
  // 计算出符合要求的饱和度值
  val newSaturationValue = saturationValue * 1.0f / 127
  // 计算出符合要求的亮度值
  val newlumValue = lumValue * 1.0f / 127
  // 计算出符合要求的色相值
  val newHueValue = (hueValue - 127) * 1.0f / 127 * 180
  // 创建一个颜色矩阵并设置其饱和度
  val colorMatrix = ColorMatrix()
  // 设置饱和度值
  colorMatrix.setSaturation(newSaturationValue)
  // 设置亮度值
  colorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
  // 控制让红色区在色轮上旋转的角度
  colorMatrix.setRotate(0, newHueValue)
  // 控制让绿红色区在色轮上旋转的角度
  colorMatrix.setRotate(1, newHueValue)
  // 控制让蓝色区在色轮上旋转的角度
  colorMatrix.setRotate(2, newHueValue)
  // 创建一个画笔并设置其颜色过滤器
  val paint = Paint()
  paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
  // 创建一个新的图片并创建画布
  val newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
  val canvas = Canvas(newBitmap)
  // 将原图使用给定的画笔画到画布上
  canvas.drawBitmap(this, 0f, 0f, paint)
  return newBitmap
}

/**
 * 怀旧效果
 *
 * @param bitmap
 * @return
 */
fun Bitmap.nostalgic(): Bitmap {
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixColor = 0
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var newR = 0
  var newG = 0
  var newB = 0
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  for (i in 0..<height) {
    for (k in 0..<width) {
      pixColor = pixels[width * i + k]
      pixR = Color.red(pixColor)
      pixG = Color.green(pixColor)
      pixB = Color.blue(pixColor)
      newR = (0.393 * pixR + 0.769 * pixG + 0.189 * pixB).toInt()
      newG = (0.349 * pixR + 0.686 * pixG + 0.168 * pixB).toInt()
      newB = (0.272 * pixR + 0.534 * pixG + 0.131 * pixB).toInt()
      val newColor = Color.argb(
        255, if (newR > 255) 255 else newR, if (newG > 255) 255 else newG,
        if (newB > 255) 255 else newB
      )
      pixels[width * i + k] = newColor
    }
  }
  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 柔化效果
 *
 * @param bitmap
 * @return
 */
fun Bitmap.soften(): Bitmap {
  // 高斯矩阵
  val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  val delta = 16 // 值越小图片会越亮，越大则越暗
  var idx = 0
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      idx = 0
      for (m in -1..1) {
        for (n in -1..1) {
          pixColor = pixels[(i + m) * width + k + n]
          pixR = Color.red(pixColor)
          pixG = Color.green(pixColor)
          pixB = Color.blue(pixColor)

          newR = newR + (pixR * gauss[idx])
          newG = newG + (pixG * gauss[idx])
          newB = newB + (pixB * gauss[idx])
          idx++
        }
      }

      newR /= delta
      newG /= delta
      newB /= delta

      newR = min(255, max(0, newR))
      newG = min(255, max(0, newG))
      newB = min(255, max(0, newB))

      pixels[i * width + k] = Color.argb(255, newR, newG, newB)

      newR = 0
      newG = 0
      newB = 0
      k++
    }
    i++
  }

  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 光照效果
 *
 * @param bitmap
 * @param centerX 光源在X轴的位置
 * @param centerY 光源在Y轴的位置
 * @return
 */
fun Bitmap.sunshine(centerX: Int, centerY: Int): Bitmap {
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  val radius = min(centerX, centerY)
  val strength = 150f // 光照强度 100~150
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  var pos = 0
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      pos = i * width + k
      pixColor = pixels[pos]

      pixR = Color.red(pixColor)
      pixG = Color.green(pixColor)
      pixB = Color.blue(pixColor)

      newR = pixR
      newG = pixG
      newB = pixB
      // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
      val distance = ((centerY - i).toDouble().pow(2.0) + (centerX - k).toDouble().pow(2.0)).toInt()
      if (distance < radius * radius) {
        // 按照距离大小计算增加的光照值
        val result = (strength * (1.0 - sqrt(distance.toDouble()) / radius)).toInt()
        newR = pixR + result
        newG = pixG + result
        newB = pixB + result
      }

      newR = min(255, max(0, newR))
      newG = min(255, max(0, newG))
      newB = min(255, max(0, newB))

      pixels[pos] = Color.argb(255, newR, newG, newB)
      k++
    }
    i++
  }

  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 底片效果
 * @return
 */
fun Bitmap.film(): Bitmap {
  // RGBA的最大值
  val MAX_VALUE = 255
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  var pos = 0
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      pos = i * width + k
      pixColor = pixels[pos]

      pixR = Color.red(pixColor)
      pixG = Color.green(pixColor)
      pixB = Color.blue(pixColor)

      newR = MAX_VALUE - pixR
      newG = MAX_VALUE - pixG
      newB = MAX_VALUE - pixB

      newR = min(MAX_VALUE, max(0, newR))
      newG = min(MAX_VALUE, max(0, newG))
      newB = min(MAX_VALUE, max(0, newB))

      pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB)
      k++
    }
    i++
  }

  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 锐化效果
 *
 * @return
 */
fun Bitmap.sharpen(): Bitmap {
  // 拉普拉斯矩阵
  val laplacian = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  var idx = 0
  val alpha = 0.3f
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      idx = 0
      for (m in -1..1) {
        for (n in -1..1) {
          pixColor = pixels[(i + n) * width + k + m]
          pixR = Color.red(pixColor)
          pixG = Color.green(pixColor)
          pixB = Color.blue(pixColor)

          newR = newR + (pixR * laplacian[idx] * alpha).toInt()
          newG = newG + (pixG * laplacian[idx] * alpha).toInt()
          newB = newB + (pixB * laplacian[idx] * alpha).toInt()
          idx++
        }
      }

      newR = min(255, max(0, newR))
      newG = min(255, max(0, newG))
      newB = min(255, max(0, newB))

      pixels[i * width + k] = Color.argb(255, newR, newG, newB)
      newR = 0
      newG = 0
      newB = 0
      k++
    }
    i++
  }

  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 浮雕效果
 * @return
 */
fun Bitmap.emboss(): Bitmap {
  val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
  var pixR = 0
  var pixG = 0
  var pixB = 0
  var pixColor = 0
  var newR = 0
  var newG = 0
  var newB = 0
  val pixels = IntArray(width * height)
  getPixels(pixels, 0, width, 0, 0, width, height)
  var pos = 0
  var i = 1
  val length = height - 1
  while (i < length) {
    var k = 1
    val len = width - 1
    while (k < len) {
      pos = i * width + k
      pixColor = pixels[pos]

      pixR = Color.red(pixColor)
      pixG = Color.green(pixColor)
      pixB = Color.blue(pixColor)

      pixColor = pixels[pos + 1]
      newR = Color.red(pixColor) - pixR + 127
      newG = Color.green(pixColor) - pixG + 127
      newB = Color.blue(pixColor) - pixB + 127

      newR = min(255, max(0, newR))
      newG = min(255, max(0, newG))
      newB = min(255, max(0, newB))

      pixels[pos] = Color.argb(255, newR, newG, newB)
      k++
    }
    i++
  }

  newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
  return newBitmap
}

/**
 * 给图片染色
 *
 * @param tintColor
 * @return
 */
fun Bitmap.tintBitmap(tintColor: Int): Bitmap {
  val outBitmap =
    Bitmap.createBitmap(width, height, getConfig())
  val canvas = Canvas(outBitmap)
  val paint = Paint()
  paint.setColorFilter(PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN))
  canvas.drawBitmap(this, 0f, 0f, paint)
  return outBitmap
}