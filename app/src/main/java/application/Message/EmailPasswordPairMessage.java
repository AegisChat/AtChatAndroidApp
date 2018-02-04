package application.Message;

public class EmailPasswordPairMessage extends Message{

	private String email;
	private String password;
	
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
}
