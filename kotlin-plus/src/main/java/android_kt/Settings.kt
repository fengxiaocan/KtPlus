package android_kt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * 根据包名打开应用的设置界面
 */
fun Context.openAppSettings(
  packageName: String = this.packageName, applyBlock: Intent.() -> Unit = {}
) {
  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    setData(Uri.fromParts("package", packageName, null))
    applyBlock(this)
    startActivity(this)
  }
}

/**
 * 根据系统设置界面
 */
fun Context.openSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_SETTINGS).apply(applyBlock))

/**
 * APN 设置界面
 */
fun Context.openApnSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_APN_SETTINGS).apply(applyBlock))

/**
 * 位置服务界面
 */
fun Context.openLocationSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply(applyBlock))

/**
 * 更多连接方式设置界面
 */
fun Context.openAirplaneSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply(applyBlock))

/**
 * 双卡和移动网络设置界面
 */
fun Context.openDataRoamingSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply(applyBlock))

/**
 * 无障碍设置界面
 */
fun Context.openAccessibilitySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply(applyBlock))

/**
 * 同步设置界面
 */
fun Context.openSyncSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_SYNC_SETTINGS).apply(applyBlock))

/**
 * 添加账户界面
 */
fun Context.openAddAccountSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_ADD_ACCOUNT).apply(applyBlock))

/**
 * 选取运营商的界面
 */
fun Context.openNetworkOperatorSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS).apply(applyBlock))

/**
 * 安全设置界面
 */
fun Context.openSecuritySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS).apply(applyBlock))

/**
 * 备份重置设置界面
 */
fun Context.openPrivacySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_PRIVACY_SETTINGS).apply(applyBlock))

/**
 * VPN 设置界面, 可能不存在
 */
@RequiresApi(Build.VERSION_CODES.N)
fun Context.openVpnSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_VPN_SETTINGS).apply(applyBlock))

/**
 * 无线网设置界面
 */
fun Context.openWifiSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply(applyBlock))

/**
 * WIFI 的 IP 设置
 */
fun Context.openWifiIpSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_WIFI_IP_SETTINGS).apply(applyBlock))

/**
 * 蓝牙设置
 */
fun Context.openBluetoothSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply(applyBlock))

/**
 * 投射设置
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.openCastSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_CAST_SETTINGS).apply(applyBlock))

/**
 * 日期时间设置
 */
fun Context.openDateSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_DATE_SETTINGS).apply(applyBlock))

/**
 * 声音设置
 */
fun Context.openSoundSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_SOUND_SETTINGS).apply(applyBlock))

/**
 * 显示设置
 */
fun Context.openDisplaySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply(applyBlock))

/**
 * 语言设置
 */
fun Context.openLocaleSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS).apply(applyBlock))

/**
 * 辅助应用和语音输入设置
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.openVoiceInputSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS).apply(applyBlock))

/**
 * 语言和输入法设置
 */
fun Context.openInputMethodSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply(applyBlock))

/**
 * 个人字典设置界面
 */
fun Context.openUserDictionarySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_USER_DICTIONARY_SETTINGS).apply(applyBlock))

/**
 * 存储空间设置的界面
 */
fun Context.openInternalStorageSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply(applyBlock))

/**
 * 搜索设置界面
 */
fun Context.openSearchSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_SEARCH_SETTINGS).apply(applyBlock))

/**
 * 开发者选项设置
 */
fun Context.openDevelopmentSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply(applyBlock))

/**
 * 手机状态信息的界面
 */
fun Context.openDeviceInfoSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply(applyBlock))

/**
 * 互动屏保设置的界面
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun Context.openDreamSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_DREAM_SETTINGS).apply(applyBlock))

/**
 * 通知使用权设置的界面
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun Context.openNotificationSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply(applyBlock))

/**
 * 勿扰权限设置的界面
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Context.openNotificationPolicySettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply(applyBlock))

/**
 * 字幕设置的界面
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Context.openCaptioningSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_CAPTIONING_SETTINGS).apply(applyBlock))

/**
 * 打印设置界面
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Context.openPrintSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_PRINT_SETTINGS).apply(applyBlock))

/**
 * 节电助手界面
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun Context.openBatterySaverSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply(applyBlock))

/**
 * 主屏幕设置界面
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.openHomeSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_HOME_SETTINGS).apply(applyBlock))

/**
 * 应用程序界面
 */
fun Context.openApplicationSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS).apply(applyBlock))

/**
 * 所有的应用程序界面
 */
fun Context.openAllApplicationSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS).apply(applyBlock))

/**
 * 已安装的的应用程序界面
 */
fun Context.openInstalledApplicationSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS).apply(applyBlock))

/**
 * 快速启动界面
 */
fun Context.openQuickLaunchSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_QUICK_LAUNCH_SETTINGS).apply(applyBlock))

/**
 * NFC设置
 */
fun Context.openNfcSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_NFC_SETTINGS).apply(applyBlock))

/**
 * NFC共享
 */
fun Context.openNfcSharingSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_NFCSHARING_SETTINGS).apply(applyBlock))

/**
 * 存储设置 【记忆卡存储】
 */
fun Context.openMemoryCardSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_MEMORY_CARD_SETTINGS).apply(applyBlock))

/**
 * 语言选择界面 【多国语言选择】
 */
fun Context.openSubtypeSettings(applyBlock: Intent.() -> Unit = {}) =
  startActivity(Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS).apply(applyBlock))