package atchat.aegis.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import application.Message.RecievedMessage;
import application.Message.SentMessage;
import application.Message.TextMessage;


public class TextMessangerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button sendButton;
    private EditText messageInputEditText;
    private List<TextMessage> messageList;

    public TextMessangerFragment() {

    }

//    public static TextMessangerFragment newInstance(String param1, String param2) {
//        TextMessangerFragment fragment = new TextMessangerFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_message_list, container, false);

        sendButton = (Button) view.findViewById(R.id.button_chatbox_send);
        messageInputEditText = (EditText) view.findViewById(R.id.edittext_chatbox);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentMessage newMessage = new SentMessage();
                newMessage.setContext(messageInputEditText.getText().toString());
                addToMessageList(newMessage);
                updateMessageAdapter(messageList);
                mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);

            }
        });

        messageList = new ArrayList<TextMessage>();
        SentMessage sm = new SentMessage();
        sm.setContext("Hey this is Frost");

        RecievedMessage dm = new RecievedMessage();
        dm.setContext("Hey this is Mendel");

        SentMessage sm1 = new SentMessage();
        sm1.setContext("I am sending a message 2 u");

        RecievedMessage dm1 = new RecievedMessage();
        dm1.setContext("I am replying");
        messageList.add(sm);
        messageList.add(dm);
        messageList.add(sm1);
        messageList.add(dm1);
        mMessageRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(mMessageRecycler.getContext(), messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(i3 < i7){
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            mMessageRecycler.smoothScrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
                        }
                    },100);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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

    public void addToMessageList(SentMessage message){
        messageList.add(message);
    }

    public void updateMessageAdapter(List messageList){

        mMessageRecycler.setAdapter(null);
        mMessageRecycler.setAdapter(new MessageListAdapter(mMessageRecycler.getContext(), messageList));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
