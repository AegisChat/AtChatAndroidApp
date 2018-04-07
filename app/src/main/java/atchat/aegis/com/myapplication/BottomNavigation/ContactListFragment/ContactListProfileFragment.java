package atchat.aegis.com.myapplication.BottomNavigation.ContactListFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Iterator;

import application.Tag.Tag;
import application.Users.UserTemplate;
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

        removeFriendButton = (Button) view.findViewById(R.id.removeFriend);
        beginChatButton = (Button) view.findViewById(R.id.startConversation);
        tagsList = (TextView) view.findViewById(R.id.tagslist);
        friendName = (TextView) view.findViewById(R.id.friendName);

        Bundle bundle = getArguments();
        UserTemplate userTemplate = (UserTemplate) bundle.getSerializable(USER_TEMPLATE_ARG);

        friendName.setText(userTemplate.getName());

        StringBuilder stringBuilder = new StringBuilder();

        Iterator<Tag> tagIterator = userTemplate.getTags().iterator();
        while(tagIterator.hasNext()){
            Tag tag = tagIterator.next();
            stringBuilder.append(tag.toString());
            stringBuilder.append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        tagsList.setText(stringBuilder.toString());
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
}
