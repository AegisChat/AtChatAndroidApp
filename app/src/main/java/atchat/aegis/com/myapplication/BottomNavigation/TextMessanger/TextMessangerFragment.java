package atchat.aegis.com.myapplication.BottomNavigation.TextMessanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.DatabaseHelpers.TextMessageDatabaseHelper;
import application.Message.SentMessage;
import application.Message.TextMessage;
import application.Users.LoggedInUserContainer;
import atchat.aegis.com.myapplication.MessageListAdapter;
import atchat.aegis.com.myapplication.R;


public class TextMessangerFragment extends Fragment {

    public static final String USERNAME_ARGUMENT = "com.aegis.atchat.TextMessageFragment.username";
    public static final String UUID_ARGUMENT = "com.aegis.atchat.TextMessageFragment.UUID";

    private OnFragmentInteractionListener mListener;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button sendButton;
    private TextView conversantsNameTextView;
    private EditText messageInputEditText;
    private List<TextMessage> messageList;
    private BroadcastReceiver broadcastReceiver;
    private String website;
    private UUID conversant;
    private String username;
    private UUID uuid;

    public TextMessangerFragment() {

    }

//    public static TextMessangerFragment newInstance(String param1, String param2) {
//        TextMessangerFragment fragment = new TextMessangerFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static TextMessangerFragment newInstance(String userName, String conversantsUUID){
        TextMessangerFragment fragment = new TextMessangerFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_ARGUMENT, userName);
        args.putString(UUID_ARGUMENT, conversantsUUID);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        website = getString(R.string.localhost);
        View view = inflater.inflate(R.layout.fragment_text_message_list, container, false);

        sendButton = (Button) view.findViewById(R.id.button_chatbox_send);
        messageInputEditText = (EditText) view.findViewById(R.id.edittext_chatbox);

        conversantsNameTextView = (TextView) view.findViewById(R.id.user_template_name);

        Bundle bundle = this.getArguments();
        conversant = UUID.fromString(getArguments().getString(UUID_ARGUMENT));
        username = bundle.getString(USERNAME_ARGUMENT);
        conversantsNameTextView.setText(username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentMessage newMessage = new SentMessage();
                newMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
                newMessage.setRecipient(conversant);
                newMessage.setContext(messageInputEditText.getText().toString());
                newMessage.setTime(System.currentTimeMillis());
                addToMessageList(newMessage);
                updateMessageAdapter(messageList);
                mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
                new Messanger().execute();
                new InputMessageIntoDatabase(newMessage).execute();
                messageInputEditText.setText("");
            }
        });

//        messageList = new ArrayList<TextMessage>();
//        SentMessage sm = new SentMessage();
//        sm.setContext("Hey this is Frost");
//
//        RecievedMessage dm = new RecievedMessage();
//        dm.setContext("Hey this is Mendel");
//
//        SentMessage sm1 = new SentMessage();
//        sm1.setContext("I am sending a message 2 u");
//
//        RecievedMessage dm1 = new RecievedMessage();
//        dm1.setContext("I am replying");
//        messageList.add(sm);
//        messageList.add(dm);
//        messageList.add(sm1);
//        messageList.add(dm1);
        try{
            if(messageList == null){
                messageList = new ArrayList<TextMessage>();
            }
        }catch (NullPointerException e){
            messageList = new ArrayList<TextMessage>();
        }
        mMessageRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(mMessageRecycler.getContext(), messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(i3 < i7){
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            mMessageRecycler.smoothScrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
                        }
                    },100);
                }
            }
        });

        new MessageRetriever().execute();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextMessage textMessage = (TextMessage) intent.getExtras().getSerializable("TextMessage");
            }
        };
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("TestMessage" + conversant.toString()));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addToMessageList(SentMessage message){
        messageList.add(message);
    }

    public void updateMessageAdapter(List messageList){

        mMessageRecycler.setAdapter(null);
        mMessageRecycler.setAdapter(new MessageListAdapter(mMessageRecycler.getContext(), messageList));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class Messanger extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/sendTextMessage";
            TextMessage textMessage = new TextMessage();
            textMessage.setContext(messageInputEditText.getText().toString());
            textMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            textMessage.setRecipient(conversant);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, textMessage, Void.class);
            }catch (Exception e){

            }
            return null;
        }
    }

    private class MessageRetriever extends AsyncTask<Void, Void, ArrayList<TextMessage>>{

        @Override
        protected ArrayList<TextMessage> doInBackground(Void... voids) {
            TextMessageDatabaseHelper db = new TextMessageDatabaseHelper(getContext());
            ArrayList<TextMessage> messages = (ArrayList<TextMessage>) db.getMessagesForUniqueConversation(conversant);
            return messages;
        }

        @Override
        protected void onPostExecute(ArrayList<TextMessage> textMessages) {
            messageList = textMessages;
            updateMessageAdapter(textMessages);
        }
    }

    private class InputMessageIntoDatabase extends AsyncTask<Void, Void, Void>{

        private TextMessage textMessage;
        private List<TextMessage> textMessages;

        public InputMessageIntoDatabase(TextMessage textMessage){
            this.textMessage = textMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TextMessageDatabaseHelper db = new TextMessageDatabaseHelper(getContext());
            db.insertMessageEntry(textMessage);
            textMessages = db.getMessagesForUniqueConversation(conversant);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateMessageAdapter(textMessages);

        }
    }
}
