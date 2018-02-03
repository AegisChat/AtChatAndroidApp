package application.Message;

import application.Users.UserTemplate;

public class FoundPartnerMessage extends Message{

	private UserTemplate partner;

	public FoundPartnerMessage() {
		
	}
	
	public FoundPartnerMessage(UserTemplate partner) {
		this.partner = partner;
	}
	
	public UserTemplate getPartner() {
		return partner;
	}

	public void setPartner(UserTemplate partner) {
		this.partner = partner;
	}
	
}
