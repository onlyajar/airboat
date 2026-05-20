package onlyajar.airboat.arch;

public class Controller extends Component {

    public Controller(Messenger messenger) {
        super(messenger);
        messenger.getControllerData().observeForever(this);
    }

    public void sendDataToPage(EventData eventData){
        getMessenger().sendToPage(eventData);
    }

}
