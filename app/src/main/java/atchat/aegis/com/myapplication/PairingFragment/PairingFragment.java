package atchat.aegis.com.myapplication.PairingFragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import application.Message.QueueMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import atchat.aegis.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PairingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PairingFragment extends Fragment {

    private ImageView imageView;
    private OnFragmentInteractionListener mListener;
    private GestureDetectorCompat mDetector;
    private String website;
    private ProgressBar progressBar;
    private TextView waitMessageTextView;
    private Button cancelQueueButton;
    private int state;

    public PairingFragment() {
        // Required empty public constructor
    }

    public static PairingFragment newInstance() {
        PairingFragment fragment = new PairingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.swipeup, container, false);
        imageView = (ImageView) view.findViewById(R.id.arrow_image);
        mDetector = new GestureDetectorCompat(getContext(), new SwipeUpGestureDetector());
        website = getString(R.string.localhost);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        waitMessageTextView = (TextView) view.findViewById(R.id.wait_message_textview);
        cancelQueueButton = (Button) view.findViewById(R.id.cancel_queue_button);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        cancelQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel for queueing
            }
        });
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
                    + " must implement OnContactListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private class SwipeUpGestureDetector implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            if (motionEvent.getY() > motionEvent1.getY()) {
                Log.i("SwipeUpGestureDetector", "SwipeUpDetected");
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                waitMessageTextView.setVisibility(View.VISIBLE);
                cancelQueueButton.setVisibility(View.VISIBLE);
//                new SendReadyToPairMessage().execute();
            }
            return false;
        }
    }

    public class SendReadyToPairMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website + "userActions/startForQueue";
            User user = LoggedInUserContainer.getInstance().getUser();
            QueueMessage queueMessage = new QueueMessage();
            queueMessage.setSender(user.getId());
            queueMessage.setNewLocation(user.getLocation());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, queueMessage, Boolean.class);

            return null;
        }
    }
}
