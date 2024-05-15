package java_kt

import java.security.MessageDigest
import java.security.Security
import java.util.zip.CRC32
import java.util.zip.CRC32C

fun digestAlgorithms(): MutableSet<String>? {
    return Security.getAlgorithms("MessageDigest")
}

fun ByteArray.digest(algorithm: String): ByteArray {
    val md5 = MessageDigest.getInstance(algorithm)
    md5.update(this)
    return md5.digest()
}

fun ByteArray.digestHex(algorithm: String): String {
    return digest(algorithm).toHex()
}

fun ByteArray.md5(): ByteArray {
    return digest("MD5")
}

fun ByteArray.md5Hex(): String {
    return md5().toHex()
}

fun ByteArray.sha1(): ByteArray {
    return digest("SHA-1")
}

fun ByteArray.sha1Hex(): String {
    return sha1().toHex()
}

fun ByteArray.sha384(): ByteArray {
    return digest("SHA-384")
}

fun ByteArray.sha384Hex(): String {
    return sha384().toHex()
}

fun ByteArray.sha224(): ByteArray {
    return digest("SHA-224")
}

fun ByteArray.sha224Hex(): String {
    return sha224().toHex()
}


fun ByteArray.sha256(): ByteArray {
    return digest("SHA-256")
}

fun ByteArray.sha256Hex(): String {
    return sha256().toHex()
}

fun ByteArray.sha512(): ByteArray {
    return digest("SHA-512")
}

fun ByteArray.sha512Hex(): String {
    return sha512().toHex()
}

fun ByteArray.crc32(): Long {
    return CRC32().also { it.update(this) }.value
}

fun ByteArray.crc32c(): Long {
    return CRC32C().also { it.update(this) }.value
}