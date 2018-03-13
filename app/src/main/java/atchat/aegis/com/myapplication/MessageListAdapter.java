package atchat.aegis.com.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import application.Message.TextMessage;

/**
 * Created by Avi on 2018-02-07.
 */

public class MessageListAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

    private Context mContext;
    private List<TextMessage> mMessageList;

    public MessageListAdapter(Context context, List<TextMessage> messageList){
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        TextMessage message = (TextMessage) mMessageList.get(position);
        if (message.getMessageType() == 0) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextMessage message = (TextMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageTextView, timeTextView;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.text_message_body);
            timeTextView = (TextView) itemView.findViewById(R.id.text_message_time);
        }


        void bind(TextMessage textMessage){
            messageTextView.setText(textMessage.getContext());
            timeTextView.setText(String.valueOf(textMessage.getTime()));
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageTextView, timeTextView;

        public SentMessageHolder(View itemView){
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.text_message_body);
            timeTextView = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(TextMessage textMessage){
            messageTextView.setText(textMessage.getContext());
            timeTextView.setText(String.valueOf(textMessage.getTime()));
        }
    }
}
