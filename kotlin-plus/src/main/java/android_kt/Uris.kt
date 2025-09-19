package android_kt

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.VolumeInfo
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.io.File
import java.lang.Long
import kotlin.Array
import kotlin.Boolean
import kotlin.Result
import kotlin.String
import kotlin.arrayOf
import kotlin.runCatching

const val STORAGE_PATH = "/storage"
const val STORAGE_PATH_REGEX = "/storage/"
const val STORAGE_EMULATED_PATH = "/storage/emulated"
const val STORAGE_EMULATED_PATH_REGEX = "/storage/emulated/[^/]+/?"
const val STORAGE_EXTENSIONS_PATH_REGEX = "/storage/([^/]+)/?"
const val STORAGE_AUTHORITY = "com.android.externalstorage.documents";
const val STORAGE_PRIMARY = "primary"
const val STORAGE_PRIMARY_ID = "primary:"

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun File.buildTreeDocument(): Uri {
  return DocumentsContract.buildTreeDocumentUri(STORAGE_AUTHORITY, getDocumentId())
}

/**
 * 返回文件路径,如果取不到就返回uri.toString()
 */
fun Uri.toPathOrUriStr(appContext: Context): String {
  val ayaExternalPath = "/external_path"
  var result = toString()
  val projection = arrayOf(MediaStore.Images.Media.DATA)
  appContext.contentResolver.query(this, projection, null, null, null)?.use { cursor ->
    if (cursor.moveToFirst()) {
      //有_data数据的,直接取,而且能取到值
      if (cursor.columnNames.contains(MediaStore.Images.Media.DATA)) {
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        //有值返回path
        if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
          result = cursor.getString(columnIndex)
          cursor.close()
          return result
        }
      }
      cursor.close()
    }
    //没有_data数据的uri,通过uri.path取,我们系统部分path前缀用/external_path代替的sdk路径
    if (path.startsWith(ayaExternalPath)) {
      result = path.replace(ayaExternalPath, "${Environment.getExternalStorageDirectory()}")
    }
  }
  return result
}

@RequiresApi(Build.VERSION_CODES.N)
fun Uri.toRealPath(appContext: Context): String? {
  val uri = this
  when {
    // DocumentProvider
    DocumentsContract.isTreeUri(uri) -> {
      val docId = DocumentsContract.getTreeDocumentId(uri)
      return getDocumentRealPath(docId)
    }

    DocumentsContract.isDocumentUri(appContext, uri) -> {
      when {
        // ExternalStorageProvider
        isExternalStorageDocument(uri) -> {
          val docId = DocumentsContract.getDocumentId(uri)
          return getDocumentRealPath(docId)
        }

        isDownloadsDocument(uri) -> {
          val fileName = appContext.queryUriFilePath(uri)
          if (fileName != null) {
            val directory =
              Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            return File(directory, fileName).absolutePath
          }
          var id = DocumentsContract.getDocumentId(uri)
          if (id.startsWith("raw:")) {
            id = id.replaceFirst("raw:".toRegex(), "")
            val file = File(id)
            if (file.exists()) return id
          }
          val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"),
            Long.valueOf(id)
          )
          return getDataColumn(appContext, contentUri, null, null)
        }

        isMediaDocument(uri) -> {
          val docId = DocumentsContract.getDocumentId(uri)
          val split = docId.split(":").toTypedArray()
          val type = split[0]
          var contentUri: Uri? = when (type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> null
          }
          val selection = "_id=?"
          val selectionArgs = arrayOf(split[1])
          return getDataColumn(appContext, contentUri, selection, selectionArgs)
        }
      }
    }

    "content".equals(uri.scheme, ignoreCase = true) -> {
      // Return the remote address
      return if (isGooglePhotosUri(uri)) uri.lastPathSegment
      else getDataColumn(appContext, uri, null, null)
    }

    "file".equals(uri.scheme, ignoreCase = true) -> {
      return uri.path
    }
  }
  return null
}

private fun getDocumentRealPath(docId: String): String? {
  val split = docId.split(":").toTypedArray()
  val type = split[0]
  return if (STORAGE_PRIMARY.equals(type, ignoreCase = true)) {
    if (split.size > 1) {
      Environment.getExternalStorageDirectory().toString() + "/" + split[1]
    } else {
      Environment.getExternalStorageDirectory().toString() + "/"
    }
  } else {
    STORAGE_PATH_REGEX + docId.replace(":", "/")
  }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun documentRootUri(): Uri {
  return DocumentsContract.buildRootUri(STORAGE_AUTHORITY, STORAGE_PRIMARY)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun documentTreeUri(): Uri {
  return DocumentsContract.buildTreeDocumentUri(STORAGE_AUTHORITY, STORAGE_PRIMARY)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun File.buildDocumentUri(): Uri {
  //content://com.android.externalstorage.documents/document/primary%3APictures%2Fwallpaper_1.jpg
  //content://com.android.externalstorage.documents/document/67F6-FB2A%3ARoms%2FNDS%2Fmedia%2F%E9%98%BF%E6%8B%89%E8%95%BE%2FboxFront.jpg
  val myPath = this.absolutePath
  if (myPath.startsWith(STORAGE_PATH)) {
    val regex: Regex
    val replacement: String
    if (myPath.startsWith(STORAGE_EMULATED_PATH)) {
      regex = Regex(STORAGE_EMULATED_PATH_REGEX)
      replacement = STORAGE_PRIMARY_ID
    } else {
      regex = Regex(STORAGE_EXTENSIONS_PATH_REGEX)
      replacement = regex.find(myPath)!!.groupValues[1] + ":"
    }
    val childDocumentId = myPath.replaceFirst(regex, replacement)
    return DocumentsContract.buildDocumentUri(STORAGE_AUTHORITY, childDocumentId)
  }
  return Uri.parse(myPath)
}

fun File.isRootPath(): Boolean {
  val myPath = this.absolutePath
  if (myPath.startsWith(STORAGE_PATH)) {
    val regex = if (myPath.startsWith(STORAGE_EMULATED_PATH)) {
      Regex(STORAGE_EMULATED_PATH_REGEX)
    } else {
      Regex(STORAGE_EXTENSIONS_PATH_REGEX)
    }
    return regex.matches(myPath)
  }
  return true
}

fun File.getRootId(context: Context): String? {
  val filePath = this.absolutePath
  val userId = 0
  val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
  val volumes = mStorageManager.volumes
  for (volume in volumes) {
    if (!volume.isMountedReadable || volume.getMountUserId() != userId) continue
    if (!filePath.startsWith(volume.path)) continue
    val rootId = if (volume.getType() == VolumeInfo.TYPE_EMULATED) {
      DocumentsContract.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID
    } else if (volume.getType() == VolumeInfo.TYPE_PUBLIC
      || volume.getType() == VolumeInfo.TYPE_STUB
    ) {
      volume.getFsUuid()
    } else {
      // Unsupported volume; ignore
      continue
    }
    if (TextUtils.isEmpty(rootId)) {
      continue
    }
    return rootId
  }
  return null
}

/**
 * 构建这种类型的：content://com.android.externalstorage.documents/tree/67F6-FB2A%3Apocketsgame
 * 构建这种类型的：content://com.android.externalstorage.documents/tree/primary%3APocketGames%2Froms%2Fnds
 */
fun File.getDocumentId(): String {
  val myPath = this.absolutePath
  if (myPath.startsWith(STORAGE_PATH)) {
    val regex: Regex
    val replacement: String
    if (myPath.startsWith(STORAGE_EMULATED_PATH)) {
      regex = Regex(STORAGE_EMULATED_PATH_REGEX)
      replacement = STORAGE_PRIMARY_ID
    } else {
      regex = Regex(STORAGE_EXTENSIONS_PATH_REGEX)
      replacement = regex.find(myPath)!!.groupValues[1] + ":"
    }
    return myPath.replaceFirst(regex, replacement)
  }
  return STORAGE_PRIMARY_ID
}

/**
 * 将绝对路径（“/storage/emulated/0/PocketGames/roms/gamecube/Kao the Kangaroo - Round 2 (USA).iso” 或 “/storage/2F628A-F974/PocketGames/roms/gamecube/Kao the Kangaroo - Round 2 (USA).iso”）
 * 转化为 content uri（“content://com.android.externalstorage.documents/tree/primary%3APocketGames%2Froms%2Fgamecube/document/primary%3APocketGames%2Froms%2Fgamecube%2FKao%20the%20Kangaroo%20-%20Round%202%20(USA).iso”）
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun File.buildDocumentTreeUri(): Uri {
  val myPath = this.absolutePath
  if (myPath.startsWith(STORAGE_PATH)) {
    val regex: Regex
    val replacement: String
    if (myPath.startsWith(STORAGE_EMULATED_PATH)) {
      regex = Regex(STORAGE_EMULATED_PATH_REGEX)
      replacement = STORAGE_PRIMARY_ID
    } else {
      regex = Regex(STORAGE_EXTENSIONS_PATH_REGEX)
      replacement = regex.find(myPath)!!.groupValues[1] + ":"
    }
    val childDocumentId = myPath.replaceFirst(regex, replacement)
    val parentDocumentUri = if (regex.matches(myPath)) {
      DocumentsContract.buildTreeDocumentUri(STORAGE_AUTHORITY, childDocumentId)
    } else {
      val parentPath = parentFile.absolutePath
      val parentDocumentId = parentPath.replaceFirst(regex, replacement)
      DocumentsContract.buildTreeDocumentUri(STORAGE_AUTHORITY, parentDocumentId)
    }
    return DocumentsContract.buildDocumentUriUsingTree(parentDocumentUri, childDocumentId)
  }
  return Uri.parse(myPath)
}

/**
 * 将绝对路径（“/storage/emulated/0/PocketGames/roms/gamecube/Kao the Kangaroo - Round 2 (USA).iso” 或 “/storage/2F628A-F974/PocketGames/roms/gamecube/Kao the Kangaroo - Round 2 (USA).iso”）
 * 转化为 content uri（“content://com.android.externalstorage.documents/tree/primary%3APocketGames%2Froms%2Fgamecube/document/primary%3APocketGames%2Froms%2Fgamecube%2FKao%20the%20Kangaroo%20-%20Round%202%20(USA).iso”）
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun String.toContentUri(): Uri {
  return File(this).buildDocumentTreeUri()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
  return STORAGE_AUTHORITY == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
  return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
  return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
  return "com.google.android.apps.photos.content" == uri.authority
}

fun getDataColumn(
  context: Context, uri: Uri?, selection: String?,
  selectionArgs: Array<String>?,
): String? {
  var cursor: Cursor? = null
  val column = "_data"
  val projection = arrayOf(
    column
  )
  try {
    if (uri == null) return null
    cursor = context.contentResolver.query(
      uri, projection, selection, selectionArgs,
      null
    )
    if (cursor != null && cursor.moveToFirst()) {
      val index = cursor.getColumnIndexOrThrow(column)
      return cursor.getString(index)
    }
  } finally {
    cursor?.close()
  }
  return null
}

fun Context.queryUriFilePath(uri: Uri?): String? {
  var cursor: Cursor? = null
  val projection = arrayOf(
    MediaStore.MediaColumns.DISPLAY_NAME
  )
  try {
    if (uri == null) return null
    cursor = contentResolver.query(
      uri, projection, null, null,
      null
    )
    if (cursor != null && cursor.moveToFirst()) {
      val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
      return cursor.getString(index)
    }
  } finally {
    cursor?.close()
  }
  return null
}

fun Uri.copyToFile(context: Context, file: File): Result<Boolean> {
  return runCatching {
    context.contentResolver.openInputStream(this).use { input ->
      file.outputStream().use { output ->
        input.copyTo(output) > 0
      }
    }
  }
}
