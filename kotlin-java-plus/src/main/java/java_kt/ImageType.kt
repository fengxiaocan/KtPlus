package java_kt

import java.io.File
import java.io.InputStream

fun File.getImageType(): Result<String> {
  return runCatching { inputStream().use { it.getImageType() } }
}

fun InputStream.getImageType(): String {
  val format = imageFormat()
  if (format.isJPEG()) {
    return "jpeg"
  } else if (format.isPNG()) {
    return "png"
  } else if (format.isWEBP()) {
    return "webp"
  } else if (format.isGIF()) {
    return "gif"
  } else if (format.isTIFF()) {
    return "tiff"
  } else if (format.isHEIC()) {
    return "heic"
  } else if (format.isHEIF()) {
    return "heif"
  } else if (format.isBMP()) {
    return "bmp"
  } else if (format.isICON()) {
    return "icon"
  }
  return "unknown"
}

fun InputStream.imageFormat(): ImageFormat {
  val array = ByteArray(12)
  read(array)
  return ImageFormat(array)
}

class ImageFormat(var data: ByteArray) {
  fun isBMP(): Boolean {
    return check(0x42, 0x4D)
  }

  fun isICON(): Boolean {
    return check(0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x20, 0x20)
  }

  fun isWEBP(): Boolean {
    return check(0, "RIFF") || check(0, "WEBP")
  }

  fun isGIF(): Boolean {
    return check(0x47, 0x49, 0x46, 0x38, 0x37, 0x61) || check(0x47, 0x49, 0x46, 0x38, 0x39, 0x61)
  }

  fun isPNG(): Boolean {
    return check(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
  }

  fun isJPEG(): Boolean {
    return check(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
  }

  fun isTIFF(): Boolean {
    return check(0x49, 0x49, 0x2a, 0x00) || check(0x4D, 0x4D, 0x00, 0x2B) || check(
      0x49, 0x20, 0x49
    ) || check(0x4D, 0x4D, 0x00, 0x2A)
  }

  fun isHEIC(): Boolean {
    return check(4, "ftypheic") || check(4, "ftypheis") || check(4, "ftypheix") || check(
      4, "ftyphevc"
    ) || check(4, "ftyphevx")
  }

  fun isHEIF(): Boolean {
    return check(4, "ftypmif1") || check(4, "ftypmsf1")
  }

  private fun check(vararg code: Byte): Boolean {
    code.forEachIndexed { index, byte ->
      if (data[index] != byte) return false
    }
    return true
  }

  private fun check(skip: Int = 0, str: String): Boolean {
    str.toByteArray().forEachIndexed { index, byte ->
      if (data[index + skip] != byte) return false
    }
    return true
  }

}