package android_kt

import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.UiModeManager
import android.app.job.JobScheduler
import android.app.usage.NetworkStatsManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.hardware.SensorManager
import android.hardware.biometrics.BiometricManager
import android.hardware.fingerprint.FingerprintManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.IpSecManager
import android.net.VpnManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.rtt.WifiRttManager
import android.os.BatteryManager
import android.os.Build
import android.os.HardwarePropertiesManager
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.euicc.EuiccManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.displayhash.DisplayHashManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi

fun Context.powerManager() = getSystemService(Context.POWER_SERVICE) as PowerManager

fun Context.windowManager() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

fun Context.layoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

fun Context.accountManager() = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

fun Context.activityManager() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

fun Context.alarmManager() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun Context.notificationManager() =
  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.accessibilityManager() =
  getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Context.captioningManager() = getSystemService(Context.CAPTIONING_SERVICE) as CaptioningManager

fun Context.keyguardManager() = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

fun Context.locationManager() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun Context.searchManager() = getSystemService(Context.SEARCH_SERVICE) as SearchManager

fun Context.sensorManager() = getSystemService(Context.SENSOR_SERVICE) as SensorManager

fun Context.storageManager() = getSystemService(Context.STORAGE_SERVICE) as StorageManager

@RequiresApi(Build.VERSION_CODES.O)
fun Context.storageStatsManager() =
  getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager

@RequiresApi(Build.VERSION_CODES.S)
fun Context.vibratorManager() =
  getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

fun Context.vibrator() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

fun Context.connectivityManager() =
  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

@RequiresApi(Build.VERSION_CODES.P)
fun Context.ipSecManager() = getSystemService(Context.IPSEC_SERVICE) as IpSecManager

@RequiresApi(Build.VERSION_CODES.R)
fun Context.vpnManager() = getSystemService(Context.VPN_MANAGEMENT_SERVICE) as VpnManager

@RequiresApi(Build.VERSION_CODES.M)
fun Context.networkStatsManager() =
  getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

fun Context.wifiManager() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

@RequiresApi(Build.VERSION_CODES.O)
fun Context.wifiAwareManager() = getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager

@RequiresApi(Build.VERSION_CODES.P)
fun Context.wifiRttManager() = getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager

fun Context.nsdManager() = getSystemService(Context.NSD_SERVICE) as NsdManager

fun Context.audioManager() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

@RequiresApi(Build.VERSION_CODES.M)
fun Context.fingerprintManager() =
  getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

@RequiresApi(Build.VERSION_CODES.Q)
fun Context.biometricManager() = getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager

fun Context.mediaRouter() = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter

fun Context.telephonyManager() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun Context.subscriptionManager() =
  getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

@RequiresApi(Build.VERSION_CODES.M)
fun Context.carrierConfigManager() =
  getSystemService(Context.CARRIER_CONFIG_SERVICE) as CarrierConfigManager

@RequiresApi(Build.VERSION_CODES.P)
fun Context.euiccManager() = getSystemService(Context.EUICC_SERVICE) as EuiccManager

fun Context.inputMethodManager() =
  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.uiModeManager() = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

fun Context.downloadManager() = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

fun Context.batteryManager() = getSystemService(Context.BATTERY_SERVICE) as BatteryManager

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.jobScheduler() = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

@RequiresApi(Build.VERSION_CODES.N)
fun Context.hardwarePropertiesManager() =
  getSystemService(Context.HARDWARE_PROPERTIES_SERVICE) as HardwarePropertiesManager

@RequiresApi(Build.VERSION_CODES.S)
fun Context.displayHashManager() =
  getSystemService(Context.DISPLAY_HASH_SERVICE) as DisplayHashManager


