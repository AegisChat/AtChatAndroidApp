package atchat.aegis.com.myapplication.FCMessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import application.DatabaseHelpers.TextMessageDatabaseHelper;
import application.Message.AcceptFriendRequestMessage;
import application.Message.CancelPairMessage;
import application.Message.FoundPartnerMessage;
import application.Message.FriendRequestMessage;
import application.Message.GetNewMessagesMessage;
import application.Message.Message;
import application.Message.MessageInterface;
import application.Message.TextMessage;
import application.Users.LoggedInUserContainer;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-03-02.
 */

public class FCMNotifications extends FirebaseMessagingService {

    private String website;
    private TextMessageDatabaseHelper db;

    public FCMNotifications(){
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        website = getString(R.string.localhost);

        Log.i("FCMNotifications" , "Message has been recieved");
        if(remoteMessage.getData().size() > 0){
            Log.i("FCMNotifications" , "data: " + remoteMessage.getData());
        }
        try{
            LoggedInUserContainer.getInstance().getUser().getAlias();
            parseMessages();
        }catch (NullPointerException e){
        }
    }

    public void parseMessages(){
        try {
            List<Message> messages = (new NewMessageReciever().execute()).get();
            if(messages != null){
                Iterator<Message> iterator = messages.iterator();
                while(iterator.hasNext()){
                    MessageInterface message = iterator.next();
                    if(message instanceof TextMessage){
                        //----------------------------------------------------------------------------------------------
                        //Upon TextMessage Recieved
                        //----------------------------------------------------------------------------------------------
                        TextMessage textMessage =  (TextMessage) message;
                        db = new TextMessageDatabaseHelper(this);
                        db.insertMessageEntry(textMessage);
                        Intent intent = new Intent("TextMessage" + textMessage.getSender());
                        intent.putExtra("TextMessage", textMessage);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }else if(message instanceof FoundPartnerMessage){
                        //----------------------------------------------------------------------------------------------
                        //Upon FoundPartnerMessage Recieved
                        //----------------------------------------------------------------------------------------------
                        Log.i("FoundPartnerMessage" , "New Partner Found");
                        FoundPartnerMessage foundPartnerMessage = (FoundPartnerMessage) message;
                        Intent intent = new Intent("FoundPartnerMessage");
                        intent.putExtra("FoundPartnerMessage", foundPartnerMessage);
                        LoggedInUserContainer.getInstance().getUser().setQueueState(false);
                        LoggedInUserContainer.getInstance().getUser().setPaired(true);
                        LoggedInUserContainer.getInstance().getUser().setLastPairedPerson(foundPartnerMessage.getPartner());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }else if(message instanceof CancelPairMessage){
                        //----------------------------------------------------------------------------------------------
                        //Upon FriendRequest Messaqe Recieved
                        //----------------------------------------------------------------------------------------------
                        CancelPairMessage cancelPairMessage = (CancelPairMessage) message;
                        Intent intent = new Intent("CancelPairMessage");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }else if(message instanceof FriendRequestMessage){
                        Log.i("FriendRequestMessage" , "New Friend Request");
                        FriendRequestMessage friendRequestMessage = (FriendRequestMessage) message;
                        Intent intent = new Intent("FriendRequestMessage");
                        intent.putExtra("FriendRequestMessage", friendRequestMessage);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    } else if(message instanceof AcceptFriendRequestMessage){
                        Intent intent = new Intent("AcceptedFriendRequestMessage");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }
                }
            }else{

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private class NewMessageReciever extends AsyncTask<Void, Void, ArrayList<Message>> {

        @Override
        protected ArrayList<Message> doInBackground(Void... voids) {
            final String url = website+"userActions/getNewMessages";
            GetNewMessagesMessage getNewMessagesMessage = new GetNewMessagesMessage();
            getNewMessagesMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ArrayList<Message> newMessages = new ArrayList<Message>();
            try {
                GetNewMessagesMessage result = restTemplate.postForObject(url, getNewMessagesMessage, GetNewMessagesMessage.class);
                newMessages = result.getMessages();
            }catch(NullPointerException e){
                Log.i("FCMNotifications", "No Messages");
            }catch(Exception e){

            }
            return newMessages;
        }
    }
}
