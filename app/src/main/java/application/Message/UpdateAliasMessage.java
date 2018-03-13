package application.Message;

public class UpdateAliasMessage extends Message{

	private String newAlias;
	
	public UpdateAliasMessage() {
		
	}

	public String getNewAlias() {
		return newAlias;
	}

	public void setNewAlias(String newAlias) {
		this.newAlias = newAlias;
	}
	
}
