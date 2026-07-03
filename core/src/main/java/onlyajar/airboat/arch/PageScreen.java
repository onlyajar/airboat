package onlyajar.airboat.arch;

public interface PageScreen {

    void onOpen();

    void toSendInputData(EventData data);

    void onReceiveOutData(EventData data);

    void onClose();
}
