package java_kt

fun forEachStackTrace(startIndex: Int = 3, block: (StackTraceElement) -> Unit) {
  val stackTrace = Thread.currentThread().stackTrace
  for (i in startIndex until stackTrace.size) {
    block(stackTrace[i])
  }
}

fun printStackTrace() {
  forEachStackTrace(4) {
    System.err.println("at ${it.methodName}(${it.className}:${it.lineNumber})")
  }
}