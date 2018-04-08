package application.Message;

import java.util.UUID;

public class RemoveFriendMessage extends Message{

    private UUID uuid;
    public RemoveFriendMessage(){

    }

    public RemoveFriendMessage(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
