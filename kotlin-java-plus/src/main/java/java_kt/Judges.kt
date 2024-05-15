package java_kt


/**
 * 判断是否为null,不为null则返回自身,为null则调用block返回新的对象
 * 用于懒创建对象
 * var str:String?=null
 * str = ifNull(str,{"创建对象"})
 */
fun <T> T?.ifNull(block: () -> T): T {
    return this ?: block()
}

/**
 * 判断是否为null,不为null则调用block方法返回另一个对象,为null则调用default返回另一个对象
 * 用于获取对象的值等
 * val size = ifNonNull(array,{array.size},0)
 */
fun <T, R> R?.ifNonNull(block: (R) -> T, default: T): T {
    return this?.let { block(this) } ?: default
}

/**
 * 判断是否为null,不为null则调用block方法返回另一个对象,为null则调用default返回另一个对象
 * 用于获取对象的值等
 * val size = ifNonNull(array,{array.size},{19})
 */
fun <T, R> R?.ifNonNull(block: (R) -> T, default: () -> T): T {
    return this?.let { block(this) } ?: default()
}

/**
 * 三元表达式
 */
fun <T> Boolean?.ifElse(trueValue: T, falseValue: T): T {
    return if (this == true) trueValue else falseValue
}

/**
 * 三元表达式
 */
fun <T> Boolean?.ifElse(trueValue: () -> T, falseValue: () -> T): T {
    return if (this == true) trueValue() else falseValue()
}

/**
 * 判断是否存在于数组中
 */
fun <T> T.isIn(vararg values: T): Boolean {
    return this in values
}

/**
 * 判断是否为true
 */
fun Boolean?.judge(): Boolean = this == true

/**
 * a != b && a!= c && a!= d
 * 如果所有的都不相等,返回true;有一个相等则为false
 * @return
 */
fun <T> T.isUnequalsAll(vararg values: T): Boolean {
    for (element in values) {
        if (element == this) return false
    }
    return true
}


/**
 * a != b || a != c || a != d
 * 如果有一个不相等,返回true
 * @return
 */
fun <T> T.isUnequalsIn(vararg values: T): Boolean {
    for (element in values) {
        if (element != this) return true
    }
    return false
}

/**
 * a == b || a == c || a == d
 * 如果有一个相等,则返回true;否则false
 * @return
 */
fun <T> T.isEqualsIn(vararg values: T): Boolean {
    for (element in values) {
        if (element == this) return true
    }
    return false
}

/**
 * a == b && a == c && a == d
 * 只有所有的都相等,才会返回true;有一个不相等,则为false
 * @return
 */
fun <T> T.isEqualsAll(vararg values: T): Boolean {
    for (element in values) {
        if (element != this) return false
    }
    return true
}