package application.Message;

import org.springframework.data.geo.Point;

public class UpdateLocationMessage extends Message{

	private Point newLocation;
	
	public UpdateLocationMessage() {
		
	}

	public Point getNewLocation() {
		return newLocation;
	}

	public void setNewLocation(Point newLocation) {
		this.newLocation = newLocation;
	}

}
