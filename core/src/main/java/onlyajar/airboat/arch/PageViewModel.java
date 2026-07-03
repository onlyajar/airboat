package onlyajar.airboat.arch;

public class PageViewModel extends Component {

    public PageViewModel(Messenger messenger) {
        super(messenger);

    }

    public void sendDataToController(EventData eventData){
        getMessenger().sendToController(eventData);
    }

}
