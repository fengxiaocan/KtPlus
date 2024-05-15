package java_kt

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


/**
 * Deep clone.
 *
 * @param data The data.
 * @param <T>  The value type.
 * @return The object of cloned
</T> */
fun <T> Serializable.deepClone(): Result<T> {
  return runCatching {
    ByteArrayOutputStream().use { os ->
      ObjectOutputStream(os).writeObject(this@deepClone)
      ByteArrayInputStream(os.toByteArray()).use { bs ->
        ObjectInputStream(bs).readObject() as T
      }
    }
  }
}
