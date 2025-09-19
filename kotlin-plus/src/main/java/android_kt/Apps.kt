package android_kt

import android.Manifest
import android.annotation.RequiresPermission
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import java_kt.toHex

fun Context.getScreenSize(): IntArray {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    val metrics = windowManager().currentWindowMetrics
    return intArrayOf(metrics.bounds.width(), metrics.bounds.height())
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    val metrics = DisplayMetrics()
    windowManager().defaultDisplay.getRealMetrics(metrics)
    return intArrayOf(metrics.widthPixels, metrics.heightPixels)
  } else {
    val defaultDisplay = windowManager().defaultDisplay
    return intArrayOf(defaultDisplay.width, defaultDisplay.height)
  }
}

/**
 * 获取包的信息
 * @param packageName
 * @return
 */
fun Context.getPackageInfo(
  packageName: String = this.packageName,
  flags: Int = 0,
): Result<PackageInfo> {
  return runCatching {
    packageManager.getPackageInfo(packageName, flags)
  }
}

/**
 * 获取ApplicationInfo的信息
 * @param packageName
 * @return
 */
fun Context.getApplicationInfo(
  packageName: String = this.packageName,
  flags: Int = 0,
): Result<ApplicationInfo> {
  return runCatching {
    packageManager.getApplicationInfo(packageName, flags)
  }
}

/**
 * 获取已安装Apk文件的源Apk文件
 * @param packageName
 * @return
 */
fun Context.getSourceApkPath(packageName: String = this.packageName): Result<String> {
  return runCatching {
    getApplicationInfo(packageName).getOrThrow().sourceDir
  }
}

/**
 * 获取Activity信息
 * @param packageName
 * @return
 */
fun Context.getActivityInfo(
  activityName: String,
  packageName: String = this.packageName,
  flags: Int = 0,
): Result<ActivityInfo> {
  return runCatching {
    packageManager.getActivityInfo(ComponentName(packageName, activityName), flags)
  }
}

/**
 * 获取Activity信息
 * @param packageName
 * @return
 */
fun Context.getServiceInfo(
  serviceName: String,
  packageName: String = this.packageName,
  flags: Int = 0,
): Result<ServiceInfo> {
  return runCatching {
    packageManager.getServiceInfo(ComponentName(packageName, serviceName), flags)
  }
}

/**
 * 获取Activity信息
 * @param packageName
 * @return
 */
fun Context.getReceiverInfo(
  receiverName: String,
  packageName: String = this.packageName,
  flags: Int = 0,
): Result<ActivityInfo> {
  return runCatching {
    packageManager.getReceiverInfo(ComponentName(packageName, receiverName), flags)
  }
}

/**
 * APK版本比较
 *
 * @param info 下载的apk信息
 * @return 下载的apk版本号大于当前版本号时返回true，反之返回false
 */
fun Context.compareApkInfo(info: PackageInfo): Boolean {
  return info.packageName == packageName &&
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            info.longVersionCode > getPackageInfo().getOrThrow().longVersionCode
          else info.versionCode > getPackageInfo().getOrThrow().versionCode
}

/**
 * 获取App名称
 *
 * @param packageName 包名
 * @return App名称
 */
fun Context.getAppName(packageName: String = this.packageName): Result<String> {
  return runCatching {
    val pi = getPackageInfo(packageName).getOrThrow()
    pi.applicationInfo.loadLabel(packageManager).toString()
  }
}

/**
 * 获取App图标
 *
 * @return App图标
 */
fun Context.getAppIcon(packageName: String = this.packageName): Result<Drawable> {
  return runCatching {
    val pi = getPackageInfo(packageName).getOrThrow()
    pi.applicationInfo.loadIcon(packageManager)
  }
}

/**
 * 获取应用程序版本名称信息
 * @return 当前应用的版本名称
 */
fun Context.getVersionName(packageName: String = this.packageName): Result<String> {
  return runCatching {
    getPackageInfo(packageName).getOrThrow().versionName
  }
}

/**
 * 获取应用程序版本名称信息
 * @return 当前应用的版本名称
 */
fun Context.getVersionCode(packageName: String = this.packageName): Result<Long> {
  return runCatching {
    getPackageInfo(packageName).getOrThrow().let {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        it.longVersionCode
      } else it.versionCode.toLong()
    }
  }
}

/**
 * 判断App是否是系统应用
 *
 * @return `true`: 是<br></br>`false`: 否
 */
fun Context.isSystemApp(packageName: String = this.packageName): Result<Boolean> {
  return runCatching {
    getApplicationInfo(packageName).getOrThrow().flags and ApplicationInfo.FLAG_SYSTEM != 0
  }
}

/**
 * 获取清单文件信息
 * @return
 */
fun Context.getApplicationMetaData(packageName: String = this.packageName): Result<Bundle> {
  return runCatching {
    val appInfo = getApplicationInfo(packageName, PackageManager.GET_META_DATA).getOrThrow()
    appInfo.metaData
  }
}

/**
 * 获取清单文件信息
 * @return
 */
fun Context.getActivityMetaData(
  activityName: String,
  packageName: String = this.packageName,
): Result<Bundle> {
  return runCatching {
    val appInfo =
      getActivityInfo(activityName, packageName, PackageManager.GET_META_DATA).getOrThrow()
    appInfo.metaData
  }
}

/**
 * 获取清单文件信息
 * @return
 */
fun Context.getServiceMetaData(
  serviceName: String,
  packageName: String = this.packageName,
): Result<Bundle> {
  return runCatching {
    val appInfo =
      getServiceInfo(serviceName, packageName, PackageManager.GET_META_DATA).getOrThrow()
    appInfo.metaData
  }
}

/**
 * 获取清单文件信息
 * @return
 */
fun Context.getReceiverMetaData(
  receiverName: String,
  packageName: String = this.packageName,
): Result<Bundle> {
  return runCatching {
    val appInfo =
      getReceiverInfo(receiverName, packageName, PackageManager.GET_META_DATA).getOrThrow()
    appInfo.metaData
  }
}

/**
 * 判断App是否是Debug版本
 *
 * @return `true`: 是<br></br>`false`: 否
 */
fun Context.isAppDebug(packageName: String = this.packageName): Result<Boolean> {
  return runCatching {
    getApplicationInfo(
      packageName
    ).getOrThrow().flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
  }
}

/**
 * 获取App签名
 *
 * @param packageName 包名
 * @return App签名
 */
fun Context.getAppSignature(packageName: String = this.packageName): Result<Array<Signature>> {
  return runCatching {
    getPackageInfo(packageName, PackageManager.GET_SIGNATURES).getOrThrow().signatures
  }
}

/**
 * 获取应用签名的的SHA1值
 *
 * 可据此判断高德，百度地图key是否正确
 *
 * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88
 * :1B:8B:E8:54:42
 */
fun Context.getAppSignatureSHA1(packageName: String = this.packageName): Result<String> {
  return runCatching {
    getAppSignature(packageName).getOrThrow()[0].toByteArray().toHex()
      .replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
  }
}

/**
 * 判断App是否处于前台
 *
 * 当不是查看当前App，且SDK大于21时，
 * 需添加权限 `<uses-permission android:name="android.permission
 * .PACKAGE_USAGE_STATS"/>`
 *
 * @param packageName 包名
 * @return `true`: 是<br></br>`false`: 否
 */
fun Context.isAppForeground(packageName: String = this.packageName): Result<Boolean> {
  return runCatching {
    getRunningAppProcessInfo().getOrThrow().forEach {
      if (it.processName == packageName) {
        return@runCatching it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
      }
    }
    false
  }
}

/**
 * 获取当前正在运行的进程信息
 *
 * @return
 */
fun Context.getRunningAppProcessInfo(): Result<MutableList<ActivityManager.RunningAppProcessInfo>> {
  return runCatching {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    am.runningAppProcesses
  }
}

/**
 * 获取进程名
 *
 * @param pid
 * @return
 */
fun Context.getProcessName(pid: Int = Process.myPid()): Result<String?> {
  return runCatching {
    getRunningAppProcessInfo().getOrThrow().forEach {
      if (it.pid == pid) {
        return@runCatching it.processName
      }
    }
    null
  }
}

/**
 * 获取运行时最大内存
 *
 * @return
 */
fun getRuntimeMaxMemory(): Long {
  return Runtime.getRuntime().maxMemory()
}

/**
 * 获取运行时空闲内存
 *
 * @return
 */
fun getRuntimeFreeMemory(): Long {
  return Runtime.getRuntime().freeMemory()
}

/**
 * 获取运行时最大内存
 *
 * @return
 */
fun getRuntimeTotalMemory(): Long {
  return Runtime.getRuntime().totalMemory()
}

/**
 * 是否忽略电池优化
 *
 * @return
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Context.isIgnoreBatteryOptimization(): Boolean {
  return powerManager().isIgnoringBatteryOptimizations(packageName)
}

/**
 * 打开电池优化
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@RequiresPermission(value = Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
fun Context.openIgnoreBatteryOptimization() {
  openBatterySaverSettings {
    setData(Uri.parse("package:$packageName"))
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
}