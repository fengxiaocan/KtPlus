package java_kt

import java.util.Base64


/**
 * 是否是数字
 *  * @receiver Byte
 * @return Boolean
 */
fun Byte.isNumber() = this in 48..57

/**
 * 是否是字母
 * @receiver Byte
 * @return Boolean
 */
fun Byte.isLetter() = this in 65..90 || this in 97..122

/**
 * 如果该字符是字母或数字，则返回 true
 * @receiver Byte
 * @return Boolean
 */
fun Byte.isLetterOrDigit() = this.isLetter() || this.isNumber()

/**
 * 如果该字符是数字则返回 true
 * @receiver Byte
 * @return Boolean
 */
fun Byte.isDigit() = isNumber() || this in 65..70 || this in 97..102

/**
 * 数字转为Int类型 如f转为15,a转为10,48转为0
 * @receiver Byte
 * @return Int
 */
fun Byte.digitToInt(): Int {
  return if (isNumber()) (this - 48)
  else if (this in 65..70) (this - 65 + 10)
  else if (this in 97..102) (this - 97 + 10)
  else throw IllegalArgumentException("Byte $this is not digit int")
}

/**
 * 数字转为Int类型 如f转为15,a转为10,48转为0
 * @receiver Byte
 * @return Int？
 */
fun Byte.digitToIntOrNull(): Int? {
  return if (isNumber()) (this - 48)
  else if (this in 65..70) (this - 65 + 10)
  else if (this in 97..102) (this - 97 + 10)
  else null
}

/**
 * 根据 unicode 标准判断字符是否为空格 如果字符为空格则返回 true
 * @receiver Byte
 * @return Boolean
 */
fun Byte.isSpace() = this.toInt() == 32

fun ByteArray.indexOf(predicate: (Byte) -> Boolean, startIndex: Int = 0): Int {
  for (index in startIndex until this.size) {
    if (predicate(this[index])) {
      return index
    }
  }
  return -1
}

fun ByteArray.indexOf(element: Byte, startIndex: Int = 0): Int {
  for (index in startIndex until this.size) {
    if (element == this[index]) {
      return index
    }
  }
  return -1
}

fun ByteArray.indexOf(element: ByteArray, startIndex: Int): Int {
  if (this.size < element.size) return -1
  var eleIndex = 0
  for (index in startIndex until this.size) {
    if (element[eleIndex] == this[index]) {
      eleIndex++
      if (eleIndex == element.size) {
        return index - eleIndex + 1
      }
    } else {
      eleIndex = 0
    }
  }
  return -1
}

fun ByteArray.base64Encode(): ByteArray {
  return Base64.getEncoder().encode(this)
}

fun ByteArray.base64Decode(): ByteArray {
  return Base64.getDecoder().decode(this)
}

//将字节数组转换为ASCII字符串
fun ByteArray.toAscii(): String {
  return String(this, Charsets.US_ASCII)
}