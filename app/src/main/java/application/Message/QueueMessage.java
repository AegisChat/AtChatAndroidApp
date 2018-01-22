package application.Message;

import java.util.UUID;

import org.springframework.data.geo.Point;

public class QueueMessage extends Message{

	private Point newLocation;
	
	
	public QueueMessage() {
	}
	
	public Point getNewLocation() {
		return newLocation;
	}

	public void setNewLocation(Point newLocation) {
		this.newLocation = newLocation;
	}


}
