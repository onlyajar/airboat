package onlyajar.airboat.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataProtocol {

    void onWrite(OutputStream outputStream, byte[] bytes) throws IOException;

    byte[] onRead(InputStream inputStream) throws IOException;
}
