package application.Message;

public class NewUserCreatedMessage extends Message{

	private boolean newUserCreated;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String alias;
	
	public NewUserCreatedMessage() {
		
	}
	
	public NewUserCreatedMessage(boolean newUserCreated) {
		this.setNewUserCreated(newUserCreated);
	}

	public boolean isNewUserCreated() {
		return newUserCreated;
	}

	public void setNewUserCreated(boolean newUserCreated) {
		this.newUserCreated = newUserCreated;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
