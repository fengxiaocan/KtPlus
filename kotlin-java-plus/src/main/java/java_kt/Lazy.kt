package java_kt

class LazyData<T>(val initializer: () -> T, val destroy: T.() -> Unit, lock: Any? = null) {
  private val lock = lock ?: this
  private var data: T? = null

  fun getOrNull(): T? {
    synchronized(lock) {
      return data
    }
  }

  fun get(): T {
    synchronized(lock) {
      if (data == null) {
        data = initializer()
      }
      return data!!
    }
  }

  fun release() {
    synchronized(lock) {
      data?.destroy()
      data = null
    }
  }
}