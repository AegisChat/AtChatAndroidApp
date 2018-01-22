package application.Message;

import application.Users.User;

public class UpdateUserMessage extends Message{

	private User updatedUser;
	
	public UpdateUserMessage() {
		
	}

	public User getUpdatedUser() {
		return updatedUser;
	}

	public void setUpdatedUser(User updatedUser) {
		this.updatedUser = updatedUser;
	}

}
