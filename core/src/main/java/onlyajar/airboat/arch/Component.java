package onlyajar.airboat.arch;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Component extends ViewModel implements Observer<EventData> {
    private final Map<Class<?>, Method> dataHandlerMap = new HashMap<>();
    private final Messenger messenger;
    public Component(Messenger messenger) {
        this.messenger = messenger;
        initDataHandler();
    }

    public Messenger getMessenger() {
        return messenger;
    }

    private void initDataHandler(){
        Method[] methods = this.getClass().getDeclaredMethods();
        for(Method m : methods){
            if (m.getParameterCount() == 1 && EventData.class.isAssignableFrom(m.getParameterTypes()[0])) {
                m.setAccessible(true);
                dataHandlerMap.put(m.getParameterTypes()[0], m);
            }
        }
    }
    @Override
    public void onChanged(EventData eventData) {
        Method method =  dataHandlerMap.get(eventData.getClass());
        if (method != null) {
            try {
                method.invoke(this, eventData);
            } catch (Exception e) {
                //
            }
        }else {
            System.out.println(eventData.getClass().getSimpleName());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        messenger.getControllerData().removeObserver(this);
        dataHandlerMap.clear();
    }

}
