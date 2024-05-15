package android_kt

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import java_kt.MimeType
import java.io.File


/**
 * 创建快捷方式
 * @param iconResId
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Activity.installShortcut(
  iconResId: Int = this.activityInfo.icon,
  shortcutName: CharSequence = this.activityInfo.name,
  shortcutId: String = this.localClassName
) {
  val icon = Icon.createWithResource(this, iconResId)
  val manager = getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
  val info = ShortcutInfo.Builder(this, shortcutId).setActivity(this.componentName).setIcon(icon)
    .setShortLabel(shortcutName).build()
  manager.createShortcutResultIntent(info)
}

/**
 * 判断是否存在Activity
 *
 * @param packageName 包名
 * @param className   activity全路径类名
 * @return `true`: 是<br></br>`false`: 否
 */
fun Context.isActivityExists(packageName: String, className: String): Boolean {
  val intent = Intent()
  intent.setClassName(packageName, className)
  return !(packageManager.resolveActivity(intent, 0) == null || intent.resolveActivity(
    packageManager
  ) == null || packageManager.queryIntentActivities(intent, 0).size == 0)
}

/**
 * 设置Activity全屏
 */
fun Activity.fullScreen() {
  requestWindowFeature(Window.FEATURE_NO_TITLE)
  window.setFlags(
    WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
  )
}

/**
 * 屏幕常亮
 */
fun Activity.keepScreen(tag: String = this.localClassName): PowerManager.WakeLock {
  window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
  return powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, tag)
}

/**
 * 打开一个界面
 */
fun Context.startActivity(clazz: Class<out Activity>, block: ((Intent) -> Unit)? = null) {
  val intent = Intent(this, clazz)
  block?.invoke(intent)
  startActivity(intent)
}

/**
 * 获取Activity根节点View
 * @return
 */
fun Activity.getRootView(): View {
  return (findViewById<ViewGroup>(R.id.content)!!).getChildAt(0)
}

/**
 * 获取Activity根节点Group
 */
fun Activity.getRootViewGroup(): ViewGroup {
  return window.decorView.findViewById(R.id.content)
}

/**
 * 分享文本
 *
 * @param content 分享文本
 */
fun Context.shareText(content: String) {
  val intent = Intent(Intent.ACTION_SEND)
  intent.setType("text/plain")
  intent.putExtra(Intent.EXTRA_TEXT, content)
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 分享图片
 *
 * @param content 分享文本
 * @param uri     图片uri
 */
fun Context.shareImage(content: String, uri: Uri) {
  val intent = Intent(Intent.ACTION_SEND)
  intent.putExtra(Intent.EXTRA_TEXT, content)
  intent.putExtra(Intent.EXTRA_STREAM, uri)
  intent.setType("image/*")
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 *分享图片
 *
 * @param content 分享文本
 * @param file     图片uri
 */
fun Context.shareImage(content: String, file: File) {
  shareImage(content, Uri.fromFile(file))
}

/**
 * 关机
 *
 * 需添加权限 `<uses-permission android:name="android.permission.SHUTDOWN"/>`
 *
 * @return intent
 */
fun Context.shutdown() {
  val intent = Intent(Intent.ACTION_SHUTDOWN)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 跳至拨号界面
 *
 * @param phoneNumber 电话号码
 */
fun Context.dial(phoneNumber: String) {
  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 拨打电话
 *
 * 需添加权限 `<uses-permission android:name="android.permission.CALL_PHONE"/>`
 *
 * @param phoneNumber 电话号码
 */
fun Context.call(phoneNumber: String) {
  val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 跳至发送短信界面
 *
 * @param phoneNumber 接收号码
 * @param content     短信内容
 */
fun Context.sendSms(phoneNumber: String, content: String) {
  val uri = Uri.parse("smsto:$phoneNumber")
  val intent = Intent(Intent.ACTION_SENDTO, uri)
  intent.putExtra("sms_body", content)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 拍照
 *
 * @param outUri 输出的uri
 * @return 拍照的意图
 */
fun Context.capture(outUri: Uri) {
  val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
  intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
  intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
  startActivity(intent)
}

/**
 * 打开压缩文件
 * @param uri
 * @return
 */
fun Context.openGzip(uri: Uri) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  intent.setDataAndType(uri, "application/x-gzip")
  startActivity(intent)
}

/**
 * 打开APK文件
 * @param uri
 * @return
 */
fun Context.openApkFile(uri: Uri) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  intent.setDataAndType(uri, "application/vnd.android.package-archive")
  startActivity(intent)
}

/**
 * 打开video文件
 *
 * @param uri
 * @return
 */
fun Context.openVideoFile(uri: Uri) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
  intent.setDataAndType(uri, "video/*")
  startActivity(intent)
}

/**
 * 打开AUDIO文件
 *
 * @param uri
 * @return
 */
fun Context.openAudioFile(uri: Uri) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
  intent.setDataAndType(uri, "audio/*")
  startActivity(intent)
}

/**
 * 打开图片文件
 * @return
 */
fun Context.openImageFile(uri: Uri) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addCategory(Intent.CATEGORY_DEFAULT)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  intent.setDataAndType(uri, "image/*")
  startActivity(intent)
}

/**
 * 打开文件
 * @return
 */
fun Context.openMimeFile(uri: Uri, type: MimeType) {
  val intent = Intent(Intent.ACTION_VIEW)
  intent.addCategory(Intent.CATEGORY_DEFAULT)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  intent.setDataAndType(uri, type.mime)
  startActivity(intent)
}
