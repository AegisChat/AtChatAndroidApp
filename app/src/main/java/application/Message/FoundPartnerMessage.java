package application.Message;

import application.Users.UserTemplate;

public class FoundPartnerMessage extends Message{

	private UserTemplate partner;
	private double distanceToPartner;

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

	public double getDistanceToPartner() {
		return distanceToPartner;
	}

	public void setDistanceToPartner(double distanceToPartner) {
		this.distanceToPartner = distanceToPartner;
	}
	
}
