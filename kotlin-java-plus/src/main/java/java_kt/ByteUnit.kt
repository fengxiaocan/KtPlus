package java_kt

enum class ByteUnit(private val bitsPerUnit: Long) {
  BIT(1L),
  B(8L),
  KB(8L * 1024),
  MB(8L * 1024 * 1024),
  GB(8L * 1024 * 1024 * 1024),
  TB(8L * 1024 * 1024 * 1024 * 1024L),
  PB(8L * 1024 * 1024 * 1024 * 1024L * 1024),
  EB(8L * 1024 * 1024 * 1024 * 1024L * 1024 * 1024);

  /** long 转换  */
  fun convert(size: Long, target: ByteUnit): Long {
    // 先转成 bit，再换成目标单位
    return target.fromBits(toBits(size))
  }

  /** double 转换  */
  fun convertDouble(size: Long, target: ByteUnit): Double {
    return toBits(size).toDouble() / target.bitsPerUnit
  }

  /** 格式化输出  */
  fun convert(size: Long, target: ByteUnit, accuracy: Int): String {
    return String.format("%." + accuracy + "f", convertDouble(size, target))
  }

  /** 直接转到指定单位的数值（double 精度）  */
  fun toDouble(size: Long, target: ByteUnit): Double {
    return convertDouble(size, target)
  }

  /** String 转换接口  */
  fun toString(size: Long, target: ByteUnit, accuracy: Int): String {
    return convert(size, target, accuracy)
  }

  private val MAX = Long.Companion.MAX_VALUE

  /** 内部换算工具  */
  private fun toBits(size: Long): Long {
    return x(size, bitsPerUnit, MAX / bitsPerUnit)
  }

  private fun fromBits(bits: Long): Long {
    return bits / bitsPerUnit
  }

  private fun x(d: Long, m: Long, over: Long): Long {
    if (d > over) return Long.Companion.MAX_VALUE
    if (d < -over) return Long.Companion.MIN_VALUE
    return d * m
  }
}