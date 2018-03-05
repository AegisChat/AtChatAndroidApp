package application.Users;



import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import application.Message.Message;
import application.Tag.Tag;


public final class User{


	private UUID id;

	private String firstName;
	private String lastName;
	private String alias;
	private String password;
	private String firebaseID;
	private int rating;
	private boolean queueState;
	private int queueNumber;
	private boolean paired;
	private double pairingDistance;
	private ArrayList<Tag> tags;
	private ArrayList<FriendHasSharedInfoPair> friends;
	private ArrayList<UUID> blocked;
	private ArrayList<Message> pendingRequests;
	private ArrayList<Message> sendingRequests;

	private Point location;


	private String emailAddress;


	private long timeStampEpoch;

	public User() {
		id = UUID.randomUUID();
		tags = new ArrayList<Tag>();
		friends = new ArrayList<FriendHasSharedInfoPair>();
		blocked = new ArrayList<UUID>();
		pendingRequests = new ArrayList<Message>();
		sendingRequests = new ArrayList<Message>();
		pairingDistance = 1;
		rating = 10;
	}

	public User(String email, String password, String firstName, String lastName, String alias) {
		this.emailAddress = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.alias = alias;
		id = UUID.randomUUID();
		tags = new ArrayList<Tag>();
		friends = new ArrayList<FriendHasSharedInfoPair>();
		blocked = new ArrayList<UUID>();
		pendingRequests = new ArrayList<Message>();
		sendingRequests = new ArrayList<Message>();
		pairingDistance = 1;
		rating = 10;
	}

	public boolean changeRating(int score) {
		boolean isLegit = false;
		if(score >=  1 && score <= 5) {
			isLegit = true;
			switch (score) {
				case(1):
					rating = rating - 2;
					break;
				case(2):
					rating = rating - 1;
					break;
				case(3):
					break;
				case(4):
					rating = rating + 1;
					break;
				case(5):
					rating = rating + 2;
					break;
			}
		}
		return isLegit;
	}

	public boolean isInBlockedList(UUID uuid) {
		boolean isInBlockedList = false;
		for(UUID uu : blocked) {
			if(uu.compareTo(uuid) == 0) {
				isInBlockedList = true;
				break;
			}
		}
		return isInBlockedList;
	}



	public boolean addFriend(UUID newFriendID) {
		FriendHasSharedInfoPair newFriend = new FriendHasSharedInfoPair();
		newFriend.setFriendID(newFriendID);
		if(hasFriend(newFriendID)){
			return false;
		}else {
			friends.add(newFriend);
			return true;
		}
	}

	public boolean removeFriend(UUID removeFriend) {
		if(!hasFriend(removeFriend)){
			return false;
		}else {
			friends.remove(removeFriend);
			return true;
		}
	}

	public boolean hasFriend(UUID friend) {
		boolean hasFriend = false;
		for(FriendHasSharedInfoPair person: friends) {
			if(person.getFriendID().compareTo(friend) == 0) {
				hasFriend = true;
			}
		}
		return hasFriend;
	}

	public boolean addBlockedUser(UUID newBlockedUser) {
		if(hasBlocked(newBlockedUser)){
			return false;
		}else {
			blocked.add(newBlockedUser);
			return true;
		}
	}

	public boolean removeBlocked(UUID removeBlocked) {
		if(!hasBlocked(removeBlocked)){
			return false;
		}else {
			blocked.remove(removeBlocked);
			return true;
		}
	}

	public boolean hasBlocked(UUID Blocked) {
		if(blocked.contains(Blocked)) {
			return true;
		}else {
			return false;
		}
	}

	public boolean addPendingRequest(Message m) {
		boolean hasPR;
		if(hasPendingRequest(m)) {
			hasPR = false;
		}else {
			pendingRequests.add(m);
			hasPR = true;
		}
		return hasPR;
	}

	public boolean removePendingRequestById(UUID u) {
		boolean hasPR = false;
		Message inMessage;
		if(this.hasPendingRequestById(u)) {
			Iterator<Message> it = pendingRequests.iterator();
			while(it.hasNext()) {
				inMessage = it.next();
				if(inMessage.getId().compareTo(u)== 0) {
					it.remove();
					hasPR = true;
				}
			}
		}
		return hasPR;
	}

	public int getQueueNumber() {
		return queueNumber;
	}

	public void setQueueNumber(int queueNumber) {
		this.queueNumber = queueNumber;
	}

	public boolean removePendingRequest(Message m) {
		boolean hasPR;
		if(hasPendingRequest(m)) {

			hasPR = false;
		}else {
			pendingRequests.remove(m);
			hasPR = true;
		}
		return hasPR;
	}

	public boolean hasPendingRequest(Message m) {
		boolean hasMessage = false;
		for (Message inMessage: pendingRequests) {
			if(inMessage.getId().compareTo(m.getId()) == 0) {
				hasMessage = true;
			}
		}
		return hasMessage;
	}

	public boolean hasPendingRequestById(UUID u) {
		boolean hasPR = false;
		for(Message inMessage: pendingRequests) {
			if(inMessage.getId().compareTo(u)==0) {
				hasPR = true;
			}
		}
		return hasPR;
	}

	public Message getPendingRequest(UUID messageID) {
		Message m = null;
		for (Message inMessage: pendingRequests) {
			if(inMessage.getId().compareTo(m.getId()) == 0) {
				m = inMessage;
			}
		}
		return m;
	}

	public boolean addSendingRequest(Message m) {
		boolean hasSR;
		if(hasSendingRequest(m)) {
			hasSR = false;
		}else {
			sendingRequests.add(m);
			hasSR = true;
		}
		return hasSR;
	}

	public boolean removeSendingRequestById(UUID m) {
		boolean hasMessage = false;
		Message inMessage;
		if(this.hasSendingRequestById(m)) {
			Iterator<Message> it = sendingRequests.iterator();
			while(it.hasNext()) {
				inMessage = it.next();
				if(inMessage.getId().compareTo(m)== 0) {
					it.remove();
					hasMessage= true;
				}
			}
		}
		return hasMessage;
	}

	public boolean removeSendingRequest(Message m) {
		boolean hasSR;
		if(hasSendingRequest(m)) {
			hasSR = false;
		}else {
			sendingRequests.remove(m);
			hasSR = true;
		}
		return hasSR;
	}

	public boolean hasSendingRequestById(UUID m) {
		boolean hasMessage = false;
		for (Message inMessage: sendingRequests) {
			if(inMessage.getId().compareTo(m) == 0) {
				hasMessage = true;
			}
		}
		return hasMessage;
	}

	public boolean hasSendingRequest(Message m) {
		boolean hasMessage = false;
		for (Message inMessage: sendingRequests) {
			if(inMessage.getId().compareTo(m.getId()) == 0) {
				hasMessage = true;
			}
		}
		return hasMessage;
	}

	public Message getSendingRequest(UUID messageID) {
		Message m = null;
		for (Message inMessage: sendingRequests) {
			if(inMessage.getId().compareTo(m.getId()) == 0) {
				m = inMessage;
			}
		}
		return m;
	}

	public boolean hasPendingMessages() {
		return pendingRequests.isEmpty();
	}

	public boolean isQueueState() {
		return queueState;
	}

	public void setQueueState(boolean queueState) {
		if(queueState == true) {
			Instant instant = Instant.now();
			this.timeStampEpoch = instant.toEpochMilli();
		}
		this.queueState = queueState;
	}

	public ArrayList<Message> getPendingRequests() {
		return pendingRequests;
	}

	public void setPendingRequests(ArrayList<Message> pendingRequests) {
		this.pendingRequests = pendingRequests;
	}

	public ArrayList<FriendHasSharedInfoPair> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<FriendHasSharedInfoPair> friends) {
		this.friends = friends;
	}

	public ArrayList<UUID> getBlocked() {
		return blocked;
	}

	public void setBlocked(ArrayList<UUID> blocked) {
		this.blocked = blocked;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean hasTag(Tag newTag) {
		boolean hasTag = false;
		Iterator<Tag> it = tags.iterator();
		while(it.hasNext()) {
			Tag t = it.next();
			if(t.equals(newTag))
				hasTag= true;
		}
		return hasTag;
	}

	public void addTag(List<Tag> newTags) {
		Iterator<Tag> it = newTags.iterator();
		while(it.hasNext()) {
			Tag t = it.next();
			addTag(t);
		}
	}

	public void addTag(Tag newTag) {
		if(!hasTag(newTag))
			tags.add(newTag);
	}

	public void removeTag(Tag tag) {
		if(hasTag(tag))
			tags.remove(indexOfTag(tag));
	}

	public int indexOfTag(Tag tag){
		int counter = 0;
		for(Tag t : tags){
			if(!t.getTag().toUpperCase().replaceAll("\\s+","").equals(tag.getTag().toUpperCase().replaceAll("\\s+",""))){
				counter++;
			}else{
				break;
			}
		}
		return counter;
	}



	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}

	public boolean isPaired() {
		return paired;
	}

	public void setPaired(boolean paired) {
		this.paired = paired;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public double getPairingDistance() {
		return pairingDistance;
	}

	public void setPairingDistance(double pairingDistance) {
		this.pairingDistance = pairingDistance;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public long getTimeStampEpoch() {
		return timeStampEpoch;
	}

	public void setTimeStampEpoch(long timeStampEpoch) {
		this.timeStampEpoch = timeStampEpoch;
	}

	@Override
	public String toString() {
		return getFirstName();
	}

	public String getFirebaseID() {
		return firebaseID;
	}

	public void setFirebaseID(String firebaseID) {
		this.firebaseID = firebaseID;
	}
}
