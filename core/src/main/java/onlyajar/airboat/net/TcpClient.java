package onlyajar.airboat.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class TcpClient implements Closeable {
    private Builder builder;
    private AcceptSocket kitSocket;
    private TcpClient(Builder builder){
        this.builder = builder;
    }

    public void connect() throws IOException{
        if(builder.sslSocketFactory != null){
            kitSocket = new AcceptSocket(builder.sslSocketFactory.createSocket(builder.ip, builder.port));
        }else {
            kitSocket =new AcceptSocket(new Socket(builder.ip, builder.port));
        }
    }

    public InputStream getInputStream() throws IOException{
        return kitSocket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException{
        return kitSocket.getOutputStream();
    }

    public byte[] process(byte[] data){
        try {
            InputStream inputStream = getInputStream();
            OutputStream outputStream = getOutputStream();
            DataProtocol protocol = builder.protocol;
            protocol.onWrite(outputStream, data);
            return protocol.onRead(inputStream);
        } catch (IOException e) {
            kitSocket.close();
        }
        return null;
    }

    @Override
    public void close() {
        kitSocket.close();
    }

    public static class Builder{
        String ip;
        int port;
        SSLSocketFactory sslSocketFactory;
        int timeOut;
        DataProtocol protocol;

        public Builder(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public Builder setSSLSocketFactory(SSLSocketFactory sslSocketFactory){
            this.sslSocketFactory = sslSocketFactory;
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

        public TcpClient build(){
            return new TcpClient(this);
        }

    }

}
