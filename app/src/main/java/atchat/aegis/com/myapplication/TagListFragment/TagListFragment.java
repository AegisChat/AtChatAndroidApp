package atchat.aegis.com.myapplication.TagListFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
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

    public TagListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);

        mTagRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_tag_list);
        tagList = new ArrayList<Tag>();
        setUpTags();
        mTagAdapter = new TagListAdapter(mTagRecycler.getContext(), tagList);
        mTagRecycler.setLayoutManager(new GridLayoutManager(mTagRecycler.getContext(), 3));
        mTagRecycler.setAdapter(mTagAdapter);

        return view;
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUpTags(){
        tagList.add(new Tag("Movies"));
        tagList.add(new Tag("Food"));
        tagList.add(new Tag("Technology"));
        tagList.add(new Tag("Video Games"));
        tagList.add(new Tag("Computers"));
        tagList.add(new Tag("Studying"));
        tagList.add(new Tag("Cars"));
        tagList.add(new Tag("Basketball"));
        tagList.add(new Tag("Baseball"));
        tagList.add(new Tag("Hockey"));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
