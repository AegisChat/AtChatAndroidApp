package atchat.aegis.com.myapplication.FCMessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import application.Message.GetNewMessagesMessage;
import application.Message.Message;
import application.Message.TextMessage;
import application.Users.LoggedInUserContainer;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-03-02.
 */

public class FCMNotifications extends FirebaseMessagingService {

    private String website;
    private Context context;

    public FCMNotifications(){
        website = getString(R.string.localhost);
        context = getApplicationContext();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FCMNotifications" , "Message has been recieved");
        if(remoteMessage.getData().size() > 0){
            Log.i("FCMNotifications" , "data: " + remoteMessage.getData());
        }
        parseMessages();
    }

    public void parseMessages(){
        try {
            List<Message> messages = (new NewMessageReciever().execute()).get();
            if(messages != null){
                Iterator<Message> iterator = messages.iterator();
                while(iterator.hasNext()){
                    Message message = iterator.next();
                    if(message instanceof TextMessage){
                        TextMessage textMessage = (TextMessage) message;
                        Toast.makeText(context, textMessage.getContext(), Toast.LENGTH_LONG).show();
                        Log.i("Message", textMessage.getContext());
                    }
                }
            }else{

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class NewMessageReciever extends AsyncTask<Void, Void, ArrayList<Message>> {

        @Override
        protected ArrayList<Message> doInBackground(Void... voids) {
            final String url = website+"userActions/getNewMessage";
            GetNewMessagesMessage getNewMessagesMessage = new GetNewMessagesMessage();
            getNewMessagesMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ArrayList<Message> newMessages = restTemplate.postForObject(url, getNewMessagesMessage, ArrayList.class);
            return newMessages;
        }
    }
}
