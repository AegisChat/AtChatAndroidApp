package atchat.aegis.com.myapplication.BottomNavigation.ContactMessageListFragment;

import application.Message.TextMessage;
import application.Users.UserTemplate;

/**
 * Created by Avi on 2018-03-12.
 */

public class ConversationTemplate {
    private UserTemplate userTemplate;
    private TextMessage textMessage;

    public ConversationTemplate(UserTemplate userTemplate, TextMessage textMessage){
        this.userTemplate = userTemplate;
        this.textMessage = textMessage;
    }

    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public void setUserTemplate(UserTemplate userTemplate) {
        this.userTemplate = userTemplate;
    }

    public TextMessage getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    public String getUserTemplateName(){
        return userTemplate.getName();
    }
}
