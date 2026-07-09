package onlyajar.airboat.arch;

import androidx.core.util.Consumer;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class Component extends ViewModel implements Observer<EventData> {

    private final Map<Class<? extends EventData>, Consumer<? extends EventData>> registerHandlerMap = new HashMap<>();
    private final Messenger messenger;

    public Component(Messenger messenger) {
        this.messenger = messenger;
        messenger.getPageData().observeForever(this);
        registerObserves();
    }

    public void registerObserves() {

    }

    public Messenger getMessenger() {
        return messenger;
    }


    public <D extends EventData> void observe(Class<D> clazz, Consumer<D> consumer) {
        registerHandlerMap.put(clazz, consumer);
    }

    @Override
    public void onChanged(EventData eventData) {
        Consumer consumer = registerHandlerMap.get(eventData.getClass());
        if (consumer != null) {
            consumer.accept(eventData);
        } else {
            System.out.println(eventData.getClass().getSimpleName());
        }

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        messenger.getControllerData().removeObserver(this);
        registerHandlerMap.clear();
    }

}
