package java_kt

/**
 * 给颜色设置透明度
 * @param alpha 颜色的透明度
 * @return
 */
inline fun Int.setColorAlpha(alpha: Int): Int {
    return this and 0xFFFFFF or (alpha shl 24)
}

/**
 * 给颜色设置red 色值
 *
 * @param red 红色值
 * @return
 */
inline fun Int.setColorRed(red: Int): Int {
    return this and -0xff0001 or (red shl 16)
}

/**
 * 给颜色设置green 色值
 *
 * @param green
 * @return
 */
inline fun Int.setColorGreen(green: Int): Int {
    return this and -0xff01 or (green shl 8)
}

/**
 * 给颜色设置blue 色值
 * @param blue
 * @return
 */
inline fun Int.setColorBlue(blue: Int): Int {
    return this and -0x100 or blue
}

/**
 * 给颜色设置Alpha
 * @param alpha
 * @return
 */
inline fun Int.setColorAlpha(alpha: Float): Int {
    return this and 0xFFFFFF or ((alpha * 255.0f + 0.5f).toInt() shl 24)
}

/**
 * 给颜色设置red 色值
 *
 * @param red
 * @return
 */
inline fun Int.setColorRed(red: Float): Int {
    return this and -0xff0001 or ((red * 255.0f + 0.5f).toInt() shl 16)
}

/**
 * 给颜色设置green 色值
 *
 * @param green
 * @return
 */
inline fun Int.setColorGreen(green: Float): Int {
    return this and -0xff01 or ((green * 255.0f + 0.5f).toInt() shl 8)
}

/**
 * 给颜色设置blue 色值
 *
 * @param blue
 * @return
 */
inline fun Int.setColorBlue(blue: Float): Int {
    return this and -0x100 or (blue * 255.0f + 0.5f).toInt()
}

/**
 * 颜色值的红色
 */
inline fun Int.colorRed(): Float {
    return (this shr 16 and 0xff) / 255.0f
}

/**
 * 颜色值的绿色
 */
inline fun Int.colorGreen(): Float {
    return (this shr 8 and 0xff) / 255.0f
}

/**
 * 颜色值的蓝色
 */
inline fun Int.colorBlue(): Float {
    return (this and 0xff) / 255.0f
}

/**
 * 颜色值的透明度
 */
inline fun Int.colorAlpha(): Float {
    return (this shr 24 and 0xff) / 255.0f
}

/**
 * 颜色的红色值
 */
inline fun Int.red(): Int {
    return this shr 16 and 0xFF
}

/**
 * 颜色的绿色值
 */
inline fun Int.green(): Int {
    return this shr 8 and 0xFF
}

/**
 * 颜色的蓝色值
 */
inline fun Int.blue(): Int {
    return this and 0xFF
}

/**
 * 颜色的透明值
 */
inline fun Int.alpha(): Int {
    return this ushr 24
}

/**
 * 颜色的透明值
 */
inline fun Int.toColorHex(): String {
    return "0x${Integer.toHexString(this).uppercase()}"
}
