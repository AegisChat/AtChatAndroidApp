package application.Message;

import application.Users.Point;

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
