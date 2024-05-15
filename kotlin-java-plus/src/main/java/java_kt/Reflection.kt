package java_kt

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class SClass {
  val aClass: Class<*>

  constructor(aClass: Class<*>) {
    this.aClass = aClass
  }

  @Throws(ClassNotFoundException::class)
  constructor(className: String) {
    this.aClass = ClassLoader.getSystemClassLoader().loadClass(className)
  }

  @Throws(ClassNotFoundException::class)
  constructor(loader: ClassLoader, className: String) {
    this.aClass = loader.loadClass(className)
  }

  @Throws(NoSuchMethodException::class)
  fun method(methodName: String, vararg parameterTypes: Class<*>?): SMethod {
    val declaredMethod = aClass.getDeclaredMethod(methodName, *parameterTypes)
    return SMethod(declaredMethod)
  }

  @Throws(NoSuchMethodException::class)
  fun privateMethod(methodName: String, vararg parameterTypes: Class<*>?): SMethod {
    val declaredMethod = aClass.getDeclaredMethod(methodName, *parameterTypes)
    declaredMethod.isAccessible = true
    return SMethod(declaredMethod)
  }

  @Throws(InstantiationException::class, IllegalAccessException::class)
  fun newInstance(): Any? {
    return aClass.newInstance()
  }

  @Throws(
    NoSuchMethodException::class,
    SecurityException::class,
    InstantiationException::class,
    IllegalAccessException::class
  )
  fun newInstance(parameterTypes: Array<Class<*>?>, parameter: Array<Any?>): Any? {
    val constructor = aClass.getDeclaredConstructor(*parameterTypes)
    constructor.isAccessible = true
    return constructor.newInstance(*parameter)
  }

  @Throws(
    NoSuchMethodException::class,
    SecurityException::class,
    InstantiationException::class,
    IllegalAccessException::class
  )
  fun newInstance(vararg parameter: Any): Any? {
    val constructor = aClass.getDeclaredConstructor(*arrayOfClass(parameter))
    constructor.isAccessible = true
    return constructor.newInstance(*parameter)
  }

  /**
   * 参数变为Class数组
   */
  private inline fun arrayOfClass(vararg parameter: Any): Array<Class<Any>?> {
    var parameterTypes: Array<Class<Any>?> = arrayOfNulls(parameter.size)
    for (i in parameter.indices) {
      parameterTypes[i] = parameter[i].javaClass
    }
    return parameterTypes
  }

  @Throws(NoSuchFieldException::class, SecurityException::class)
  fun field(fieldName: String): Field {
    return aClass.getDeclaredField(fieldName)
  }

  @Throws(NoSuchFieldException::class, SecurityException::class)
  fun getField(fieldName: String): SField {
    val field = aClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return SField(field)
  }

  @Throws(NoSuchFieldException::class, SecurityException::class)
  fun getKtCompanion(): Any? {
    return getField("Companion").getValue()
  }

  @Throws(NoSuchFieldException::class, SecurityException::class)
  fun getKtCompanionClass(): SClass? {
    return getKtCompanion()?.let { SClass(it.javaClass) }
  }
}


class SField(val field: Field) {
  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getValue(obj: Any? = null): Any? {
    return field.get(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getBoolean(obj: Any? = null): Boolean {
    return field.getBoolean(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getByte(obj: Any? = null): Byte {
    return field.getByte(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getChar(obj: Any? = null): Char {
    return field.getChar(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getDouble(obj: Any? = null): Double {
    return field.getDouble(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getFloat(obj: Any? = null): Float {
    return field.getFloat(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getInt(obj: Any? = null): Int {
    return field.getInt(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getLong(obj: Any? = null): Long {
    return field.getLong(obj)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun getShort(obj: Any? = null): Short {
    return field.getShort(obj)
  }

  fun getValueOrNull(obj: Any? = null): Any? {
    try {
      return field.get(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getBooleanOrNull(obj: Any? = null): Boolean? {
    try {
      return field.getBoolean(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getByteOrNull(obj: Any? = null): Byte? {
    try {
      return field.getByte(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getCharOrNull(obj: Any? = null): Char? {
    try {
      return field.getChar(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getDoubleOrNull(obj: Any? = null): Double? {
    try {
      return field.getDouble(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getFloatOrNull(obj: Any? = null): Float? {
    try {
      return field.getFloat(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getIntOrNull(obj: Any? = null): Int? {
    try {
      return field.getInt(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getLongOrNull(obj: Any? = null): Long? {
    try {
      return field.getLong(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  fun getShortOrNull(obj: Any? = null): Short? {
    try {
      return field.getShort(obj)
    } catch (e: Throwable) {
      return null
    }
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setValue(obj: Any? = null, value: Any? = null) {
    field.set(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setBoolean(obj: Any? = null, value: Boolean = false) {
    field.setBoolean(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setByte(obj: Any? = null, value: Byte = 0) {
    return field.setByte(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setChar(obj: Any? = null, value: Char = 0.toChar()) {
    return field.setChar(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setDouble(obj: Any? = null, value: Double = 0.0) {
    return field.setDouble(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setFloat(obj: Any? = null, value: Float = 0f) {
    return field.setFloat(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setInt(obj: Any? = null, value: Int = 0) {
    return field.setInt(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setLong(obj: Any? = null, value: Long = 0L) {
    return field.setLong(obj, value)
  }

  @Throws(IllegalArgumentException::class, IllegalAccessException::class)
  fun setShort(obj: Any? = null, value: Short = 0) {
    return field.setShort(obj, value)
  }
}

class SMethod(val method: Method) {
  @Throws(InvocationTargetException::class, IllegalAccessException::class)
  operator fun invoke(obj: Any?, vararg args: Any?): Any {
    return method.invoke(obj, *args)
  }
}