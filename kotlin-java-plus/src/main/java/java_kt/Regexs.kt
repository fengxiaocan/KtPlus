package java_kt

/**
 * 判断一个字符串是不是数字
 */
fun CharSequence.isNumber(): Boolean {
  return "-?[0-9]+.?[0-9]+".toRegex().matches(this)
}

fun CharSequence.isEmail(): Boolean {
  return "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".toRegex().matches(this)
}

fun CharSequence.isURL(): Boolean {
  return "[a-zA-z]+://\\S*".toRegex().matches(this)
}

/**
 * 用户名，取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位
 */
fun CharSequence.isUserName(): Boolean {
  return "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$".toRegex().matches(this)
}

/**
 * 是否是中文汉字
 */
fun CharSequence.isZH(): Boolean {
  return "^[\\u4e00-\\u9fa5]+$".toRegex().matches(this)
}

/**
 * 是否是IP地址
 */
fun CharSequence.isIpAddress(): Boolean {
  return "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)".toRegex()
    .matches(this)
}

/**
 * 是否是双字节字符(包括汉字在内)
 */
fun CharSequence.isDoubleByteChar(): Boolean {
  return "[^\\x00-\\xff]".toRegex().matches(this)
}

/**
 * 是否是空白行
 */
fun CharSequence.isBlankLine(): Boolean {
  return "\\n\\s*\\r".toRegex().matches(this)
}

/**
 * 是否是正整数
 */
fun CharSequence.isPositiveInt(): Boolean {
  return "^[1-9]\\d*$".toRegex().matches(this)
}

/**
 * 是否是负整数
 */
fun CharSequence.isNegativeInt(): Boolean {
  return "^-[1-9]\\d*$".toRegex().matches(this)
}

/**
 * 正则：整数
 */
fun CharSequence.isInteger(): Boolean {
  return "^-?[1-9]\\d*$".toRegex().matches(this)
}

/**
 * 正则：非负整数(正整数 + 0)
 */
fun CharSequence.isNotNegativeInteger(): Boolean {
  return "^[1-9]\\d*|0$".toRegex().matches(this)
}

/**
 * 正则：非正整数（负整数 + 0）
 */
fun CharSequence.isNotPositiveInteger(): Boolean {
  return "^-[1-9]\\d*|0$".toRegex().matches(this)
}

/**
 * 正则：正浮点数
 */
fun CharSequence.isPositiveFloat(): Boolean {
  return "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$".toRegex().matches(this)
}

/**
 * 正则：负浮点数
 */
fun CharSequence.isNegativeFloat(): Boolean {
  return "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$".toRegex().matches(this)
}

/**
 * 正则:匹配URL中的img src
 */
fun CharSequence.isImgSrcTag(): Boolean {
  return "<img\\b[^>]*?\\bsrc[\\s]*=[\\s]*[\"']?[\\s]*(?<imgUrl>[^\"'>]*)[^>]*?/?[\\s]*>".toRegex()
    .matches(this)
}

/**
 * 是否是HttpUrl
 */
fun CharSequence.isHttpUrl(): Boolean {
  return "(https?|ftp)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]".toRegex()
    .matches(this)
}

fun CharSequence.isImgSrc(): Boolean {
  return "src=('|\")[^'\"]+('|\")".toRegex().matches(this)
}

fun CharSequence.isHref(): Boolean {
  return "href=('|\")[^'\"]+('|\")".toRegex().matches(this)
}