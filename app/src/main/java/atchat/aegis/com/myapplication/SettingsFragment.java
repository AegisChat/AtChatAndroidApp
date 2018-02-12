package atchat.aegis.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import application.Message.UpdatePairingDistanceMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;


public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static SeekBar Distance_Slider;
    private static TextView Distance_Text;
    private User user;
    private String website;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        website = getString(R.string.localhost);
        user = LoggedInUserContainer.getInstance().getUser();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        Distance_Slider = (SeekBar) view.findViewById(R.id.distanceSlider);
        Distance_Text = (TextView) view.findViewById(R.id.distanceText);

        Distance_Text.setText("Distance: " + 0.5 + " km");


        Distance_Slider.setMax(20);

        Distance_Slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double DistanceValue = 0.5;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DistanceValue =  Math.min((0.5 + (double)(i)/2) , 10);

                Distance_Text.setText("Distance: " + DistanceValue + " km" );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    new UpdatePairingDistance(DistanceValue).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Distance_Text.setText("Distance: " + DistanceValue + " km");
            }
        });

        return view;
    }



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

    private class UpdatePairingDistance extends AsyncTask<Void, Void, Void> {

        private double distance;

        private UpdatePairingDistance(double distance){
            this.distance = distance;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            UpdatePairingDistanceMessage updm = new UpdatePairingDistanceMessage(distance);
            updm.setSender(user.getId());
            final String url = website+"user/updatePairingDistance";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, updm, User.class);
            }catch (Exception e){

            }
            return null;
        }
    }
}
