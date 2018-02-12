package atchat.aegis.com.myapplication.TagListFragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import application.DatabaseHelpers.TagDatabaseHelper;
import application.Message.UpdateTagMessage;
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
    private String website;
    private TagDatabaseHelper tagDatabaseHelper;

    public TagListAdapter(Context context, List<Tag> tags){
        mContext = context;
        mTags = tags;
        website = mContext.getString(R.string.localhost);
        tagDatabaseHelper = new TagDatabaseHelper(mContext);
    }

    public void setTags(List<Tag> tags){
        mTags = tags;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    private class UpdateUserTagList extends AsyncTask<Void, Void, Void>{

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
                restTemplate.postForObject(url, utm, void.class);
            }catch (Exception e){

            }
            return null;
        }
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
            Log.i("UserName", user.getFirstName());
        }

        public void bind(final Tag tag){
            tagButton.setText(tag.getTag());
            if(user.hasTag(tag)){
                tagButton.setPressed(true);
            }

            tagButton.setOnTouchListener(new View.OnTouchListener() {
                private int CLICK_ACTION_THRESHOLD = 200;
                private float startX;
                private float startY;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = view.getX();
                            startY = view.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endX = view.getX();
                            float endY = view.getY();
                            if (isAClick(startX, startY, endX, endY)) {
                                boolean state = !tagButton.isPressed();
                                tagButton.setPressed(state);
                                if (state) {
                                    user.addTag(tag);

                                } else {
                                    user.removeTag(tag);
                                }
                                new UpdateUserTagList().execute();
                            }
                            break;
                    }
                        return true;
                }

                    private boolean isAClick(float startX, float endX, float startY, float endY) {
                        float differenceX = Math.abs(startX - endX);
                        float differenceY = Math.abs(startY - endY);
                        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
                    }
                });

        }
    }
}
