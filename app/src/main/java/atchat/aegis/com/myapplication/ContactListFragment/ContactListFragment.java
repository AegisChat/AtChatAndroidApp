package atchat.aegis.com.myapplication.ContactListFragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import application.Message.GetFriendsListMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.R;


public class ContactListFragment extends Fragment {

    private RecyclerView mRecycler;
    private ContactListAdapter mContactListAdapter;
    private List<UserTemplate> contactListUserTemplate;
    private OnContactListFragmentInteractionListener mListener;
    private User user;
    private String website;

    public ContactListFragment() {
        // Required empty public constructor
    }

    public static ContactListFragment newInstance(){
        return new ContactListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        user = LoggedInUserContainer.getInstance().getUser();
        website = getString(R.string.localhost);
        mRecycler = (RecyclerView) view.findViewById(R.id.contact_list);
        try {
            contactListUserTemplate = new ArrayList<UserTemplate>();
            contactListUserTemplate = new GetFriendsListUserTemplate().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mContactListAdapter = new ContactListAdapter(getContext(), contactListUserTemplate);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mContactListAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onContactListFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactListFragmentInteractionListener) {
            mListener = (OnContactListFragmentInteractionListener) context;
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

    private class GetFriendsListUserTemplate extends AsyncTask<Void, Void, List<UserTemplate>>{

        private GetFriendsListMessage gfm;

        @Override
        protected List<UserTemplate> doInBackground(Void... voids) {
            final String url = website+"user/getFriendsList";
            User user = LoggedInUserContainer.getInstance().getUser();
            GetFriendsListMessage gfm = new GetFriendsListMessage();
            gfm.setSender(user.getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            GetFriendsListMessage response = restTemplate.postForObject(url, gfm, GetFriendsListMessage.class);
            return response.getFriends();
        }
    }

    public interface OnContactListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onContactListFragmentInteractionListener(Uri uri);
    }


}
