package application.Message;

public class UpdateFirebaseIDMessage extends Message{
	
	private String firebaseID;
	
	public UpdateFirebaseIDMessage() {
		
	}

	public String getFirebaseID() {
		return firebaseID;
	}

	public void setFirebaseID(String firebaseID) {
		this.firebaseID = firebaseID;
	}

}
