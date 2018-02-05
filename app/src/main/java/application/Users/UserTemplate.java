package application.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import application.Tag.Tag;

public final class UserTemplate implements Serializable {

	private String name;
	private UUID id;
	private int rating;
	private ArrayList<Tag> tags;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public ArrayList<Tag> getTags() {
		return tags;
	}
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
}
