package onlyajar.airboat.net;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import onlyajar.airboat.utils.AppUtils;


public class BTServer {
    private static final String NAME = "POS_BT";
    public static final UUID BT_UUID = UUID.fromString("c944245a-6975-41dc-92fc-a9a3fa20c576");
    private BluetoothServerSocket bServerSocket;
    ExecutorService executorService = Executors.newCachedThreadPool();
    private BluetoothAdapter bluetoothAdapter;
    private final KitSocketHandler kitSocketHandler;
    public BTServer(KitSocketHandler kitSocketHandler) {
        this.kitSocketHandler = kitSocketHandler;
        init();
    }

    private void init(){
        BluetoothManager bluetoothManager =(BluetoothManager) AppUtils.getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public void startup(UUID uuid) throws IOException{
        startup(true, uuid);
    }
    public void startup(boolean secure, UUID uuid) throws IOException{

        try(BluetoothServerSocket serverSocket =
                    secure? bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid)
                : bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, uuid)){
            bServerSocket = serverSocket;
            while (true){
                final BluetoothSocket socket = bServerSocket.accept();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        kitSocketHandler.accept(new KitSocket(socket));
                    }
                });
            }
        }catch (IOException e){
            e.fillInStackTrace();
            throw e;
        }
    }

    public void shutdown(){
        if(bServerSocket != null){
            try {
                bServerSocket.close();
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }
}
