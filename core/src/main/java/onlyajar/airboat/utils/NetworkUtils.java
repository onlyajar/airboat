package onlyajar.airboat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public final class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static boolean simEnable(){
        Context context = AppUtils.getApplication();
        ConnectivityManager connMgr =
                (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        boolean enable = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkCapabilities networkCapabilities = connMgr.getNetworkCapabilities(network);
            boolean transport = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            if(transport){
                enable = true;
                break;
            }
        }
        return enable;
    }

    /**
     *
     * @param transportType
     * {@link NetworkCapabilities#TRANSPORT_CELLULAR}
     * {@link NetworkCapabilities#TRANSPORT_WIFI}
     * {@link NetworkCapabilities#TRANSPORT_ETHERNET}
     * @return
     */
    public Network getNetwork(int transportType) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                AppUtils.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null){
            return null;
        }
        Network[] networks = connectivityManager.getAllNetworks();
        for(Network network : networks){
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasTransport(transportType)){
                return network;
            }
        }
        return null;
    }
    /**
     * 设置进程将要使用的网络类型，进程后续的所有通讯都将强制使用该网络
     *  【但是】如果该网络出错，进程将无法再通讯，而不会自动切换其他网络
     * @param network   网络对象，可通过{@link NetworkUtils#getNetwork(int)}进行获取
     *                  当该值为null将恢复安卓默认的网络优先级
     * @return          是否执行成功
     */
    public static boolean bindProcessToNetwork(Network network){
        if(Build.VERSION.SDK_INT >= 23){
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    AppUtils.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager == null){
                return false;
            }
            return connectivityManager.bindProcessToNetwork(network);
        } else {
            return ConnectivityManager.setProcessDefaultNetwork(network);
        }
    }

    /**
     * 判断是否打开了WIFI开关
     * @return
     */
    public static boolean isWifiEnabled(){
        try{
            WifiManager wifiManager = (WifiManager) AppUtils.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWifiEnabled();
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断是否打开了移动网络的开关
     * @return
     */
    public static boolean isMobileEnabled() {
        try {
            Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    AppUtils.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            return (Boolean) getMobileDataEnabledMethod.invoke(connectivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 反射失败，默认开启
        return true;
    }

    /**
     * 判断是否有SIM卡
     * @return
     */
    public static boolean hasSimCard() {
        TelephonyManager telMgr = (TelephonyManager)
                AppUtils.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
            default:
                break;
        }
        return result;
    }
}
