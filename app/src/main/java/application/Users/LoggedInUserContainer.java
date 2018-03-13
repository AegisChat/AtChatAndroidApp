package application.Users;

import java.util.ArrayList;

import atchat.aegis.com.myapplication.ContactMessageListFragment.ConversationTemplate;

/**
 * Created by Avi on 2018-02-04.
 */

public class LoggedInUserContainer {
    private static User user;
    private static LoggedInUserContainer instance = null;
    private ArrayList<UserTemplate> friends;
    private ArrayList<ConversationTemplate> conversationTemplates;

    protected LoggedInUserContainer(){

    }

    public static LoggedInUserContainer getInstance(){
        if(instance == null){
            return new LoggedInUserContainer();
        }else{
            return instance;
        }
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public ArrayList<UserTemplate> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<UserTemplate> friends) {
        this.friends = friends;
    }

    public ArrayList<ConversationTemplate> getConversationTemplates() {
        return conversationTemplates;
    }

    public void setConversationTemplates(ArrayList<ConversationTemplate> conversationTemplates) {
        this.conversationTemplates = conversationTemplates;
    }
}
