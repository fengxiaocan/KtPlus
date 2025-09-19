package java_kt

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.security.MessageDigest
import java.util.zip.CRC32
import java.util.zip.CRC32C


/**
 * 在文件夹中根据文件名直接创建文件
 * @receiver String 文件夹的路径
 * @return File 创建的文件
 */
fun String?.asFile(): File? {
  return this?.let { File(it) }
}

fun File.forRead(
  block: (ByteArray, Int) -> Unit, bufferSize: Int = DEFAULT_BUFFER_SIZE,
) {
  inputStream().use {
    it.forRead(block, bufferSize)
  }
}

fun File.copy(targetDir: File, targetName: String = this.name, overwrite: Boolean = false) {
  targetDir.mkdirs()
  if (isDirectory) {
    val listFiles = listFiles() ?: return
    val dir = File(targetDir, targetName)
    for (file in listFiles) {
      file.copy(dir, file.name, overwrite)
    }
  } else {
    val file = File(targetDir, targetName)
    this.copyTo(file, overwrite)
  }
}

/**
 * 删除目录
 */
fun File.remove(): Int {
  if (isDirectory) {
    var count = 0
    listFiles()?.forEach {
      count += it.remove()
    }
    return this.delete().ifElse(count + 1, count)
  } else {
    return this.delete().ifElse(1, 0)
  }
}

fun File.crc32(): Result<Long> {
  return runCatching {
    val crc32 = CRC32()
    forRead({ bytes, i ->
      crc32.update(bytes, 0, i)
    })
    crc32.value
  }
}

fun File.crc32c(): Result<Long> {
  return runCatching {
    val crc32c = CRC32C()
    forRead({ bytes, i ->
      crc32c.update(bytes, 0, i)
    })
    crc32c.value
  }
}

fun File.md5(): Result<ByteArray> {
  return runCatching {
    val digest = MessageDigest.getInstance("MD5")
    forRead({ bytes, i ->
      digest.update(bytes, 0, i)
    })
    digest.digest()
  }
}

fun File.md5Hex(): Result<String> {
  return runCatching {
    md5().getOrThrow().toHex()
  }
}

fun File.charset(): Result<String> {
  return runCatching {
    inputStream().charset().getOrThrow()
  }
}

fun <T : Serializable> File.writeObject(value: T): Result<Unit> {
  return runCatching {
    outputStream().use {
      ObjectOutputStream(it).writeObject(value)
    }
  }
}

fun <T : Serializable> File.readObject(): Result<T> {
  return runCatching {
    inputStream().use {
      ObjectInputStream(it).readObject() as T
    }
  }
}

fun File.isImage(
  imageExtensions: Array<String> = arrayOf("jpg", "jpeg", "png", "gif", "bmp"),
): Boolean {
  if (!exists() || !isFile) return false
  val fileExtension = this.extension.lowercase()
  return imageExtensions.contains(fileExtension)
}

fun File.isVideo(
  videoExtensions: Array<String> = arrayOf(
    "mp4", "3gp", "avi", "wmv", "mpeg", "mpg", "mov", "flv", "swf", "mpg", "rm"
  ),
): Boolean {
  if (!exists() || !isFile) return false
  val fileExtension = this.extension.lowercase()
  return videoExtensions.contains(fileExtension)
}

fun String.replaceIllegalFileNameCharacters(): String {
  val illegalCharacters = "[/\\\\?%*:|\"<>]".toRegex()
  return this.replace(illegalCharacters, "_")
}