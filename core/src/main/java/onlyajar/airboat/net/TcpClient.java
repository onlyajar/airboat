package onlyajar.airboat.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLContext;

public class TcpClient {
    private Builder builder;
    private TcpClient(Builder builder){
        this.builder = builder;
    }

    public byte[] process(byte[] data){
        IoStatus step = IoStatus.CONNECT;
        try {
            KitSocket kitSocket;
            if(builder.sslContext != null){
                kitSocket = new KitSocket(builder.sslContext.getSocketFactory().createSocket(builder.ip, builder.port));
            }else {
                kitSocket =new KitSocket(new Socket(builder.ip, builder.port));
            }

            if(builder.ioHandler != null)
                builder.ioHandler.socketOpened(kitSocket);
            InputStream inputStream = kitSocket.getInputStream();
            OutputStream outputStream = kitSocket.getOutputStream();
            DataProtocol protocol = builder.protocol;
            step = IoStatus.WRITE;
            protocol.onWrite(outputStream, data);
            if(builder.ioHandler != null)
                builder.ioHandler.messageSent(data);
            step = IoStatus.READ;
            byte[] receiveData = protocol.onRead(inputStream);
            if(builder.ioHandler != null)
                builder.ioHandler.messageReceived(receiveData);
            try {
                kitSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return receiveData;
        } catch (IOException e) {
            if(builder.ioHandler != null)
                builder.ioHandler.exceptionCaught(e, step);
        }
        return null;
    }

    public static class Builder{
        String ip;
        int port;
        SSLContext sslContext;
        int timeOut;
        DataProtocol protocol;

        IoHandler ioHandler;

        public Builder(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public Builder setSSLContext(SSLContext sslContext){
            this.sslContext = sslContext;
            return this;
        }

        public Builder setTimeOut(int timeOut){
            this.timeOut = timeOut;
            return this;
        }


        public Builder setProtocol(DataProtocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setIoHandler(IoHandler ioHandler) {
            this.ioHandler = ioHandler;
            return this;
        }

        public TcpClient build(){
            return new TcpClient(this);
        }

    }

}
