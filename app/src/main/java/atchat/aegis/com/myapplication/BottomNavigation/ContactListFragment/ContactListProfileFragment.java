package atchat.aegis.com.myapplication.BottomNavigation.ContactListFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.UUID;

import application.Message.RemoveFriendMessage;
import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.BottomNavigation.TextMessanger.TextMessengerFragment;
import atchat.aegis.com.myapplication.R;

public class ContactListProfileFragment extends Fragment {

    public static final String USER_TEMPLATE_ARG = "com.aegis.com.myapplication.UserTemplate";

    private OnFragmentInteractionListener mListener;

    private Button removeFriendButton;
    private TextView tags;
    private TextView tagsList;
    private Button beginChatButton;
    private String tagsListToString;
    private TextView friendName;
    private String website;
    private UUID friendID;

    public ContactListProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ContactListProfileFragment newInstance(UserTemplate userTemplate){
        ContactListProfileFragment fragment = new ContactListProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_TEMPLATE_ARG, userTemplate);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_list_profile, container, false);

        website = getString(R.string.localhost);
        removeFriendButton = (Button) view.findViewById(R.id.removeFriend);
        beginChatButton = (Button) view.findViewById(R.id.startConversation);
        tagsList = (TextView) view.findViewById(R.id.tagslist);
        friendName = (TextView) view.findViewById(R.id.friendName);



        Bundle bundle = getArguments();
        final UserTemplate userTemplate = (UserTemplate) bundle.getSerializable(USER_TEMPLATE_ARG);

        friendName.setText(userTemplate.getName());
        final String friendName1 = userTemplate.getName();
        final StringBuilder stringBuilder = new StringBuilder();

        Iterator<Tag> tagIterator = userTemplate.getTags().iterator();
        while(tagIterator.hasNext()){
            Tag tag = tagIterator.next();
            stringBuilder.append(tag.toString());
            stringBuilder.append(", ");
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }

        tagsList.setText(stringBuilder.toString());

        friendID = userTemplate.getId();

        removeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logout();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure you wish to remove " + friendName1 + " as a friend?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Settings Fragment", "User confirmed remove friend");
                        new RemoveFriend(friendID).execute();
                        Fragment fragment = ContactListFragment.newInstance();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                        fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                builder.show();
            }
        });

        //Configure Chat Button
        beginChatButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Fragment fragment = TextMessengerFragment.newInstance(userTemplate.getName(), userTemplate.getId().toString(), stringBuilder.toString(), (double) -1);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
            }
        });
        return view;
    }


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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private class RemoveFriend extends AsyncTask<Void, Void, Void> {

        private UUID id;
        private RemoveFriend(UUID id){
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RemoveFriendMessage rfm = new RemoveFriendMessage();
            rfm.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            rfm.setFriendUuid(id);
            LoggedInUserContainer.getInstance().getUser().removeFriend(id);
            final String url = website+"userActions/removeFriend";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, rfm, Boolean.class);
            return null;
        }
    }
}
