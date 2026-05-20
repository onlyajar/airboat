package onlyajar.airboat.arch;

public interface DataObserver<T extends EventData> {
    void OnReceiveData(T t);
}
