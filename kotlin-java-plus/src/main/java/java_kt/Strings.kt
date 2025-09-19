package java_kt

import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.Collator
import java.util.Base64
import java.util.Collections
import java.util.Locale


/**
 *  转化为半角字符
 */
fun String.toDBC(): String {
  if (this.isNotEmpty()) {
    val builder = StringBuilder()
    toCharArray().forEach {
      val code = it.code
      if (code == 12288) {
        builder.append(' ')
      } else if (code in 65281..65374) {
        builder.append((code - 65248).toChar())
      } else {
        builder.append(it)
      }
    }
    return builder.toString()
  }
  return this
}

/**
 *  转化为全角字符
 */
fun String.toSBC(): String {
  if (this.isNotEmpty()) {
    val builder = StringBuilder()
    toCharArray().forEach {
      val code = it.code
      if (it == ' ') {
        builder.append(12288.toChar())
      } else if (code in 33..126) {
        builder.append((code + 65248).toChar())
      } else {
        builder.append(it)
      }
    }
    return builder.toString()
  }
  return this
}

/**
 * 限制字符串的长度，超过长度的会截取一部分，末尾显示为自定义 end 字符串
 *
 * @param length  限制长度
 * @param end     待替换字符串b
 */
fun String?.limit(length: Int, end: String? = null): String {
  if (this == null) return ""
  if (this.length > length) {
    return "${this.substring(0, length - 1)}${end.orEmpty()}"
  }
  return this
}

/**
 * 判断字符串中是否包含多个字符串
 * @return
 */
fun String?.containsAll(ignoreCase: Boolean = false, vararg agr: String): Boolean {
  if (this == null) return false
  agr.forEach {
    if (!this.contains(it, ignoreCase)) {
      return@containsAll false
    }
  }
  return true
}

/**
 * 判断字符串中是否包含其中一个字符串
 * @return
 */
fun String?.containsOf(ignoreCase: Boolean = false, vararg agr: String): Boolean {
  if (this == null) return false
  agr.forEach {
    if (this.contains(it, ignoreCase)) {
      return@containsOf true
    }
  }
  return false
}

/**
 * 计算中英文字符串的字节长度
 * @return int 中文占两个字节,英文1个字节
 */
fun String?.charLength(): Int {
  return this?.toCharArray()?.sumOf { (if (it.code > 255) 2 else 1).toInt() } ?: 0
}

/**
 * 获取url的后缀
 */
fun String.fileNameExtension(): String {
  return File(this).extension
}

/**
 * 获取url的中的文件名
 */
fun String.fileNameWithoutExtension(): String {
  return File(this).nameWithoutExtension
}

fun String.toUnicode(): String = this.map {
  val hexString = Integer.toHexString(it.code)
  when (hexString.length) {
    1 -> "\\u000$hexString"
    2 -> "\\u00$hexString"
    3 -> "\\u0$hexString"
    else -> "\\u$hexString"
  }
}.joinToString("")

/**
 * unicode 转字符串
 */
fun String.fromUnicode(): String =
  this.split("\\\\u".toRegex()).joinToString("", transform = {
    if (it.isNotEmpty()) it.toInt(16).toChar().toString() else ""
  })

/**
 * URL解码
 */
fun String.decodeURL(charset: Charset = Charsets.UTF_8): Result<String> =
  kotlin.runCatching { URLDecoder.decode(this, charset.name()) }

/**
 * URL加密
 */
fun String.encodeURL(charset: Charset = Charsets.UTF_8): Result<String> =
  kotlin.runCatching { URLEncoder.encode(this, charset.name()) }


fun String.md5(): ByteArray {
  return toByteArray().md5()
}

fun String.md5Hex(): String {
  return toByteArray().md5Hex()
}

fun String.sha1(): ByteArray {
  return toByteArray().sha1()
}

fun String.sha1Hex(): String {
  return toByteArray().sha1Hex()
}

fun String.sha384(): ByteArray {
  return toByteArray().sha384()
}

fun String.sha384Hex(): String {
  return toByteArray().sha384Hex()
}

fun String.sha224(): ByteArray {
  return toByteArray().sha224()
}

fun String.sha224Hex(): String {
  return toByteArray().sha224Hex()
}

fun String.sha256(): ByteArray {
  return toByteArray().sha256()
}

fun String.sha256Hex(): String {
  return toByteArray().sha256Hex()
}

fun String.sha512(): ByteArray {
  return toByteArray().sha512()
}

fun String.sha512Hex(): String {
  return toByteArray().sha512Hex()
}

fun String.digest(algorithm: String): ByteArray {
  return toByteArray().digest(algorithm)
}

fun String.digestHex(algorithm: String): String {
  return toByteArray().digestHex(algorithm)
}

fun String.base64Encode(charset: Charset = Charsets.UTF_8): String {
  val encode = Base64.getEncoder().encode(toByteArray(charset))
  return String(encode)
}

fun String.base64Decode(charset: Charset = Charsets.UTF_8): String {
  val decode = Base64.getDecoder().decode(toByteArray(charset))
  return String(decode)
}

fun String.crc32(): Long {
  return toByteArray().crc32()
}

fun String.crc32c(): Long {
  return toByteArray().crc32c()
}

fun CharSequence.isChineseCharacters(): Boolean {
  val regex = Regex("[\\u4E00-\\u9FFF]+")
  return regex.matches(this)
}

fun CharSequence.containsChineseCharacters(): Boolean {
  val regex = Regex("[\\u4E00-\\u9FFF]+")
  return regex.containsMatchIn(this)
}

fun CharSequence?.ifNullOrEmpty(block: () -> CharSequence): CharSequence {
  if (isNullOrEmpty()) {
    return block()
  }
  return this
}

/**
 * 汉字排序
 */
fun List<CharSequence>.sortByChineseCharacter(): List<CharSequence> {
  Collections.sort(this, Collator.getInstance(Locale.CHINA))
  return this
}