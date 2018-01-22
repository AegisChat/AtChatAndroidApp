package application.Message;

import java.util.UUID;

public class DenyFriendRequestMessage extends Message{
	
	private UUID friendRequestID;
	
	public DenyFriendRequestMessage() {
		
	}

	public UUID getFriendRequestID() {
		return friendRequestID;
	}

	public void setFriendRequestID(UUID friendRequestID) {
		this.friendRequestID = friendRequestID;
	}

}
