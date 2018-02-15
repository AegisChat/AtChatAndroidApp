package atchat.aegis.com.myapplication.ContactMessageListFragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import atchat.aegis.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactMessageListFragment extends Fragment {

    private RecyclerView mRecycler;
    private ContactMessageListAdapter mContactMessageListAdapter;



    public ContactMessageListFragment() {
    }

    public static ContactMessageListFragment newInstance(){
        ContactMessageListFragment fragment = new ContactMessageListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_message_list, container, false);
        
        // Inflate the layout for this fragment
        return view;
    }

    public interface OnContactMessageListFragmentInteractionListener{
        void onContactMessageListFragmentInteractionListener(Uri uri);
    }


}
