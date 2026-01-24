package onlyajar.airboat.arch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class Messenger extends ViewModel {
    private final MutableLiveData<Data> pageData = new MutableLiveData<>();
    private final MutableLiveData<Data> controllerData = new MutableLiveData<>();



    public MutableLiveData<Data> getPageData() {
        return pageData;
    }

    public MutableLiveData<Data> getControllerData() {
        return controllerData;
    }

    public void sendToController(Data data) {
        controllerData.setValue(data);
    }

    public void sendToPage(Data data) {
        pageData.setValue(data);
    }
}

