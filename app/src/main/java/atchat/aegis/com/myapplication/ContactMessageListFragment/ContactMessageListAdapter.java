package atchat.aegis.com.myapplication.ContactMessageListFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import application.Users.LoggedInUserContainer;
import application.Users.User;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-02-12.
 */

public class ContactMessageListAdapter extends RecyclerView.Adapter {

    private List<UserTemplate> mFriends;
    private Context mContext;
    private String website;
    private User user;


    public ContactMessageListAdapter(Context context, List<UserTemplate> friends){
        this.mContext = context;
        this.mFriends = friends;
        website = mContext.getString(R.string.localhost);
        user = LoggedInUserContainer.getInstance().getUser();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        return new ContactMessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserTemplate userTemplate = (UserTemplate) mFriends.get(position);
        ((ContactMessageViewHolder) holder).bind(userTemplate);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    private class ContactMessageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView userTemplateName;
        private TextView message;
        private String name;
        private String latestTestMessage;


        public ContactMessageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.profile_image);
            userTemplateName = (TextView) itemView.findViewById(R.id.contact_profile_message);
            message = (TextView) itemView.findViewById(R.id.contact_messge_message);
        }

        public void bind(UserTemplate userTemplate){
            name = userTemplate.getName();


        }


    }
}
