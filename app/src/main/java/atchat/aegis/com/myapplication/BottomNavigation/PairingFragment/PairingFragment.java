package atchat.aegis.com.myapplication.BottomNavigation.PairingFragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import application.Message.CancelQueueMessage;
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

    private static final String PAIRING_FRAGMENT_KEY = "PairingState";
    private static final int NORMAL_STATE = 1;
    private static final int SWIPED_UP_STATE = 2;

    private ImageView imageView1, imageView2, imageView3;
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
        imageView1 = (ImageView) view.findViewById(R.id.arrow_image1);
        imageView2 = (ImageView) view.findViewById(R.id.arrow_image2);
        imageView3 = (ImageView) view.findViewById(R.id.arrow_image3);
        mDetector = new GestureDetectorCompat(getContext(), new SwipeUpGestureDetector());
        website = getString(R.string.localhost);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        waitMessageTextView = (TextView) view.findViewById(R.id.wait_message_textview);
        cancelQueueButton = (Button) view.findViewById(R.id.cancel_queue_button);
        state = 1;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PAIRING_FRAGMENT_KEY, Context.MODE_PRIVATE);
        state = sharedPreferences.getInt(PAIRING_FRAGMENT_KEY, 0);
        changeState(state);

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
                changeState(NORMAL_STATE);
                LoggedInUserContainer.getInstance().getUser().setQueueState(false);
                new CancelPairingMessager().execute();
                //Cancel for queueing
            }
        });
        return view;
    }

    public void changeState(int state){
        switch(state) {
            case 1:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                waitMessageTextView.setVisibility(View.GONE);
                cancelQueueButton.setVisibility(View.GONE);
                this.state = NORMAL_STATE;
                break;
            case 2:
                imageView1.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                waitMessageTextView.setVisibility(View.VISIBLE);
                cancelQueueButton.setVisibility(View.VISIBLE);
                this.state = SWIPED_UP_STATE;
                break;
            default:
                this.state = NORMAL_STATE;
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                waitMessageTextView.setVisibility(View.GONE);
                cancelQueueButton.setVisibility(View.GONE);
        }

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

    @Override
    public void onPause() {
        super.onPause();
        changeState(NORMAL_STATE);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PAIRING_FRAGMENT_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(PAIRING_FRAGMENT_KEY, state);
        sharedPreferencesEditor.commit();
    }


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
                changeState(2);
                LoggedInUserContainer.getInstance().getUser().setQueueState(true);
                new SendReadyToPairMessager().execute();
            }
            return false;
        }
    }

    public class SendReadyToPairMessager extends AsyncTask<Void, Void, Void> {

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

    public class CancelPairingMessager extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website + "userActions/cancelQueue";
            User user = LoggedInUserContainer.getInstance().getUser();
            CancelQueueMessage cancelQueueMessage = new CancelQueueMessage();
            cancelQueueMessage.setSender(user.getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, cancelQueueMessage, Boolean.class);
            return null;
        }
    }
}
