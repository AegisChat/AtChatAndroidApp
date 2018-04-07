package atchat.aegis.com.myapplication.BottomNavigation.ContactMessageListFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import atchat.aegis.com.myapplication.BottomNavigation.BottomNavigationMenue;
import atchat.aegis.com.myapplication.BottomNavigation.TextMessanger.TextMessengerFragment;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-02-12.
 */

public class ContactMessageListAdapter extends RecyclerView.Adapter {

    private List<ConversationTemplate> mConvsations;
    private Context mContext;
    private String website;
    private User user;


    public ContactMessageListAdapter(Context context, List<ConversationTemplate> conversationList){
        this.mContext = context;
        this.mConvsations = conversationList;
        website = mContext.getString(R.string.localhost);
        user = LoggedInUserContainer.getInstance().getUser();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_message_layout, parent, false);
        return new ContactMessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConversationTemplate conversationTemplate = (ConversationTemplate) mConvsations.get(position);
        ((ContactMessageViewHolder) holder).bind(conversationTemplate);
    }

    @Override
    public int getItemCount() {
        return mConvsations.size();
    }

    private class ContactMessageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView userTemplateName;
        private TextView message;
        private ConversationTemplate conversationTemplate;


        public ContactMessageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.profile_image);
            userTemplateName = (TextView) itemView.findViewById(R.id.contact_profile_message);
            message = (TextView) itemView.findViewById(R.id.contact_messge_message);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Send info to TextMessageFragment
                    List<Tag> tagList = conversationTemplate.getUserTemplate().getTags();
                    StringBuilder stringBuilder =  new StringBuilder();
                    for(Tag tag : tagList){
                        stringBuilder.append(tag.toString());
                        stringBuilder.append(", ");
                    }
                    Fragment fragment = TextMessengerFragment.newInstance(conversationTemplate.getUserTemplateName(), conversationTemplate.getUserTemplate().getId().toString(),stringBuilder.toString(), (double)-1 );
                    FragmentTransaction fragmentTransaction =((BottomNavigationMenue) mContext).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
                }
            });
        }

        public void bind(ConversationTemplate conversationTemplate){
            this.conversationTemplate = conversationTemplate;
            userTemplateName.setText(conversationTemplate.getUserTemplateName());
            message.setText(conversationTemplate.getTextMessage().getContext());
        }
    }
}
