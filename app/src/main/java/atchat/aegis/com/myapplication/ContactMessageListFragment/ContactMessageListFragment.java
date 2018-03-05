package atchat.aegis.com.myapplication.ContactMessageListFragment;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactMessageListFragment extends Fragment {

    private RecyclerView mRecycler;
    private ContactMessageListAdapter mContactMessageListAdapter;
    private List<UserTemplate> friends;
    private String website;

    public ContactMessageListFragment() {
    }

    public static ContactMessageListFragment newInstance(){
        ContactMessageListFragment fragment = new ContactMessageListFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        website = getString(R.string.localhost);
        View view = inflater.inflate(R.layout.fragment_contact_message_list, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_contact_message_list);
        try {
            friends = new ArrayList<UserTemplate>();
            friends = new GetFriendsListUserTemplate().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(friends.isEmpty()){
            Log.i("ContactMessageListFrag", "is empty");
        }
       mContactMessageListAdapter = new ContactMessageListAdapter(mRecycler.getContext(), friends);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mContactMessageListAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    public interface OnContactMessageListFragmentInteractionListener{
        void onContactMessageListFragmentInteractionListener(Uri uri);
    }

    private class GetFriendsListUserTemplate extends AsyncTask<Void, Void, List<UserTemplate>> {

        @Override
        protected List<UserTemplate> doInBackground(Void... voids) {
            final String url = website+"user/getFriendsList";
            User user = LoggedInUserContainer.getInstance().getUser();
            GetFriendsListMessage gfm = new GetFriendsListMessage();
            gfm.setSender(user.getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            GetFriendsListMessage response = restTemplate.postForObject(url, gfm, GetFriendsListMessage.class);
            List<UserTemplate> friends = response.getFriends();
            return friends;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


}
