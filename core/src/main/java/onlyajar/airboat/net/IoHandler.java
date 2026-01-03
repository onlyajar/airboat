package onlyajar.airboat.net;

public interface IoHandler {
    void socketOpened(KitSocket socket);

    void messageSent(byte[] data);

    void messageReceived(byte[] data);

    void exceptionCaught(Throwable throwable, IoStatus status);
}
