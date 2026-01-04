package onlyajar.airboat.net;

import javax.net.ssl.SSLSocketFactory;

public class ForwarderSession {
    private final DataProtocol dataProtocol;
    private volatile boolean alive;

    public ForwarderSession(DataProtocol dataProtocol) {
        this.dataProtocol = dataProtocol;
    }

    public DataProtocol getDataProtocol() {
        return dataProtocol;
    }

    public synchronized boolean isAlive() {
        return alive;
    }

    public synchronized void setAlive(boolean alive) {
        this.alive = alive;
    }
}
