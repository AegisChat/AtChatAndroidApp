package application.Message;

public class FriendRequestMessage extends Message{

	private String name;

	public FriendRequestMessage() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
