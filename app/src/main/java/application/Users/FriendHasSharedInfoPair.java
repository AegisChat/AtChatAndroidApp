package application.Users;

import java.util.UUID;

public class FriendHasSharedInfoPair {
	
	private UUID FriendID;
	private boolean hasSharedInfo;
	
	public FriendHasSharedInfoPair() {
		hasSharedInfo = false;
	}

	public UUID getFriendName() {
		return FriendID;
	}

	public void setFriendName(UUID friendName) {
		FriendID = friendName;
	}

	public boolean isHasSharedInfo() {
		return hasSharedInfo;
	}

	public void setHasSharedInfo(boolean hasSharedInfo) {
		this.hasSharedInfo = hasSharedInfo;
	}

}
