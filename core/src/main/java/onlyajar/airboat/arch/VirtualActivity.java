package onlyajar.airboat.arch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public final class VirtualActivity implements ViewModelStoreOwner {
    private final ViewModelStore viewModelStore = new ViewModelStore();
    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }


    public void onStop() {
    }

    public void onRestart() {
    }

    public void onDestroy() {
        getViewModelStore().clear();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}
