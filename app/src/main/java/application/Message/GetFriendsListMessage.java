package application.Message;

import java.util.ArrayList;
import java.util.List;

import application.Users.UserTemplate;

public class GetFriendsListMessage extends Message{
	
	private List<UserTemplate> friends;
	
	public GetFriendsListMessage() {
		
	}
	
	public GetFriendsListMessage(ArrayList<UserTemplate> friends) {
		this.setFriends(friends);
	}

	public List<UserTemplate> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<UserTemplate> friends) {
		this.friends = friends;
	}
}
