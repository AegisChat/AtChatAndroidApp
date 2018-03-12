package application.Message;

import java.util.ArrayList;

public class GetNewMessagesMessage extends Message{
    private ArrayList<Message> messages;

    public GetNewMessagesMessage(){

    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
