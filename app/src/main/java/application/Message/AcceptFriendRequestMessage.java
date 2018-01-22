package application.Message;

import java.util.UUID;

public class AcceptFriendRequestMessage extends Message{
	
	private UUID friendRequestID;
	
	public AcceptFriendRequestMessage() {
		
	}

	public UUID getFriendRequestID() {
		return friendRequestID;
	}

	public void setFriendRequestID(UUID friendRequestID) {
		this.friendRequestID = friendRequestID;
	}
}
