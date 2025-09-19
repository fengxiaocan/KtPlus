package java_kt

/**
 * 检查是否已经root
 */
fun checkRootPermission(): Boolean {
  return "echo root".execCmd().getOrNull() == "0"
}

fun String.execCmd(): Result<String> {
  return runCatching {
    val cmd = this
    var textResult = ""
    Runtime.getRuntime().exec(cmd).apply {
      inputStream.bufferedReader().use {
        textResult = it.readText()
      }
      errorStream.bufferedReader().use {
        textResult = it.readText()
      }
      val exitCode = waitFor()
      destroy()
    }
    textResult
  }
}

fun String.processShell(shell: String): Result<Pair<Boolean, String?>> {
  val command = this
  return runCatching {
    ProcessBuilder(shell, "-c", command).start().let {
      var textResult: String? = null
      it.inputStream.bufferedReader().use {
        textResult = it.readText()
      }
      it.errorStream.bufferedReader().use {
        textResult = it.readText()
      }
      val exitCode = it.waitFor()
      it.destroy()
      Pair(exitCode == 0, textResult)
    }
  }
}
