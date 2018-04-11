package application.Message;

import application.Users.User;

public class EmailPasswordPairMessage extends Message{

	private String email;
	private String password;
	private User user;
	
	public EmailPasswordPairMessage() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
