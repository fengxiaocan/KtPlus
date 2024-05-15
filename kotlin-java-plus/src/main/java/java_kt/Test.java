package java_kt;

public class Test {
    public static void main(String[] args) {
//        String msg = "ACTION_VPN_SETTINGS    VPN 设置界面, 可能不存在    " +
//                "ACTION_WIFI_SETTINGS    无线网设置界面    " +
//                "ACTION_WIFI_IP_SETTINGS    WIFI 的 IP 设置    " +
//                "ACTION_BLUETOOTH_SETTINGS    蓝牙设置    " +
//                "ACTION_CAST_SETTINGS    投射设置    " +
//                "ACTION_DATE_SETTINGS    日期时间设置    " +
//                "ACTION_SOUND_SETTINGS    声音设置    " +
//                "ACTION_DISPLAY_SETTINGS    显示设置    " +
//                "ACTION_LOCALE_SETTINGS    语言设置    " +
//                "ACTION_VOICE_INPUT_SETTINGS    辅助应用和语音输入设置    " +
//                "ACTION_INPUT_METHOD_SETTINGS    语言和输入法设置    " +
//                "ACTION_USER_DICTIONARY_SETTINGS    个人字典设置界面    " +
//                "ACTION_INTERNAL_STORAGE_SETTINGS    存储空间设置的界面    " +
//                "ACTION_SEARCH_SETTINGS    搜索设置界面    " +
//                "ACTION_APPLICATION_DEVELOPMENT_SETTINGS    开发者选项设置    " +
//                "ACTION_DEVICE_INFO_SETTINGS    手机状态信息的界面    " +
//                "ACTION_DREAM_SETTINGS    互动屏保设置的界面    " +
//                "ACTION_NOTIFICATION_LISTENER_SETTINGS    通知使用权设置的界面    " +
//                "ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS    勿扰权限设置的界面    " +
//                "ACTION_CAPTIONING_SETTINGS    字幕设置的界面    " +
//                "ACTION_PRINT_SETTINGS    打印设置界面    " +
//                "ACTION_BATTERY_SAVER_SETTINGS    节电助手界面    " +
//                "ACTION_HOME_SETTINGS    主屏幕设置界面";
//
//        String[] split = msg.split("    ");
//        for (int i = 0; i < split.length; i+=2) {
//            String action = split[i];
//            String doc = split[i+1];
//            System.err.println("/**");
//            System.err.println(" * "+doc);
//            System.err.println(" */");
//            System.err.print("fun Context.open");
//            String string = action.substring(7);
//            String[] split1 = string.split("_");
//            for (String s : split1) {
//                System.err.print(s.substring(0,1).toUpperCase());
//                System.err.print(s.substring(1).toLowerCase());
//            }
//            System.err.print("() = startActivity(Intent(Settings.");
//            System.err.print(action);
//            System.err.println("))");
//        }

        String value = "WINDOW_SERVICE, WindowManager, LAYOUT_INFLATER_SERVICE, android.view.LayoutInflater, ACTIVITY_SERVICE, ActivityManager, POWER_SERVICE, android.os.PowerManager, ALARM_SERVICE, android.app.AlarmManager, NOTIFICATION_SERVICE, android.app.NotificationManager, KEYGUARD_SERVICE, android.app.KeyguardManager, LOCATION_SERVICE, android.location.LocationManager, SEARCH_SERVICE, android.app.SearchManager, SENSOR_SERVICE, android.hardware.SensorManager, STORAGE_SERVICE, StorageManager, VIBRATOR_MANAGER_SERVICE, android.os.VibratorManager, VIBRATOR_SERVICE, android.os.Vibrator, CONNECTIVITY_SERVICE, android.net.ConnectivityManager, WIFI_SERVICE, android.net.wifi.WifiManager, AUDIO_SERVICE, android.media.AudioManager, MEDIA_ROUTER_SERVICE, android.media.MediaRouter, TELEPHONY_SERVICE, android.telephony.TelephonyManager, TELEPHONY_SUBSCRIPTION_SERVICE, android.telephony.SubscriptionManager, CARRIER_CONFIG_SERVICE, android.telephony.CarrierConfigManager, EUICC_SERVICE, android.telephony.euicc.EuiccManager,  INPUT_METHOD_SERVICE, android.view.inputmethod.InputMethodManager, UI_MODE_SERVICE, android.app.UiModeManager, DOWNLOAD_SERVICE, android.app.DownloadManager, BATTERY_SERVICE, android.os.BatteryManager, JOB_SCHEDULER_SERVICE, android.app.job.JobScheduler, NETWORK_STATS_SERVICE, android.app.usage.NetworkStatsManager, android.os.HardwarePropertiesManager, HARDWARE_PROPERTIES_SERVICE, DOMAIN_VERIFICATION_SERVICE, android.content.pm.verify.domain.DomainVerificationManager, DISPLAY_HASH_SERVICE, android.view.displayhash.DisplayHashManager";
        String[] split = value.split(",");
        for (int i = 1; i < split.length; i+=2) {
            System.err.println("import "+split[i].trim());
        }
        for (int i = 0; i < split.length; i+=2) {
            String name = split[i+1].trim();
            String realName = name.contains(".")?name.substring(name.lastIndexOf(".")+1):name;
            String na = realName.substring(0,1).toLowerCase()+realName.substring(1);
            System.err.println("fun Context."+ na+"()=");
            System.err.println("getSystemService(Context."+split[i].trim()+") as "+realName);
        }
    }
}
