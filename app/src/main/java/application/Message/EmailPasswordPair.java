package application.Message;

public class EmailPasswordPair extends Message{

	private String email;
	private String password;
	
	public EmailPasswordPair() {
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
