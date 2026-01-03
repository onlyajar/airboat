package onlyajar.airboat.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public final class BluetoothUtils {
    private BluetoothUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean support(){
        BluetoothManager bluetoothManager =(BluetoothManager) AppUtils.getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter != null;
    }

}
