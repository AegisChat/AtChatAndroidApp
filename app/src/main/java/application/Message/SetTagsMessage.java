package application.Message;
import java.util.ArrayList;
import application.Tag.Tag;

public class SetTagsMessage  extends Message{
    public SetTagsMessage () {
    }
    private ArrayList<Tag>tagMessage;

    public ArrayList<Tag> getTagMessage() {
        return tagMessage;
    }
    public void setTagMessage(ArrayList<Tag> tagMessage) {
        this.tagMessage = tagMessage;
    }

}