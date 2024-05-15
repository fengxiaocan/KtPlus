package java_kt

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat


fun String.toDecimals(): Sequence<BigDecimal> {
    return "\\d+".toRegex().findAll(this).map { it.value.toBigDecimal() }
}

fun String.toIntegers(): Sequence<BigInteger> {
    return "\\d+".toRegex().findAll(this).map { it.value.toBigInteger() }
}

fun String.toLongs(): Sequence<Long> {
    return "\\d+".toRegex().findAll(this).map { it.value.toLong() }
}

fun String.toInts(): Sequence<Int> {
    return "\\d+".toRegex().findAll(this).map { it.value.toInt() }
}

fun String.toFloats(): Sequence<Float> {
    return "\\d+".toRegex().findAll(this).map { it.value.toFloat() }
}

fun String.toDoubles(): Sequence<Double> {
    return "\\d+".toRegex().findAll(this).map { it.value.toDouble() }
}

fun Float.formatDecimal(pattern: String = "#.00"): String {
    return DecimalFormat(pattern).format(this)
}

fun Double.formatDecimal(pattern: String = "#.00"): String {
    return DecimalFormat(pattern).format(this)
}

fun Float.formatPercent(pattern: String = "#.##%"): String {
    return formatDecimal(pattern)
}

fun Double.formatPercent(pattern: String = "#.##%"): String {
    return formatDecimal(pattern)
}

fun Float.formatFE(pattern: String = "#.#####E0"): String {
    return formatDecimal(pattern)
}

fun Double.formatFE(pattern: String = "#.#####E0"): String {
    return formatDecimal(pattern)
}

fun Double.formatSeparate(pattern: String = ",###"): String {
    return formatDecimal(pattern)
}
