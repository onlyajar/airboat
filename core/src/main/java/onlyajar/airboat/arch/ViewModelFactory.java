package onlyajar.airboat.arch;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Messenger messenger;

    public ViewModelFactory(Messenger messenger) {
        this.messenger = messenger;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getDeclaredConstructor(Messenger.class)
                    .newInstance(messenger);
        } catch (Exception ignored) {
            throw  new UnsupportedOperationException(modelClass.getName());
        }
    }
}
