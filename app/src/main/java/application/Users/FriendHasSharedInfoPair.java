package application.Users;

import java.util.UUID;

public class FriendHasSharedInfoPair {

	private UUID FriendID;
	private boolean hasSharedInfo;

	public FriendHasSharedInfoPair() {
		hasSharedInfo = false;
	}

	public UUID getFriendID() {
		return FriendID;
	}

	public void setFriendID(UUID friendID) {
		FriendID = friendID;
	}

	public boolean isHasSharedInfo() {
		return hasSharedInfo;
	}

	public void setHasSharedInfo(boolean hasSharedInfo) {
		this.hasSharedInfo = hasSharedInfo;
	}

}