package onlyajar.airboat.net;

import android.bluetooth.BluetoothSocket;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AcceptSocket implements Closeable {
    private Closeable closeable;
    private InputStream inputStream;
    private OutputStream outputStream;
    public AcceptSocket(Socket socket){
        closeable = socket;
    }
    public AcceptSocket(BluetoothSocket socket){
        closeable = socket;

    }
    public InputStream getInputStream() throws IOException {
        if(inputStream != null) return inputStream;
        if(closeable instanceof  Socket){
            inputStream = ((Socket)closeable).getInputStream();
        } else if(closeable instanceof  BluetoothSocket){
            inputStream = ((BluetoothSocket)closeable).getInputStream();
        }
        return inputStream;
    }
    public OutputStream getOutputStream() throws IOException {
        if(outputStream != null) return outputStream;
        if(closeable instanceof  Socket){
            outputStream = ((Socket)closeable).getOutputStream();
        } else if(closeable instanceof  BluetoothSocket){
            outputStream = ((BluetoothSocket)closeable).getOutputStream();
        }
        return outputStream;
    }
    @Override
    public void close() {
        try{
            if(inputStream != null){
                inputStream.close();
            }
            if(outputStream != null){
                outputStream.close();

            }
            if(closeable != null){
                closeable.close();
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }
}
