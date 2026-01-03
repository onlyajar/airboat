package onlyajar.airboat.page;

import androidx.lifecycle.MutableLiveData;

public class MyMutableLiveData<T> extends MutableLiveData<T> {
    @Override
    protected void onActive() {
        System.out.println("onActive");
        super.onActive();
    }

    @Override
    protected void onInactive() {
        System.out.println("onInactive");
        super.onInactive();
    }
}
