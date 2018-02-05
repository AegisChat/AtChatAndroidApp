package atchat.aegis.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import application.Users.UserTemplate;


public class UserTemplateFragment extends Fragment {

    private UserTemplate userTemplate;

    private TextView userNameTextView;
    private ListView TagsListView;

    private OnFragmentInteractionListener mListener;

    private ListView tagsListView;
    public UserTemplateFragment() {
        // Required empty public constructor
    }

    public static UserTemplateFragment newInstance(String param1, String param2) {
        UserTemplateFragment fragment = new UserTemplateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_template, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Log.i("UserTemplateFragment", "Fragment Message");
        Bundle bundle = getArguments();
        userTemplate = (UserTemplate) bundle.getSerializable("userTemplate");
        if(userTemplate == null){
            Log.i("UserTemplateFragment", "userTemplate is null");
        }
        if(userTemplate.getName() == null){
            Log.i("UserTemplateFragment", "getName() is null");
        }
        userNameTextView = (TextView) getView().findViewById(R.id.user_name);
        tagsListView = (ListView) getView().findViewById(R.id.tags_ListView);
        userNameTextView.setText(userTemplate.getName());
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
