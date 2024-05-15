package java_kt

import java.io.File
import java.io.IOException
import java.util.Enumeration
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 通过GZip压缩文件
 */
fun File.compressByGZip(zipFile: File? = null): Result<Long> {
    if (this.isDirectory) return Result.failure(
        NoSuchFileException(this, null, "File type error!")
    )
    return runCatching {
        val file = zipFile.ifNull { File(parentFile, "$name.gzip") }
        file.outputStream().use { out ->
            val output = GZIPOutputStream(out)
            try {
                this.inputStream().use { it.copyTo(output) }
            } finally {
                output.finish()
            }
        }
    }
}

/**
 * 解压缩GZip文件
 */
fun File.decompressByGZip(descFile: File): Result<Long> {
    if (this.isDirectory) return Result.failure(
        NoSuchFileException(this, null, "File type error!")
    )
    return runCatching {
        descFile.outputStream().use { out ->
            this.inputStream().use {
                GZIPInputStream(it).use { gis ->
                    gis.copyTo(out)
                }
            }
        }
    }
}

/**
 * 批量压缩文件
 * @receiver Collection<File> 待压缩文件集合
 * @param zipFile File 需要压缩成的文件
 * @param rootPathName String 压缩的根目录
 * @return Result<Long>
 */
fun Iterable<File>.compressByZip(
    zipFile: File, rootPathName: String? = null, compressLevel: ZipLevel? = null
): Result<Long> {
    return runCatching {
        zipFile.deleteOnExit()
        var bytesCopied: Long = 0
        zipFile.outputStream().use { fos ->
            val zos = ZipOutputStream(fos)
            compressLevel?.let {
                zos.setLevel(it.level)
            }
            try {
                forEach { file ->
                    bytesCopied += file.compressByZip(zos, rootPathName).getOrThrow()
                }
            } finally {
                zos.finish()
            }
        }
        bytesCopied
    }
}

/**
 * 批量压缩文件
 * @receiver Collection<File> 待压缩文件集合
 * @param zipFile File
 * @return Result<Long>
 */
fun Array<File>.compressByZip(
    zipFile: File, rootPathName: String? = null, compressLevel: ZipLevel? = null
): Result<Long> {
    return runCatching {
        var bytesCopied: Long = 0
        zipFile.outputStream().use { fos ->
            val zos = ZipOutputStream(fos)
            compressLevel?.let {
                zos.setLevel(it.level)
            }
            try {
                forEach { file ->
                    bytesCopied += file.compressByZip(zos, rootPathName).getOrThrow()
                }
            } finally {
                zos.finish()
            }
        }
        bytesCopied
    }
}

/**
 * 压缩文件
 * @receiver File 被压缩文件
 * @param zipFile File 压缩后的文件
 * @return Result<Boolean>
 */
fun File.compressByZip(
    zipFile: File? = null, rootPathName: String? = null, compressLevel: ZipLevel? = null
): Result<Long> {
    return runCatching {
        val file = zipFile.ifNull { File(parentFile, "$name.zip") }
        file.outputStream().use { stream ->
            val zos = ZipOutputStream(stream)
            compressLevel?.let {
                zos.setLevel(it.level)
            }
            try {
                this.compressByZip(zos, rootPathName).getOrThrow()
            } finally {
                zos.finish()
            }
        }
    }
}

enum class ZipLevel(val level: Int) {
    /**
     * 最快的压缩速度，但压缩比例最低。
     */
    BEST_SPEED(Deflater.BEST_SPEED),

    /**
     * 最高的压缩比例，但压缩速度最慢。
     */
    BEST_COMPRESSION(Deflater.BEST_COMPRESSION),

    /**
     * 默认的压缩级别，平衡了压缩比例和压缩速度。
     */
    DEFAULT_COMPRESSION(Deflater.DEFAULT_COMPRESSION),

    /**
     * 不进行压缩，直接存储原始数据。
     */
    NO_COMPRESSION(Deflater.NO_COMPRESSION);
}

/**
 *
 * @receiver File 被压缩文件
 * @param output ZipOutputStream 压缩文件流
 * @param rootPathName String?
 */
fun File.compressByZip(
    output: ZipOutputStream,
    rootPathName: String? = null,
): Result<Long> {
    return runCatching {
        var bytesCopied: Long = 0
        val path = if (rootPathName.isNullOrBlank()) {
            this.name
        } else {
            if (rootPathName.endsWith("/")) {
                "$rootPathName${this.name}"
            } else {
                "$rootPathName/${this.name}"
            }
        }
        if (isDirectory) {
            val entry = ZipEntry("$path/") // 表示目录
            output.putNextEntry(entry)
            output.closeEntry()

            val fileList = this.listFiles()
            if (!fileList.isNullOrEmpty()) {
                for (file in fileList) {
                    bytesCopied += file.compressByZip(output, path).getOrThrow()
                }
            }
        } else {
            val entry = ZipEntry(path)
            output.putNextEntry(entry)
            bytesCopied += this.inputStream().use { it.copyTo(output) }
            output.closeEntry()
        }
        bytesCopied
    }
}

/**
 * 批量解压文件
 * @receiver Collection<File> 压缩文件集合
 * @param destDir File 目标目录
 * @return  Result<Long>
 */
fun Iterable<File>.decompressByZip(
    destDir: File,
    filterEntry: ((ZipEntry) -> Boolean)? = null
): Result<Long> {
    return runCatching {
        var bytesCopied: Long = 0
        forEach {
            bytesCopied += it.decompressByZip(destDir, filterEntry).getOrThrow()
        }
        bytesCopied
    }
}

/**
 * 批量解压文件
 * @receiver Collection<File> 压缩文件集合
 * @param destDir File 目标目录
 * @return  Result<Long>
 */
fun Array<File>.decompressByZip(
    destDir: File,
    filterEntry: ((ZipEntry) -> Boolean)? = null
): Result<Long> {
    return runCatching {
        var bytesCopied: Long = 0
        forEach {
            bytesCopied += it.decompressByZip(destDir, filterEntry).getOrThrow()
        }
        bytesCopied
    }
}

/**
 * 解压文件
 * @receiver File 待解压文件
 * @param destDir File 目标目录
 * @param filterEntry 过滤器
 * @return Result<Long>
 */
fun File.decompressByZip(
    destDir: File? = null, filterEntry: ((ZipEntry) -> Boolean)? = null
): Result<Long> {
    return runCatching {
        val zipDir = destDir.ifNull { File(this.parentFile, this.nameWithoutExtension) }
        ZipFile(this).decompressByZip(zipDir, filterEntry).getOrThrow()
    }
}

/**
 * zip文件流解压到文件夹
 * @receiver ZipInputStream
 * @param destDir File
 * @param filterEntry 过滤压缩文件
 * @return Result<Long>
 */
fun ZipInputStream.decompressByZip(
    destDir: File, filterEntry: ((ZipEntry) -> Boolean)? = null
): Result<Long> {
    return useCatching { input ->
        destDir.mkdirs()
        var bytesCopied: Long = 0
        forEachEntry { zipEntry ->
            if (filterEntry == null || filterEntry(zipEntry)) {
                val file = File(destDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.outputStream().use {
                        bytesCopied += input.copyTo(it)
                    }
                }
            }
        }
        bytesCopied
    }
}

/**
 * zip文件流解压到文件夹
 * @receiver ZipInputStream
 * @param destDir File
 * @param filterEntry 过滤压缩文件
 * @return Result<Long>
 */
fun ZipFile.decompressByZip(
    destDir: File,
    filterEntry: ((ZipEntry) -> Boolean)? = null
): Result<Long> {
    return useCatching {
        destDir.mkdirs()
        var bytesCopied: Long = 0
        val entries = entries()
        while (entries.hasMoreElements()) {
            val zipEntry = entries.nextElement()
            if (filterEntry == null || filterEntry(zipEntry)) {
                val file = File(destDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    file.mkdirs()
                } else {
                    bytesCopied += getInputStream(zipEntry).copyToFile(file).getOrDefault(0)
                }
            }
        }
        bytesCopied
    }
}

/**
 * 获取压缩文件中的文件路径链表
 * @receiver File zip文件
 * @return List<String>
 * @throws IOException
 */
fun File.zipFilePaths(): Result<List<String>> {
    return runCatching {
        ZipFile(this).zipFilePaths().getOrThrow()
    }
}

/**
 * 获取压缩文件中的文件路径链表
 * @return List<String>
 * @throws IOException
 */
fun ZipFile.zipFilePaths(): Result<List<String>> {
    return useCatching {
        ArrayList<String>().also {
            val entries = entries()
            while (entries.hasMoreElements()) {
                it.add(entries.nextElement().name)
            }
        }
    }
}

/**
 * 获取输入流中的压缩信息
 * @receiver InputStream
 * @return Result<List<ZipEntry>>
 */
fun ZipInputStream.zipFilePaths(): Result<List<String>> {
    return useCatching {
        ArrayList<String>().also { list ->
            forEachEntry {
                list.add(it.name)
            }
        }
    }
}

/**
 * 获取压缩文件中的文件对象
 * @receiver File
 * @return Enumeration<out ZipEntry>
 * @throws IOException
 */
fun File.zipEntries(): Result<Enumeration<out ZipEntry>> {
    return runCatching {
        ZipFile(this).use {
            it.entries()
        }
    }
}

/**
 * 获取输入流中的压缩信息
 * @receiver InputStream
 * @return Result<List<ZipEntry>>
 */
fun ZipInputStream.zipEntries(): Result<List<ZipEntry>> {
    return useCatching {
        ArrayList<ZipEntry>().also { list ->
            forEachEntry {
                list.add(it)
            }
        }
    }
}

private fun ZipInputStream.forEachEntry(block: (ZipEntry) -> Unit) {
    var nextEntry = this.nextEntry
    while (nextEntry != null) {
        block(nextEntry)
        closeEntry()
        nextEntry = this.nextEntry
    }
}