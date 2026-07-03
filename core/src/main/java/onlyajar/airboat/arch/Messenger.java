package onlyajar.airboat.arch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class Messenger extends ViewModel {
    private final MutableLiveData<EventData> pageData = new MutableLiveData<>();
    private final MutableLiveData<EventData> controllerData = new MutableLiveData<>();

    public MutableLiveData<EventData> getPageData() {
        return pageData;
    }

    public MutableLiveData<EventData> getControllerData() {
        return controllerData;
    }

    public void sendToController(EventData eventData) {
        controllerData.setValue(eventData);
    }

    public void sendToPage(EventData eventData) {
        pageData.setValue(eventData);
    }
}

