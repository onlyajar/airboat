package onlyajar.airboat.arch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class Messenger extends ViewModel {
    private final MutableLiveData<Data> pageData = new MutableLiveData<>();
    private final MutableLiveData<Data> controllerData = new MutableLiveData<>();


}
