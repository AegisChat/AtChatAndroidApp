package atchat.aegis.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static SeekBar Distance_Slider;
    private static TextView Distance_Text;



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
}
