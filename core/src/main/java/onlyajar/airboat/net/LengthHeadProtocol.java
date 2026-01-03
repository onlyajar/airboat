package onlyajar.airboat.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import onlyajar.airboat.utils.ByteUtils;


public class LengthHeadProtocol implements DataProtocol {
    private final int length;

    public LengthHeadProtocol() {
        this.length = 2;
    }

    public LengthHeadProtocol(int length) {
        this.length = length;
    }

    @Override
    public void onWrite(OutputStream outputStream, byte[] bytes) throws IOException{
        int length = bytes.length;
        byte[] lengthBytes = ByteUtils.intToByteArray(length, 2);
        byte[] data = ByteUtils.concat(lengthBytes, bytes);
        outputStream.write(data);
        outputStream.flush();
    }

    @Override
    public byte[] onRead(InputStream inputStream) throws IOException {
        byte[] lengthBytes = new byte[2];
        int acceptCount = 0;
        do {
            int len = inputStream.read(lengthBytes, acceptCount, 2 - acceptCount);
            if(len == -1) throw new IOException("receive data length exception");
            acceptCount += len;
        } while (acceptCount < 2);
        int dataLength = ByteUtils.toInt(lengthBytes);
        byte[] dataBytes = new byte[dataLength];
        acceptCount = 0;
        do {
            int len = inputStream.read(dataBytes, acceptCount, dataLength - acceptCount);
            if(len == -1) throw new IOException("receive data exception");
            acceptCount += len;
        }while (acceptCount < dataLength);
//        byte[] data = ByteUtils.concat(lengthBytes, dataBytes);
        return dataBytes;
    }
}
