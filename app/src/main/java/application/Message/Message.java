package application.Message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")

@JsonSubTypes({
		@JsonSubTypes.Type(value = FriendRequestMessage.class),
		@JsonSubTypes.Type(value = BlockMessage.class),
		@JsonSubTypes.Type(value = CancelQueueMessage.class),
		@JsonSubTypes.Type(value = DenyFriendRequestMessage.class),
		@JsonSubTypes.Type(value = QueueMessage.class),
		@JsonSubTypes.Type(value = SetTagsMessage.class),
		@JsonSubTypes.Type(value = UpdateLocationMessage.class),
		@JsonSubTypes.Type(value = FoundPartnerMessage.class),
		@JsonSubTypes.Type(value = QueueMessage.class),
		@JsonSubTypes.Type(value = VerifyLoginMessage.class),
		@JsonSubTypes.Type(value = AcceptedEmailAddressMessage.class),
		@JsonSubTypes.Type(value = NewUserCreatedMessage.class),
		@JsonSubTypes.Type(value = GetFriendsListMessage.class),
		@JsonSubTypes.Type(value = TextMessage.class),
		@JsonSubTypes.Type(value = SentMessage.class),
		@JsonSubTypes.Type(value = RecievedMessage.class),
		@JsonSubTypes.Type(value = UpdateTagMessage.class),
		@JsonSubTypes.Type(value = UpdatePairingDistanceMessage.class),
		@JsonSubTypes.Type(value = EmailPasswordPairMessage.class),
		@JsonSubTypes.Type(value = GetConversationListMessage.class),
		@JsonSubTypes.Type(value = GetNewMessagesMessage.class),
		@JsonSubTypes.Type(value = UpdateAliasMessage.class)
})
public abstract class Message implements MessageInterface{

	protected UUID sender;
	protected UUID recipient;
    protected UUID id;

	public Message() {
        generateUUID();
    }

	public Message(String stringID){
		id= UUID.fromString(stringID);
	}

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStringToUUID(String id){
	    this.id = UUID.fromString(id);
    }

	public UUID getId() {
		return id;
	}

	public UUID getSender() {
		return sender;
	}

	public void setSender(UUID sender) {
		this.sender = sender;
	}

	public UUID getRecipient() {
		return recipient;
	}

	public void setRecipient(UUID recipient) {
		this.recipient = recipient;
	}

	public void generateUUID(){
	    if(id == null)
	    id = UUID.randomUUID();
    }

	@Override
	public String toString() {
		return "Sender: " + this.getSender() + " " + "Recipient: " + this.getRecipient() + " " + "ID: " + this.getId();
	}

}
