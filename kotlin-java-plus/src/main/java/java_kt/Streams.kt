package java_kt

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.Reader
import java.io.StringWriter


fun <T : Closeable?, R> T.useCatching(block: (T) -> R): Result<R> {
    return try {
        this.use {
            Result.success(block(this))
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

fun InputStream.copyToFile(
    file: File,
    append: Boolean = false,
    bufferSize: Int = DEFAULT_BUFFER_SIZE
): Result<Long> {
    return useCatching {
        FileOutputStream(file, append).use {
            copyTo(it, bufferSize)
        }
    }
}

fun InputStream.toOutputStream(bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteArrayOutputStream {
    val output = ByteArrayOutputStream()
    copyTo(output, bufferSize)
    return output
}

fun Reader.toWriter(bufferSize: Int = DEFAULT_BUFFER_SIZE): StringWriter {
    val buffer = StringWriter()
    copyTo(buffer, bufferSize)
    return buffer
}

fun InputStream.forRead(
    block: (ByteArray, Int) -> Unit,
    bufferSize: Int = DEFAULT_BUFFER_SIZE
): Long {
    var allSize: Long = 0
    val data = ByteArray(bufferSize)
    var size = this.read(data)
    allSize += size
    while (size > 0) {
        block(data, size)
        size = this.read(data)
        allSize += size
    }
    return allSize
}

fun Reader.forRead(block: (CharArray, Int) -> Unit, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var allSize: Long = 0
    val data = CharArray(bufferSize)
    var size = this.read(data)
    allSize += size
    while (size > 0) {
        block(data, size)
        size = this.read(data)
        allSize += size
    }
    return allSize
}

/**
 * 获取流的字符集
 */
fun InputStream.charset(): Result<String> {
    return useCatching {
        //找到文档的前三个字节并自动判断文档类型。
        val bytes = ByteArray(3)
        it.read(bytes)
        if (bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) { // utf-8
            "utf-8"
        } else if (bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()) {
            "unicode"
        } else if (bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()) {
            "utf-16be"
        } else if (bytes[0] == 0xFF.toByte() && bytes[1] == 0xFF.toByte()) {
            "utf-16le"
        } else {
            "GBK"
        }
    }
}
