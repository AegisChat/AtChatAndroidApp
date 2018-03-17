package atchat.aegis.com.myapplication.BottomNavigation.TagListFragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.DatabaseHelpers.TagDatabaseHelper;
import application.Message.UpdateTagMessage;
import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import atchat.aegis.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TagListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView mTagRecycler;
    private TagListAdapter mTagAdapter;
    private List<Tag> tagList;
    private EditText searchEditText;
    private Button addTagButton;
    private String website;
    private TagDatabaseHelper tagDatabaseHelper;

    //TODO CONNECT TO DATABASE

    public TagListFragment() {
        // Required empty public constructor
    }

    public static TagListFragment newInstance(){
        return new TagListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Get the view
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        //Set up the RecyclerView
        mTagRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_tag_list);

        //Set up the Tag Database
        tagDatabaseHelper = new TagDatabaseHelper(getContext());
        new GetAllTags().execute();
//        Log.i("DBHelper" , String.valueOf(tagDatabaseHelper.tagExists(new Tag("Math"))));
//        Retrieve all entries from the database

        tagList = new ArrayList<Tag>();

        //Set up the ArrayAdapter
        mTagAdapter = new TagListAdapter(mTagRecycler.getContext(), tagList);

        //Change to GridLayout
        mTagRecycler.setLayoutManager(new GridLayoutManager(mTagRecycler.getContext(), 3));

        //Insert the ArrayAdapter
        mTagRecycler.setAdapter(mTagAdapter);

        searchEditText = (EditText) view.findViewById(R.id.searchview_tagsearch);
        addTagButton = (Button) view.findViewById(R.id.button_add_tag);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<Tag> t = searchForTag(searchEditText.getText().toString());
                updateTagAdapter(t);
            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTextString = "";
                    if(!searchEditText.getText().toString().equals("")){
                        Tag newTag = new Tag(searchEditText.getText().toString());
                        if(!hasTag(newTag)){
                            tagList.add(newTag);
                            new AddToDataBase(newTag).execute();
                        }
                        LoggedInUserContainer.getInstance().getUser().addTag(newTag);
                        new UpdateUserTagList().execute();
                        searchEditText.setText("");
                        updateTagAdapter(tagList);
                    }
            }
        });

        website = getContext().getString(R.string.localhost);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateTagAdapter(List tagList){
        mTagRecycler.setAdapter(null);
        mTagRecycler.setAdapter(new TagListAdapter(mTagRecycler.getContext(), tagList));
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

    public List<Tag> searchForTag(String string){
        List<Tag> tags = new ArrayList<Tag>();
        for(Tag tag : tagList){
            if(tag.getTag().toUpperCase().contains(string.toUpperCase())){
                tags.add(tag);
            }
        }
        return tags;
    }


    public boolean hasTag(Tag tag){
        boolean ans = false;
        Iterator<Tag> tagIterator = tagList.iterator();
        while (tagIterator.hasNext()){
            Tag t = tagIterator.next();
            if(t.equals(tag)){
                ans = true;
            }
        }
        return ans;
    }

    private class UpdateUserTagList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            User user = LoggedInUserContainer.getInstance().getUser();
            final String url = website+"user/updateTags";
            UpdateTagMessage utm = new UpdateTagMessage();
            utm.setSender(user.getId());
            utm.setTags(user.getTags());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, utm, User.class);
            }catch (Exception e){

            }
            return null;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GetAllTags extends AsyncTask<Void, Void, ArrayList<Tag>>{

        @Override
        protected ArrayList<Tag> doInBackground(Void... voids) {
            return tagDatabaseHelper.getAllTags();
        }

        @Override
        protected void onPostExecute(ArrayList<Tag> tags) {
            updateTagAdapter(tags);
        }
    }

    private class AddToDataBase extends AsyncTask<Void, Void, Void>{

        private Tag tag;

        public AddToDataBase(Tag tag){
            this.tag = tag;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tagDatabaseHelper.insertTagEntry(tag);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
