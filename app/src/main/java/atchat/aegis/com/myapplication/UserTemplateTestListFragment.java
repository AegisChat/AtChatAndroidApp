package atchat.aegis.com.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import application.Message.GetFriendsListMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import application.Users.UserTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserTemplateTestListFragment extends Fragment {
    private String website;
    private LoggedInUserContainer loggedInUserContainer;


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public UserTemplateTestListFragment() {

    }

    @SuppressWarnings("unused")
    public static UserTemplateTestListFragment newInstance(int columnCount) {
        UserTemplateTestListFragment fragment = new UserTemplateTestListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        website = getString(R.string.localhost);
        loggedInUserContainer = LoggedInUserContainer.getInstance();

        User user = loggedInUserContainer.getUser();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usertemplate_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<UserTemplate> friends = new ArrayList<UserTemplate>();
            try {
                friends = new GetUserTemplatesTask().execute().get().getFriends();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            recyclerView.setAdapter(new UserTemplateAdapter(friends, mListener));
        }
        return view;
    }

    public class GetUserTemplatesTask extends AsyncTask<Void, Void, GetFriendsListMessage> {

        private GetFriendsListMessage gfm;

        @Override
        protected GetFriendsListMessage doInBackground(Void... voids) {
            final String url = website+"user/getFriendsList";
            User user = LoggedInUserContainer.getInstance().getUser();
            GetFriendsListMessage gfm = new GetFriendsListMessage();
            gfm.setSender(user.getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            GetFriendsListMessage response = restTemplate.postForObject(url, gfm, GetFriendsListMessage.class);
            return response;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(UserTemplate userTemplate);
    }
}
