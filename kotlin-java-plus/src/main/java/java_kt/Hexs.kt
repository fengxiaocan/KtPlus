package java_kt


/**
 * 把数字转换为16进制
 */
fun Long.toHex(): String {
  return java.lang.Long.toHexString(this)
}

/**
 * 把数字转换为16进制
 */
fun Int.toHex(): String {
  return Integer.toHexString(this)
}

/**
 * 把数字转换为二进制
 */
fun Long.toBinary(): String {
  return java.lang.Long.toBinaryString(this)
}

/**
 * 把数字转换为二进制
 */
fun Int.toBinary(): String {
  return Integer.toBinaryString(this)
}

/**
 * 把数字转换为8进制
 */
fun Long.toOctal(): String {
  return java.lang.Long.toOctalString(this)
}

/**
 * 把数字转换为8进制
 */
fun Int.toOctal(): String {
  return Integer.toOctalString(this)
}

/**
 * 把8进制为10进制
 */
fun String.octalToInt(): Int {
  return Integer.valueOf(this, 8)
}

/**
 * 把2进制为10进制
 */
fun String.binaryToInt(): Int {
  return Integer.valueOf(this, 2)
}

/**
 * 把16进制为10进制
 */
fun String.hexToInt(): Int {
  return Integer.valueOf(this, 16)
}

/**
 * 把8进制为10进制
 */
fun String.octalToLong(): Long {
  return java.lang.Long.valueOf(this, 8)
}

/**
 * 把2进制为10进制
 */
fun String.binaryToLong(): Long {
  return java.lang.Long.valueOf(this, 2)
}

/**
 * 把16进制为10进制
 */
fun String.hexToLong(): Long {
  return java.lang.Long.valueOf(this, 16)
}

/**
 * 把Byte数组转换成16进制字符串
 */
fun ByteArray.toHex(): String {
  return StringBuilder().also {build->
    forEach {
      val byteInt = it.toInt()
      if (byteInt < 16) {
        build.append('0')
      }
      build.append(byteInt.toHex())
    }
  }.toString()
}