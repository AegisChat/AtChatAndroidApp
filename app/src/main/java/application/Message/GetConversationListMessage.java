package application.Message;

import java.util.ArrayList;
import java.util.UUID;

import application.Users.UserTemplate;

public class GetConversationListMessage extends Message{

	private ArrayList<UserTemplate> conversations;
	private ArrayList<UUID> conversants;

	public GetConversationListMessage() {

	}

	public GetConversationListMessage(ArrayList<UserTemplate> conversations) {
		this.setConversations(conversations);
	}

	public ArrayList<UserTemplate> getConversations() {
		return conversations;
	}

	public void setConversations(ArrayList<UserTemplate> conversations) {
		this.conversations = conversations;
	}

	public ArrayList<UUID> getConversants() {
		return conversants;
	}

	public void setConversants(ArrayList<UUID> conversants) {
		this.conversants = conversants;
	}

}
