package application.Message;

import java.util.UUID;

public class RemoveFriendMessage extends Message{

    private UUID friendUuid;

    public RemoveFriendMessage(){

    }

    public RemoveFriendMessage(UUID uuid){
        this.friendUuid = uuid;
    }

    public UUID getFriendUuid() {
        return friendUuid;
    }

    public void setFriendUuid(UUID friendUuid) {
        this.friendUuid = friendUuid;
    }
}
