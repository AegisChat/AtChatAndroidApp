package atchat.aegis.com.myapplication.BottomNavigation.TextMessanger;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import application.DatabaseHelpers.TextMessageDatabaseHelper;
import application.Message.CancelPairMessage;
import application.Message.FriendRequestMessage;
import application.Message.RecievedMessage;
import application.Message.SentMessage;
import application.Message.TextMessage;
import application.Users.LoggedInUserContainer;
import atchat.aegis.com.myapplication.BottomNavigation.PairingFragment.PairingFragment;
import atchat.aegis.com.myapplication.MessageListAdapter;
import atchat.aegis.com.myapplication.R;


public class TextMessengerFragment extends Fragment {

    public static final String USERNAME_ARGUMENT = "com.aegis.atchat.TextMessageFragment.username";
    public static final String UUID_ARGUMENT = "com.aegis.atchat.TextMessageFragment.UUID";
    public static final String TAGS_ARGUMENT = "com.aegis.atchat.TextMessageFragment.Tags";

    private OnFragmentInteractionListener mListener;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button sendButton;
    private TextView conversantsNameTextView;
    private EditText messageInputEditText;
    private List<TextMessage> messageList;
    private BroadcastReceiver broadcastReceiver;
    private String website;
    private String tagsListToString;
    private UUID conversant;
    private String username;
    private UUID uuid;
    private ImageButton cancelConversationImageButton;
    private ImageButton addFriendImageButton;
    private TextView commonTagsText;
    private GoogleApiClient googleApiClient;

    public TextMessengerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static TextMessengerFragment newInstance(String userName, String conversantsUUID, String tagsListToString){
        TextMessengerFragment fragment = new TextMessengerFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_ARGUMENT, userName);
        args.putString(UUID_ARGUMENT, conversantsUUID);
        args.putString(TAGS_ARGUMENT, tagsListToString);
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
        cancelConversationImageButton = (ImageButton) view.findViewById(R.id.leave_conversation_button);
        addFriendImageButton = (ImageButton)  view.findViewById(R.id.add_friend_button);
        commonTagsText = (TextView) view.findViewById(R.id.commonTags);

        addFriendImageButton.setOnClickListener(addFriendImageButtonOnClickListener());

//        cancelConversationImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
//                builder2.setTitle("Are you sure you wish to leave this conversation?");
//
//                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.i("TextMessageFragment", "User confirmed exit");
//                        try {
//                            new CancelPair().execute().get();
//                            LoggedInUserContainer.getInstance().getUser().setPaired(false);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                        closeTextMessageFragment();
//
//                    }
//                });
//                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder2.show();
//            }
//        });

        cancelConversationImageButton.setOnClickListener(cancelConversationImageButtonOnClickListner());

        Bundle bundle = this.getArguments();
        conversant = UUID.fromString(getArguments().getString(UUID_ARGUMENT));
        username = bundle.getString(USERNAME_ARGUMENT);
        tagsListToString = bundle.getString(TAGS_ARGUMENT);
        conversantsNameTextView.setText(username);

        commonTagsText.setText(tagsListToString);

        sendButton.setOnClickListener(sendButtonOnClickListener());

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
                Log.i("TextMessage", "Broadcast recieved");
//                new MessageRetriever().execute();
                new InputMessageIntoDatabase(textMessage).execute();
            }
        };

        if(LoggedInUserContainer.getInstance().getUser().hasFriend(conversant)){
            addFriendImageButton.setVisibility(View.GONE);
            cancelConversationImageButton.setVisibility(View.GONE);
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("TextMessage" + conversant.toString()));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    private View.OnClickListener addFriendImageButtonOnClickListener(){
        View.OnClickListener viewOnClickListner = new View.OnClickListener() {
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
        };
        return viewOnClickListner;
    }

    private View.OnClickListener cancelConversationImageButtonOnClickListner(){
        final View.OnClickListener viewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("Are you sure you wish to leave this conversation?");

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("TextMessageFragment", "User confirmed exit");
                        try {
                            new CancelPair().execute().get();
                            LoggedInUserContainer.getInstance().getUser().setPaired(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        closeTextMessageFragment();

                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder2.show();
            }
        };
        return  viewOnClickListener;
    }

    private View.OnClickListener sendButtonOnClickListener(){
        View.OnClickListener viewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("Are you sure you wish to leave this conversation?");

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("TextMessageFragment", "User confirmed exit");
                        try {
                            new CancelPair().execute().get();
                            LoggedInUserContainer.getInstance().getUser().setPaired(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        closeTextMessageFragment();

                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder2.show();
            }
        };
        return viewOnClickListener;
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
        mMessageRecycler.scrollToPosition(messageList.size() - 1);
    }

    public void closeTextMessageFragment(){
        Fragment fragment = new PairingFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public TextMessage configureMessage(TextMessage textMessage){
        if(textMessage.getSender().equals(LoggedInUserContainer.getInstance().getUser().getId())){
            SentMessage sentMessage = new SentMessage();
            sentMessage.setId(textMessage.getId());
            sentMessage.setSender(textMessage.getSender());
            sentMessage.setRecipient(textMessage.getRecipient());
            sentMessage.setContext(textMessage.getContext());
            sentMessage.setTime(textMessage.getTime());
            return sentMessage;
        }else {
            RecievedMessage recievedMessage = new RecievedMessage();
            recievedMessage.setId(textMessage.getId());
            recievedMessage.setSender(textMessage.getSender());
            recievedMessage.setRecipient(textMessage.getRecipient());
            recievedMessage.setContext(textMessage.getContext());
            recievedMessage.setTime(textMessage.getTime());
            return recievedMessage;
        }
    }

    private class Messanger extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/sendTextMessage";
            TextMessage textMessage = new TextMessage();
            textMessage.setTime(System.currentTimeMillis());
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

    //----------------------------------------------------------------------------------------------

    private class InputMessageIntoDatabase extends AsyncTask<Void, Void, Void>{

        private TextMessage textMessage;
        private List<TextMessage> textMessages;

        public InputMessageIntoDatabase(TextMessage textMessage){
            this.textMessage = textMessage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            messageList.add(configureMessage(textMessage));
//            updateMessageAdapter(messageList);
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

    //----------------------------------------------------------------------------------------------

    private class CancelPair extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/cancelPair";
            CancelPairMessage cancelPairMessage = new CancelPairMessage();
            cancelPairMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            cancelPairMessage.setRecipient(conversant);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, cancelPairMessage, Boolean.class);
            }catch (Exception e){

            }
            return null;
        }
    }

    private class AddFriend extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/friendRequest";
            FriendRequestMessage friendRequestMessage = new FriendRequestMessage();
            friendRequestMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            friendRequestMessage.setRecipient(conversant);
            friendRequestMessage.setName(LoggedInUserContainer.getInstance().getUser().getAlias());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, friendRequestMessage, Boolean.class);
            }catch (Exception e){

            }
            return null;
        }
    }
}
