package onlyajar.airboat.arch;

public interface DataObserver<T extends Data> {
    void OnReceiveData(T t);
}
