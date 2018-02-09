package atchat.aegis.com.myapplication.TagListFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import atchat.aegis.com.myapplication.R;


/**
 * Created by Avi on 2018-02-04.
 */

public class TagListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_TAG = 0;
    private Context mContext;
    private List<Tag> mTags;

    public TagListAdapter(Context context, List<Tag> tags){
        mContext = context;
        mTags = tags;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_TAG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_button, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tag tag = (Tag) mTags.get(position);
        ((TagViewHolder) holder).bind(tag);
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    private class TagViewHolder extends RecyclerView.ViewHolder{
        private Button tagButton;
        private LoggedInUserContainer userContainer;
        private User user;

        public TagViewHolder(View itemView) {
            super(itemView);
            tagButton = (Button) itemView.findViewById(R.id.tag_button);
            userContainer = LoggedInUserContainer.getInstance();
            user = userContainer.getUser();
        }

        public void bind(final Tag tag){
            tagButton.setText(tag.getTag());
            if(user.hasTag(tag)){
                tagButton.setSelected(true);
            }
            tagButton.setOnClickListener(new View.OnClickListener() {
                private boolean state = tagButton.isSelected();
                @Override
                public void onClick(View view) {
                    state = !state;
                    tagButton.setSelected(state);
                    if(state)
                        user.addTag(tag);
                    else
                        user.removeTag(tag);
                }
            });
        }
    }
}
