package onlyajar.airboat.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import onlyajar.airboat.arch.Component;

public class InputAmountViewModel extends Component {

    public final MutableLiveData<String> dateTime = new MyMutableLiveData<>();

    public void update(String dt){
        dateTime.setValue(dt);
    }
}
