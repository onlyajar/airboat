package onlyajar.airboat.arch;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class VIewModelFactory implements ViewModelProvider.Factory {
    private final LifecycleOwner lifecycleOwner;

    public VIewModelFactory(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getDeclaredConstructor(LifecycleOwner.class)
                    .newInstance(lifecycleOwner);
        } catch (Exception ignored) {
            throw  new UnsupportedOperationException(modelClass.getName());
        }
    }
}
