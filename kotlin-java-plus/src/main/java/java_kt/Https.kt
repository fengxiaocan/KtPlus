package java_kt

import java.io.File
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


/**
 * 授权的密钥管理器，用来授权验证
 * @param password
 */
fun InputStream.getKeyManagers(password: String): Result<Array<KeyManager>> {
  return runCatching {
    val clientKeyStore = KeyStore.getInstance("BKS")
    clientKeyStore.load(this, password.toCharArray())
    val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    keyManagerFactory.init(clientKeyStore, password.toCharArray())
    keyManagerFactory.keyManagers
  }
}

/**
 * 授权的密钥管理器，用来授权验证
 */
fun File.getKeyManagers(password: String): Result<Array<KeyManager>> {
  return runCatching {
    inputStream().use { it.getKeyManagers(password).getOrThrow() }
  }
}

/**
 * 被授权的证书管理器，用来验证服务器端的证书
 *
 * @param certificates
 * @return
 */
fun getTrustManagers(vararg certificates: InputStream): Result<Array<TrustManager>> {
  return runCatching {
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null)
    certificates.forEachIndexed { index, certificate ->
      val certificateAlias = index.toString()
      val generateCertificate = certificateFactory.generateCertificate(certificate)
      keyStore.setCertificateEntry(certificateAlias, generateCertificate)
    }
    val trustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(keyStore)
    trustManagerFactory.trustManagers
  }
}

/**
 * 被授权的证书管理器，用来验证服务器端的证书
 *
 * @return
 */
fun getTrustManagers(vararg cerFiles: File): Result<Array<TrustManager>> {
  return runCatching {
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null)
    cerFiles.forEachIndexed { index, file ->
      file.inputStream().use { certificate ->
        val certificateAlias = index.toString()
        val generateCertificate = certificateFactory.generateCertificate(certificate)
        keyStore.setCertificateEntry(certificateAlias, generateCertificate)
      }
    }
    val trustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(keyStore)
    trustManagerFactory.trustManagers
  }
}

/**
 * 获取证书创建工具
 *
 * @param trustManagers
 * @return
 */
fun getSslSocketFactory(
  keyManagers: Array<KeyManager>? = null, trustManagers: Array<TrustManager>? = null
): Result<SSLSocketFactory> {
  return runCatching {
    val sslContext = SSLContext.getInstance("TLS")
    if (trustManagers == null) {
      sslContext.init(keyManagers, arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
      }), SecureRandom())
    } else {
      sslContext.init(keyManagers, trustManagers, null)
    }
    sslContext.socketFactory
  }
}