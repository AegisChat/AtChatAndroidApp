package application.Message;

public class UpdatePairingDistanceMessage extends Message{
	
	private double distance;
	
	public UpdatePairingDistanceMessage() {
		
	}
	
	public UpdatePairingDistanceMessage(double distance) {
		this.setDistance(distance);
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	

}
