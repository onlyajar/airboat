package onlyajar.airboat.net;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class AcceptSocketHandler implements KitSocketHandler, IoHandler{
    @Override
    public void accept(KitSocket socket) {
        IoStatus step = IoStatus.CONNECT;
        try {
            socketOpened(socket);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            step = IoStatus.READ;
            DataProtocol protocol = getDataProtocol();
            byte[] receiveData = protocol.onRead(inputStream);
            messageReceived(receiveData);
            step = IoStatus.PROCESS;
            byte[] processData = process(receiveData);
            step = IoStatus.WRITE;
            protocol.onWrite(outputStream, processData);
            step = IoStatus.NORMAL;
            messageSent(processData);
            try {
                socket.close();
            }catch (Exception e){
                e.fillInStackTrace();
            }
        }catch (Exception e){
            exceptionCaught(e, step);
        }
    }

    @Override
    public void socketOpened(KitSocket socket) {

    }

    @Override
    public void messageReceived(byte[] data) {

    }

    @Override
    public void messageSent(byte[] data) {

    }


    public DataProtocol getDataProtocol(){
        return new LengthHeadProtocol();
    }

    public abstract byte[] process(byte[] data);
}
