package onlyajar.airboat.net;

import java.io.EOFException;
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
        byte[] lengthBytes = ByteUtils.intToByteArray(length, length);
        byte[] data = ByteUtils.concat(lengthBytes, bytes);
        outputStream.write(data);
        outputStream.flush();
    }

    @Override
    public byte[] onRead(InputStream inputStream) throws IOException {
        byte[] lengthBytes = new byte[length];
        readFully(inputStream, lengthBytes, 0, length);
        int dataLength = ByteUtils.toInt(lengthBytes);
        byte[] dataBytes = new byte[dataLength];
        readFully(inputStream, dataBytes, 0, dataLength);
        return dataBytes;
    }

    public final void readFully(InputStream inputStream, byte[] bytes) throws IOException {
        readFully(inputStream, bytes, 0, bytes.length);
    }
    public final void readFully(InputStream inputStream , byte[] bytes, int off, int len) throws IOException {
        int acceptCount = 0;
        do {
            int count = inputStream.read(bytes, off + acceptCount, len - acceptCount);
            if (count < 0)
                throw new EOFException();
            acceptCount += count;
        } while(acceptCount < len);
    }
}
