package java_kt

fun forCurrentTraces(block: (index: Int, StackTraceElement) -> Unit) {
    val stackTrace = Thread.currentThread().stackTrace
    stackTrace.forEachIndexed { index, stack ->
        block(index, stack)
    }
}