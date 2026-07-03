package onlyajar.airboat.arch;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class PageFragment extends ArchFragment implements PageScreen{

    private Messenger messenger;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messenger = new ViewModelProvider(requireActivity()).get(Messenger.class);
        messenger.getControllerData().observe(getViewLifecycleOwner(), this::onReceiveOutData);
        onOpen();
    }

    @Override
    public void onOpen() {
        messenger.sendToController(new PageReadyEvent());
    }

    @Override
    public void toSendInputData(EventData data) {
        messenger.sendToController(data);
    }

    @Override
    public void onReceiveOutData(EventData data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onClose();
    }

    @Override
    public void onClose() {
        messenger.sendToController(new PageReadyEvent());
    }
}
