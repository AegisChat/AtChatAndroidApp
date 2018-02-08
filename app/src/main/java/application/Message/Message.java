package application.Message;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")

@JsonSubTypes({
		@Type(value = FriendRequestMessage.class),
		@Type(value = BlockMessage.class),
		@Type(value = DenyFriendRequestMessage.class),
		@Type(value = hasNewMessages.class),
		@Type(value = QueueMessage.class),
		@Type(value = SetTagsMessage.class),
		@Type(value = UpdateLocationMessage.class),
		@Type(value = FoundPartnerMessage.class),
		@Type(value = QueueMessage.class),
		@Type(value = VerifyLoginMessage.class),
		@Type(value = AcceptedEmailAddressMessage.class),
		@Type(value = EmailPasswordPairMessage.class),
		@Type(value = NewUserCreatedMessage.class),
		@Type(value = GetFriendsListMessage.class),
		@Type(value = TextMessage.class),
		@Type(value = SentMessage.class),
		@Type(value = RecievedMessage.class),
})

public abstract class Message implements Cloneable{

	protected UUID sender;
	protected UUID recipient;
	protected final UUID id;

	public Message() {
		id = UUID.randomUUID();
	}

	public UUID getID() {
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

	@Override
	public String toString() {
		return "Sender: " + this.getSender() + " " + "Recipient: " + this.getRecipient() + " " + "ID: " + this.getID();
	}

}
