package android_kt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


class ActivityLaunch(
  private val owner: LifecycleOwner,
  private val activityResultCaller: ActivityResultCaller = owner as ActivityResultCaller,
  private val activityResultRegistry: ActivityResultRegistry? = null,
) {
  fun browseUriFolder(
    title: String = "File Manager", onResultBlock: (Uri?) -> Unit = {},
  ): BrowseFolder<Uri> {
    return SelectFolderBySystemImpl(this, onResultBlock, title)
  }

  fun browsePathFolder(
    title: String = "File Manager", onResultBlock: (Uri?) -> Unit = {},
  ): BrowseFolder<String> {
    return SelectFolderBySystemImpl(this, onResultBlock, title)
  }

  /**
   * 选择文档
   */
  fun selectDocument(onResultBlock: (Uri?) -> Unit): DocumentSelector {
    return DocumentSelectorImpl(this, onResultBlock)
  }

  /**
   * 选择多个文档
   */
  fun selectMultipleDocument(
    onResultBlock: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ): MultipleDocumentSelector {
    return MultipleDocumentSelectorImpl(this, onResultBlock)
  }

  /**
   * 选择文档目录
   */
  fun selectDocumentTree(onResultBlock: (Uri?) -> Unit): DocumentTreeSelector {
    return DocumentTreeSelectorImpl(this, onResultBlock)
  }

  fun startActivityForResult(onResultBlock: (ActivityResult) -> Unit): IStartActivityForResult {
    return StartActivityForResultImpl(this, onResultBlock)
  }

  fun startIntentSenderForResult(
    onResultBlock: (ActivityResult) -> Unit,
  ): IStartIntentSenderForResult {
    return StartIntentSenderForResultImpl(this, onResultBlock)
  }

  fun requestMultiplePermissions(
    onResultBlock: (Map<String, @JvmSuppressWildcards Boolean>) -> Unit,
  ): IRequestMultiplePermissions {
    return RequestMultiplePermissionsImpl(this, onResultBlock)
  }

  fun requestPermission(onResultBlock: (Boolean) -> Unit): IRequestPermission {
    return RequestPermissionImpl(this, onResultBlock)
  }

  fun takePicturePreview(onResultBlock: (Bitmap?) -> Unit): ITakePicturePreview {
    return TakePicturePreviewImpl(this, onResultBlock)
  }

  fun takePicture(onResultBlock: (Boolean) -> Unit): ITakePicture {
    return TakePictureImpl(this, onResultBlock)
  }

  @Deprecated("Use CaptureVideo")
  fun takeVideo(onResultBlock: (Bitmap?) -> Unit): ITakeVideo {
    return TakeVideoImpl(this, onResultBlock)
  }

  fun captureVideo(onResultBlock: (Boolean) -> Unit): ICaptureVideo {
    return CaptureVideoImpl(this, onResultBlock)
  }

  fun pickContact(onResultBlock: (Uri?) -> Unit): IPickContact {
    return PickContactImpl(this, onResultBlock)
  }

  fun getContent(onResultBlock: (Uri?) -> Unit): IGetContent {
    return GetContentImpl(this, onResultBlock)
  }

  fun getMultipleContents(
    onResultBlock: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ): IGetMultipleContents {
    return GetMultipleContentsImpl(this, onResultBlock)
  }

  fun createDocument(mimeType: String, onResultBlock: (Uri?) -> Unit): ICreateDocument {
    return CreateDocumentImpl(this, mimeType, onResultBlock)
  }

  fun pickVisualMedia(onResultBlock: (Uri?) -> Unit): IPickVisualMedia {
    return PickVisualMediaImpl(this, onResultBlock)
  }

  fun pickMultipleVisualMedia(
    maxItems: Int = -1, onResultBlock: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ): IPickMultipleVisualMedia {
    return PickMultipleVisualMediaImpl(this, maxItems, onResultBlock)
  }

  interface BrowseFolder<I> : ILaunch<I>
  interface DocumentSelector : IDocumentSelector
  interface MultipleDocumentSelector : IDocumentSelector
  interface DocumentTreeSelector : ILaunch<Uri?>
  interface IStartActivityForResult : ILaunch<Intent>
  interface IStartIntentSenderForResult : ILaunch<IntentSenderRequest>
  interface IRequestMultiplePermissions : ILaunch<Array<String>>
  interface IRequestPermission : ILaunch<String>
  interface ITakePicturePreview : ILaunch<Void?>
  interface ITakePicture : ILaunch<Uri>
  interface ITakeVideo : ILaunch<Uri>
  interface ICaptureVideo : ILaunch<Uri>
  interface IPickContact : ILaunch<Void?>
  interface IGetContent : IContentSelector
  interface IGetMultipleContents : IContentSelector
  interface ICreateDocument : ILaunch<String>
  interface IPickVisualMedia : ILaunch<PickVisualMediaRequest>
  interface IPickMultipleVisualMedia : ILaunch<PickVisualMediaRequest>


  private class PickMultipleVisualMediaImpl(
    activityLaunch: ActivityLaunch,
    val maxItems: Int = -1,
    onResult: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ) :
    BaseLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>(activityLaunch, onResult),
    IPickMultipleVisualMedia {
    override fun registerContract() = if (maxItems < 1) {
      ActivityResultContracts.PickMultipleVisualMedia()
    } else {
      ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    }
  }

  private class PickVisualMediaImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Uri?) -> Unit,
  ) : BaseLauncher<PickVisualMediaRequest, Uri?>(activityLaunch, onResult), IPickVisualMedia {
    override fun registerContract() = ActivityResultContracts.PickVisualMedia()
  }

  private class CreateDocumentImpl(
    activityLaunch: ActivityLaunch,
    val mimeType: String,
    onResult: (Uri?) -> Unit,
  ) : BaseLauncher<String, Uri?>(activityLaunch, onResult), ICreateDocument {
    override fun registerContract() = ActivityResultContracts.CreateDocument(mimeType)
  }

  private class GetMultipleContentsImpl(
    activityLaunch: ActivityLaunch,
    onResult: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ) : BaseLauncher<String, List<@JvmSuppressWildcards Uri>>(activityLaunch, onResult),
    IGetMultipleContents {
    override fun registerContract() = ActivityResultContracts.GetMultipleContents()
  }

  private class GetContentImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Uri?) -> Unit,
  ) : BaseLauncher<String, Uri?>(activityLaunch, onResult), IGetContent {
    override fun registerContract() = ActivityResultContracts.GetContent()
  }

  private class PickContactImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Uri?) -> Unit,
  ) : BaseLauncher<Void?, Uri?>(activityLaunch, onResult), IPickContact {
    override fun registerContract() = ActivityResultContracts.PickContact()
  }

  private class CaptureVideoImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Boolean) -> Unit,
  ) : BaseLauncher<Uri, Boolean>(activityLaunch, onResult), ICaptureVideo {
    override fun registerContract() = ActivityResultContracts.CaptureVideo()
  }

  private class TakeVideoImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Bitmap?) -> Unit,
  ) : BaseLauncher<Uri, Bitmap?>(activityLaunch, onResult), ITakeVideo {
    override fun registerContract() = ActivityResultContracts.TakeVideo()
  }

  private class TakePictureImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Boolean) -> Unit,
  ) : BaseLauncher<Uri, Boolean>(activityLaunch, onResult), ITakePicture {
    override fun registerContract() = ActivityResultContracts.TakePicture()
  }

  private class TakePicturePreviewImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Bitmap?) -> Unit,
  ) : BaseLauncher<Void?, Bitmap?>(activityLaunch, onResult), ITakePicturePreview {
    override fun registerContract() = ActivityResultContracts.TakePicturePreview()
  }

  private class RequestPermissionImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Boolean) -> Unit,
  ) : BaseLauncher<String, Boolean>(activityLaunch, onResult), IRequestPermission {
    override fun registerContract() = ActivityResultContracts.RequestPermission()
  }

  private class RequestMultiplePermissionsImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Map<String, @JvmSuppressWildcards Boolean>) -> Unit,
  ) : BaseLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>(
    activityLaunch, onResult
  ), IRequestMultiplePermissions {
    override fun registerContract() = ActivityResultContracts.RequestMultiplePermissions()
  }

  private class StartIntentSenderForResultImpl(
    activityLaunch: ActivityLaunch,
    onResult: (ActivityResult) -> Unit,
  ) : BaseLauncher<IntentSenderRequest, ActivityResult>(activityLaunch, onResult),
    IStartIntentSenderForResult {
    override fun registerContract() = ActivityResultContracts.StartIntentSenderForResult()
  }

  private class StartActivityForResultImpl(
    activityLaunch: ActivityLaunch,
    onResult: (ActivityResult) -> Unit,
  ) : BaseLauncher<Intent, ActivityResult>(activityLaunch, onResult), IStartActivityForResult {
    override fun registerContract() = ActivityResultContracts.StartActivityForResult()
  }

  private class DocumentTreeSelectorImpl(
    activityLaunch: ActivityLaunch,
    onResult: (Uri?) -> Unit,
  ) : BaseLauncher<Uri?, Uri?>(activityLaunch, onResult), DocumentTreeSelector {
    override fun registerContract() = ActivityResultContracts.OpenDocumentTree()
  }

  private class MultipleDocumentSelectorImpl(
    activityLaunch: ActivityLaunch,
    onResult: (List<@JvmSuppressWildcards Uri>) -> Unit,
  ) : BaseLauncher<Array<String>, List<@JvmSuppressWildcards Uri>>(activityLaunch, onResult),
    MultipleDocumentSelector {
    override fun registerContract() = ActivityResultContracts.OpenMultipleDocuments()
  }

  private class DocumentSelectorImpl(activityLaunch: ActivityLaunch, onResult: (Uri?) -> Unit) :
    BaseLauncher<Array<String>, Uri?>(activityLaunch, onResult), DocumentSelector {
    override fun registerContract() = ActivityResultContracts.OpenDocument()
  }

  private class SelectFolderBySystemImpl<I>(
    activityLaunch: ActivityLaunch, onResult: (Uri?) -> Unit, val title: String = "File Manager",
  ) : BaseLauncher<I, Uri?>(activityLaunch, onResult), BrowseFolder<I> {
    override fun registerContract() = OpenFolderBySystem<I>(title)
  }

  private abstract class BaseLauncher<Input, Result>(
    activityLaunch: ActivityLaunch,
    val onResult: (Result) -> Unit,
  ) : LifecycleEventObserver, ILaunch<Input> {
    private val owner = activityLaunch.owner
    private val activityResultCaller = activityLaunch.activityResultCaller
    private val activityResultRegistry = activityLaunch.activityResultRegistry
    protected lateinit var launcher: ActivityResultLauncher<Input>

    init {
      owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
      if (event == Lifecycle.Event.ON_CREATE) {
        initOnCreate()
      }
    }

    private fun initOnCreate() {
      if (!this::launcher.isInitialized) {
        create()
      }
    }

    private fun create() {
      if (activityResultRegistry == null) {
        launcher = activityResultCaller.registerForActivityResult(registerContract(), onResult)
      } else {
        launcher = activityResultCaller.registerForActivityResult(
          registerContract(), activityResultRegistry!!, onResult
        )
      }
    }

    override fun launch(input: Input, options: ActivityOptionsCompat?) {
      initOnCreate()
      launcher.launch(input, options)
    }

    override fun unregister() {
      launcher.unregister()
    }

    abstract fun registerContract(): ActivityResultContract<Input, Result>
  }

  interface IDocumentSelector : ILaunch<Array<String>> {
    fun selectAll(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("*/*"), options)
    }

    fun selectImage(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("image/*"), options)
    }

    fun selectVideo(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("video/*"), options)
    }

    fun selectText(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("text/*"), options)
    }

    fun selectZip(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("application/zip"), options)
    }

    fun selectCompressed(options: ActivityOptionsCompat? = null) {
      launch(
        arrayOf(
          "application/zip",
          "application/gz",
          "application/gtar",
          "application/tgz",
          "application/tar"
        ), options
      )
    }

    fun selectAudio(options: ActivityOptionsCompat? = null) {
      launch(arrayOf("audio/*"), options)
    }
  }


  interface IContentSelector : ILaunch<String> {
    fun selectImage(options: ActivityOptionsCompat? = null) {
      launch("image/*", options)
    }

    fun selectVideo(options: ActivityOptionsCompat? = null) {
      launch("video/*", options)
    }

    fun selectAudio(options: ActivityOptionsCompat? = null) {
      launch("audio/*", options)
    }

    fun selectText(options: ActivityOptionsCompat? = null) {
      launch("text/*", options)
    }
  }

  interface ILaunch<I> {
    fun launch(input: I, options: ActivityOptionsCompat? = null)
    fun unregister()
  }

  open class OpenFolderBySystem<I>(val title: String = "File Manager") :
    ActivityResultContract<I, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: I): Intent {
      return Intent(Intent.ACTION_GET_CONTENT).apply {
        setType("*/*")
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra("android.content.extra.SHOW_ADVANCED", true)
        putExtra("android.content.extra.FANCY", true)
        putExtra("android.content.extra.SHOW_FILESIZE", true)
        putExtra(Intent.EXTRA_TITLE, title)
        if (input is String) {
          putExtra("android.content.extra.FOLDER_NAME", input)
        } else if (input is Uri) {
          putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
        }
      }
    }

    final override fun getSynchronousResult(
      context: Context, input: I,
    ): SynchronousResult<Uri?>? = null

    final override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
      return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
  }
}

fun Uri?.takePersistableUriPermission(context: Context) {
  this?.let {
    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    context.contentResolver.takePersistableUriPermission(it, flag)
  }
}