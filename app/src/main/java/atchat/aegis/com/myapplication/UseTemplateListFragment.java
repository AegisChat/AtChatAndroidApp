package atchat.aegis.com.myapplication;

import android.app.ListFragment;
import android.content.Context;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;

import application.Message.GetFriendsListMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import application.Users.UserTemplate;


public class UseTemplateListFragment extends ListFragment {

    private String website;
    private List<UserTemplate> friends;
    private User user;
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        website = getString(R.string.localhost);
        LoggedInUserContainer loggedInUserContainer = LoggedInUserContainer.getInstance();
        user = loggedInUserContainer.getUser();
        try {
            friends = new GetUserTemplatesTask().execute().get().getFriends();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class GetUserTemplatesTask extends AsyncTask<Void, Void, GetFriendsListMessage>{

        private GetFriendsListMessage gfm;

        @Override
        protected GetFriendsListMessage doInBackground(Void... voids) {
            final String url = website+"user/getFriendsList";

            GetFriendsListMessage gfm = new GetFriendsListMessage();
            gfm.setSender(user.getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            GetFriendsListMessage response = restTemplate.postForObject(url, gfm, GetFriendsListMessage.class);
            return response;
        }
    }
}
