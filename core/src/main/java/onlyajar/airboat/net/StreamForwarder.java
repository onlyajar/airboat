package onlyajar.airboat.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamForwarder implements Runnable {
    private final InputStream input;
    private final OutputStream output;
    private final ForwarderSession forwarderSession;
    private final String direction;

    public StreamForwarder(InputStream input, OutputStream output, ForwarderSession forwarderSession, String direction) {
        this.input = input;
        this.output = output;
        this.forwarderSession = forwarderSession;
        this.direction = direction;
    }

    @Override
    public void run() {
        while (true) {
            if(!forwarderSession.isAlive()) break;
            try {
                byte[] receiveData = forwarderSession.getDataProtocol().onRead(input);
                process(receiveData);
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
        onClose();
        forwarderSession.setAlive(false);
    }

    protected void process(byte[] data) throws IOException {
        forwarderSession.getDataProtocol().onWrite(output, data);
    }

    public void onClose(){

    }
}
