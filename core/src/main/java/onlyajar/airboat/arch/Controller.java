package onlyajar.airboat.arch;

public class Controller extends Component {

    public Controller(Messenger messenger) {
        super(messenger);
    }

    public void sendDataToPage(EventData eventData){
        getMessenger().sendToPage(eventData);
    }

}
