package application.Message;

import java.util.List;

import application.Users.UserTemplate;

public class GetConversationListMessage extends Message{
	
	private List<UserTemplate> conversations;
	
	public GetConversationListMessage() {
		
	}
	
	public GetConversationListMessage(List<UserTemplate> conversations) {
		this.setConversations(conversations);
	}

	public List<UserTemplate> getConversations() {
		return conversations;
	}

	public void setConversations(List<UserTemplate> conversations) {
		this.conversations = conversations;
	}

}
