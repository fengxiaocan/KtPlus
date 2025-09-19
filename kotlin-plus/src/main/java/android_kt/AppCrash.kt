package android_kt

import android.app.Application
import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.os.SystemProperties
import android.util.DisplayMetrics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat

fun Application.tryCatchUncaughtException() {
  Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler(this))
}

class AppCrashHandler(
  private val context: Application,
  private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler(),
) : Thread.UncaughtExceptionHandler {
  private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // 管理协程的生命周期

  override fun uncaughtException(t: Thread, e: Throwable) {
    // 保存日志到SD卡
    coroutineScope.launch {
      writeCrash(e)
      //上报给bugly
//      CrashReport.postCatchedException(e,t)
      // 继续执行默认的异常处理程序
      defaultHandler?.uncaughtException(t, e)
    }
  }

  private fun writeCrash(e: Throwable) {
    runCatching {
      val packageName = context.packageName
      val logDir = File(Environment.getExternalStorageDirectory(), "AYANEO/log")
      logDir.mkdirs()
      logDir.listFiles()?.filter { it.name.startsWith(packageName) }?.let { list ->
        if (list.size > 1000) {
          val files = list.sortedByDescending { it.lastModified() }
          for (i in 1000 until files.size) {
            files[i].delete()
          }
        }
      }
      val log = File(logDir, "$packageName-${System.currentTimeMillis()}.log")
      log.writeLog(context, e)
    }
  }
}

fun File.writeLog(app: Application, throwable: Throwable, append: Boolean = true): Result<Unit> {
  return runCatching {
    PrintStream(FileOutputStream(this, append)).use { ps ->
      printApkInfos(ps, app)
      printDeviceInfos(ps)
      throwable.printStackTrace(ps)
      ps.println()
    }
  }.onFailure {
    it.printStackTrace()
  }
}

fun printApkInfos(ps: PrintStream, app: Application) {
  ps.println("应用信息：")
  ps.append("包名：").println(app.packageName)
  val packageInfo = app.getPackageInfo().getOrThrow()
  ps.append("VersionName：").println(packageInfo.versionName)
  ps.append("Version：").println(packageInfo.versionCode)
  val manager = app.packageManager
  val info = manager.getPackageInfo(app.packageName, 0)
  ps.append("应用安装时间：").println(formatTime(info.firstInstallTime))
  ps.append("应用最后更新时间：").println(formatTime(info.lastUpdateTime))
  ps.println()
}

fun printDeviceInfos(ps: PrintStream) {
  ps.println("设备信息：")
  ps.println(Build.MODEL)
  ps.append("设备ID：").println(Build.ID)
  ps.append("品牌：").println(Build.BRAND)
  ps.append("制造商：").println(Build.MANUFACTURER)
  ps.append("产品：").println(Build.PRODUCT)
  ps.append("设备：").println(Build.DEVICE)
  ps.append("User：").println(Build.USER)
  ps.append("Host：").println(Build.HOST)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    ps.append("CPU品牌：").println(Build.SOC_MODEL)
  }
  ps.append("CPU型号：").println(Build.HARDWARE)
  ps.append("CPU架构：").println(SystemProperties.get("ro.product.cpu.abi", Build.UNKNOWN))

  ps.println()
  ps.println("系统信息：")
  ps.append("Android ").append(Build.VERSION.RELEASE).append(" / API ")
    .println(Build.VERSION.SDK_INT)
  ps.append("基带版本：").println(SystemProperties.get("gsm.version.baseband", Build.UNKNOWN))
  ps.append("编译版本号：").println(Build.DISPLAY)
  ps.append("编译序列：").println(Build.FINGERPRINT)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    ps.append("安全补丁日期：").println(Build.VERSION.SECURITY_PATCH)
  }
  ps.append("版本构建日期：").println(formatTime(Build.TIME))
  ps.append("构建版本类型：").println(Build.TYPE)
  ps.append("构建版本标志：").println(Build.TAGS)
  ps.append("Incremental：").println(Build.VERSION.INCREMENTAL)
  ps.println()
  ps.println("屏幕信息：")
  val metrics = Resources.getSystem().displayMetrics
  metrics.density
  ps.append("分辨率：")
  ps.print(metrics.widthPixels)
  ps.print(" x ")
  ps.print(metrics.heightPixels)
  ps.print(" px / ")
  ps.print(metrics.widthPixels / metrics.density)
  ps.print(" x ")
  ps.print(metrics.heightPixels / metrics.density)
  ps.println(" dp")
  ps.print("字体密度：")
  ps.println(metrics.scaledDensity)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    ps.print("屏幕密度：")
    ps.print(DisplayMetrics.DENSITY_DEVICE_STABLE)
    ps.print("dp / ")
    ps.print(metrics.density)
    ps.println("x")
  }
  ps.println()
}

fun formatTime(time: Long): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time) ?: ""