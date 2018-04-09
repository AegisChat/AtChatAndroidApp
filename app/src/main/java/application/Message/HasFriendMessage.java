package application.Message;

import java.util.UUID;

public class HasFriendMessage extends Message{

	private boolean hasFriend;
	private UUID possbileFriendUUID;
	
	public HasFriendMessage() {
		
	}
	
	public UUID getPossbileFriendUUID() {
		return possbileFriendUUID;
	}

	public void setPossbileFriendUUID(UUID possbileFriendUUID) {
		this.possbileFriendUUID = possbileFriendUUID;
	}

	public boolean isHasFriend() {
		return hasFriend;
	}

	public void setHasFriend(boolean hasFriend) {
		this.hasFriend = hasFriend;
	}
	
}
