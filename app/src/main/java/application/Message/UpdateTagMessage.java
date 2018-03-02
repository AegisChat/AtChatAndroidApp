package application.Message;

import java.util.ArrayList;

import application.Tag.Tag;

public class UpdateTagMessage extends Message{

	private ArrayList<Tag> tags;

	public UpdateTagMessage(){

	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
}
