package atchat.aegis.com.myapplication.BottomNavigation.ContactMessageListFragment;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.DatabaseHelpers.TextMessageDatabaseHelper;
import application.Message.GetConversationListMessage;
import application.Message.TextMessage;
import application.Users.LoggedInUserContainer;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactMessageListFragment extends Fragment {

    private RecyclerView mRecycler;
    private ContactMessageListAdapter mContactMessageListAdapter;
    private List<ConversationTemplate> conversants;
    private String website;
    private Button addConversationButton;

    public ContactMessageListFragment() {
    }

    public static ContactMessageListFragment newInstance(){
        ContactMessageListFragment fragment = new ContactMessageListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        //TEMP
//        TextMessage textMessage = new TextMessage();
//        textMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
//        textMessage.setRecipient(UUID.fromString("8091a4cd-e968-4b41-be8b-30703a526e8d"));
//        textMessage.setContext("This is the most recent message");
//        textMessage.setTime(System.currentTimeMillis());
//        final TextMessageDatabaseHelper textMessageDatabaseHelper1 = new TextMessageDatabaseHelper(getContext());
//        textMessageDatabaseHelper1.insertMessageEntry(textMessage);
//        LoggedInUserContainer.getInstance().getUser().addToConversationList(UUID.fromString("8091a4cd-e968-4b41-be8b-30703a526e8d"));

        //add all friends to conversation list

        website = getString(R.string.localhost);
        View view = inflater.inflate(R.layout.fragment_contact_message_list, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_contact_message_list);

        conversants = new ArrayList<ConversationTemplate>();

        mContactMessageListAdapter = new ContactMessageListAdapter(mRecycler.getContext(), conversants);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mContactMessageListAdapter);

        addConversationButton = (Button) view.findViewById(R.id.add_conversation_button);
        addConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ContactMessageList", "Add Conversation Button has been hit");
            }
        });
        new GetConversationListReceiver().execute();
        return view;
    }

    public interface OnContactMessageListFragmentInteractionListener{
        void onContactMessageListFragmentInteractionListener(Uri uri);
    }

    public void updateConversationListNames(List userTemplates){
       mRecycler.setAdapter(null);
       mRecycler.setAdapter(new ContactMessageListAdapter(mRecycler.getContext(), userTemplates));
    }

    private class GetConversationListReceiver extends AsyncTask<Void, Void, ArrayList<ConversationTemplate>> {
        @Override
        protected ArrayList<ConversationTemplate> doInBackground(Void... voids) {
            final String url = website+"user/getConversationList";
            final TextMessageDatabaseHelper textMessageDatabaseHelper = new TextMessageDatabaseHelper(getContext());
            ArrayList<ConversationTemplate> conversationTemplates = new ArrayList<ConversationTemplate>();
            GetConversationListMessage getConversationListMessage = new GetConversationListMessage();
            getConversationListMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            getConversationListMessage.setConversants(LoggedInUserContainer.getInstance().getUser().getConversations());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                GetConversationListMessage result = restTemplate.postForObject(url, getConversationListMessage, GetConversationListMessage.class);
                ArrayList<UserTemplate> newMessages;
                if (result != null) {
                    newMessages = (ArrayList<UserTemplate>) result.getConversations();
                } else {
                    newMessages = new ArrayList<UserTemplate>();
                }
                conversationTemplates = new ArrayList<ConversationTemplate>();
                Iterator<UserTemplate> userTemplateIterator = newMessages.iterator();
                while(userTemplateIterator.hasNext()){
                    UserTemplate userTemplate = userTemplateIterator.next();
                    Log.i("ConversationReceiver", "User name: " + userTemplate.getName());
                    Log.i("ConversationReceiver", "User id: " + userTemplate.getId());
                    TextMessage textMessage = textMessageDatabaseHelper.getMostRecentMessage(userTemplate.getId());
                    conversationTemplates.add(new ConversationTemplate(userTemplate, textMessage));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return conversationTemplates;
        }

        @Override
        protected void onPostExecute(ArrayList<ConversationTemplate> conversationTemplates) {
            updateConversationListNames(conversationTemplates);
        }
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(){
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }


}
