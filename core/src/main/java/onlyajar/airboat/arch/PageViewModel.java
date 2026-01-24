package onlyajar.airboat.arch;

public class PageViewModel extends Component {

    public PageViewModel(Messenger messenger) {
        super(messenger);
        messenger.getPageData().observeForever(this);
    }

    public void sendDataToController(Data data){
        getMessenger().sendToController(data);
    }

}
