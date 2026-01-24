package onlyajar.airboat.arch;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Component extends ViewModel implements Observer<Data> {
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
            if (m.getParameterCount() == 1 && Data.class.isAssignableFrom(m.getParameterTypes()[0])) {
                m.setAccessible(true);
                dataHandlerMap.put(m.getParameterTypes()[0], m);
            }
        }
    }
    @Override
    public void onChanged(Data data) {
        Method method =  dataHandlerMap.get(data.getClass());
        if (method != null) {
            try {
                method.invoke(this, data);
            } catch (Exception e) {
                //
            }
        }else {
            System.out.println(data.getClass().getSimpleName());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        messenger.getControllerData().removeObserver(this);
        dataHandlerMap.clear();
    }

}
