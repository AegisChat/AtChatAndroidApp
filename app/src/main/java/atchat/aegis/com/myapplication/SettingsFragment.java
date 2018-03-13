package atchat.aegis.com.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import application.Message.UpdatePairingDistanceMessage;
import application.Message.UpdatePasswordMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;


public class SettingsFragment extends Fragment {

    private onSettingsFragmentInteractionListener mListener;

    private static SeekBar Distance_Slider;
    private static TextView Distance_Text;
    private User user;
    private String website;
    EditText changedName;
    TextView chname;
    Button applyName;
    Button changenameButtonClick;
    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(){
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
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

       //changedName = (EditText) view.findViewById(R.id.changeAlias);
      //  changedName.setHint("Change Name ("+(String)LoggedInUserContainer.getInstance().getUser().getAlias() + ")");
      // chname = (TextView) view.findViewById(R.id.name1);
      //  applyName = (Button) view.findViewById(R.id.namebutton);

        changenameButtonClick = (Button) view.findViewById(R.id.changeNameButton);


        changenameButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Name");

                final EditText updatedName = new EditText(view.getContext());
                updatedName.setHint((String)LoggedInUserContainer.getInstance().getUser().getAlias());
                builder.setView(updatedName);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String finaltext = updatedName.getText().toString();
                        Toast.makeText(getContext(), "Name has been updated!", Toast.LENGTH_SHORT).show();
//                        try {
//                            new UpdatePassword(finaltext).execute();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
                     //   }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.show();

//                builder.setView()

            }
        });

//        applyName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               changedName.setVisibility(View.VISIBLE);
//              //  String finaltext = changedName.getText().toString();
//               chname.setText(finaltext);
//            }
//        });




//        String name = (String)chname.getText();

        Distance_Slider = (SeekBar) view.findViewById(R.id.distanceSlider);
        Distance_Text = (TextView) view.findViewById(R.id.distanceText);

        Distance_Text.setText("Distance: " + LoggedInUserContainer.getInstance().getUser().getPairingDistance() + " km");


        Distance_Slider.setMax(20);
        Distance_Slider.setProgress((int)LoggedInUserContainer.getInstance().getUser().getPairingDistance());

        Distance_Slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double DistanceValue = LoggedInUserContainer.getInstance().getUser().getPairingDistance();
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DistanceValue =  Math.min((0.5 + (double)(i)/2) , 10);

                Distance_Text.setText("Distance:  " + DistanceValue + " km" );

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
            mListener.onSettingsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onSettingsFragmentInteractionListener) {
            mListener = (onSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement  onSettingsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onSettingsFragmentInteractionListener {
        void onSettingsFragmentInteraction(Uri uri);
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

    private class UpdatePassword extends AsyncTask<Void, Void, Void> {

        private String changedPassword;


        private UpdatePassword(String changedPassword){
        this.changedPassword = changedPassword;

        }
        @Override
        protected Void doInBackground(Void... voids) {

            UpdatePasswordMessage upw = new UpdatePasswordMessage(changedPassword);
            upw.setSender(user.getId());
            final String url = website+"user/updatePassword";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, upw, User.class);
            }catch (Exception e){

            }
            return null;
        }
    }
}
