package atchat.aegis.com.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import application.DatabaseHelpers.TagDatabaseHelper;
import application.DatabaseHelpers.TextMessageDatabaseHelper;
import application.Message.UpdateAliasMessage;
import application.Message.UpdatePairingDistanceMessage;
import application.Message.UpdatePasswordMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;


public class SettingsFragment extends Fragment {

    private static final String FileSettingsName = "LogInFile";

    private onSettingsFragmentInteractionListener mListener;

    private static SeekBar Distance_Slider;
    private static TextView Distance_Text;
    private User user;
    private String website;
    private SharedPreferences settings;
    private EditText changedName;
    private TextView chname;
    private Button applyName;
    private Button changenameButtonClick;
    private Button changePasswordButtonClick;
    private EditText text11;
    private EditText text22;
    private Button logoutButton;
    private TextView passwordPrerequisiteLengthTextView;
    private TextView passwordPrerequisiteNumberTextView;
    private TextView passwordPrerequisiteUpperTextView;
    private TextView passwordPrerequisiteLowerTextView;

    private final int PASSWORD_LENGTH_REQUIREMENT = 6;
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

        settings = getContext().getSharedPreferences(FileSettingsName, Context.MODE_PRIVATE);

       //changedName = (EditText) view.findViewById(R.id.changeAlias);
      //  changedName.setHint("Change Name ("+(String)LoggedInUserContainer.getInstance().getUser().getAlias() + ")");
      // chname = (TextView) view.findViewById(R.id.name1);
      //  applyName = (Button) view.findViewById(R.id.namebutton);
       logoutButton = (Button) view.findViewById(R.id.logoutButton1);

//CHANGE ALIAS BUTTON------------------------------------
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
                        Log.i("Settings Fragment", "Changing name to: " + finaltext);
                        Toast.makeText(getContext(), "Name has been updated!", Toast.LENGTH_SHORT).show();

                        new UpdateAlias(finaltext).execute();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.show();
            }
        });

        //-----------------------------------------------------------
//CHANGE PASSWORD BUTTON -------------------------------
        //-------------------------------------------------------

//        passwordPrerequisiteLengthTextView = (TextView) view.findViewById(R.id.password_not_long_enough);
//        passwordPrerequisiteNumberTextView = (TextView) view.findViewById(R.id.password_needs_number);
//        passwordPrerequisiteUpperTextView = (TextView) view.findViewById(R.id.password_needs_upper_case);
//        passwordPrerequisiteLowerTextView = (TextView) view.findViewById(R.id.password_needs_lower_case);
//
//        passwordPrerequisiteLengthTextView.setText("Must be at least "+ PASSWORD_LENGTH_REQUIREMENT + " characters long");
//        passwordPrerequisiteNumberTextView.setText("Must contain at least one number");
//        passwordPrerequisiteUpperTextView.setText("Must contain at least 1 upper case");
//        passwordPrerequisiteLowerTextView.setText("Must contain at least 1 lower case");


        changePasswordButtonClick = (Button) view.findViewById(R.id.changePasswordButton);
        changePasswordButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                LayoutInflater inflater2 = getActivity().getLayoutInflater();

                View alertdialogView = inflater2.inflate(R.layout.password_dialog, null);

                builder1.setView(alertdialogView);

                final EditText currentUserPass = new EditText(view.getContext());
                currentUserPass.setText((String)LoggedInUserContainer.getInstance().getUser().getPassword());

                final EditText currentPassInput = (EditText)alertdialogView.findViewById(R.id.currentPassword);
                final EditText newPassword1 = (EditText)alertdialogView.findViewById(R.id.password1);
                final EditText newPassword2 = (EditText)alertdialogView.findViewById(R.id.password2);


                builder1.setPositiveButton("Update Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                           if (currentUserPass.getText().toString().equals(currentPassInput.getText().toString())
                                && (newPassword1.getText().toString().equals(newPassword2.getText().toString())))
                        {
                            String newPassword = newPassword1.getText().toString();
                               Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            new UpdatePassword(newPassword).execute();
                        }
//
                       else {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "NO MATCH", Toast.LENGTH_SHORT).show();
                    }
                });

               builder1.show(); }
//            }
  });
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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logout();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("Are you sure you wish to logout?");
                final EditText updatedName = new EditText(view.getContext());

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Settings Fragment", "User confirmed logout");
                        logout();

                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                });
                builder2.show();
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

    private class UpdateAlias extends AsyncTask<Void, Void, Void> {
        // /user/updateAlias
        private String changedAlias;


        private UpdateAlias(String changedAlias){
            this.changedAlias = changedAlias;

        }
        @Override
        protected Void doInBackground(Void... voids) {

            UpdateAliasMessage upa = new UpdateAliasMessage();
            upa.setSender(user.getId());
            upa.setNewAlias(changedAlias);
            final String url = website+"user/updateAlias";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, upa, Boolean.class);
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
            upw.setChangedPassword(changedPassword);
            final String url = website+"user/updatePassword";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, upw, Boolean.class);
            return null;
        }
    }

    public void logout(){
        //proper loggout
        final SharedPreferences.Editor tempEditor = settings.edit();
        tempEditor.putString("loginEmail", null);
        tempEditor.putString("password", null);
        tempEditor.commit();
        new DumpDatabase(getContext()).execute();
        new DumpTextMessageDatabase(getContext()).execute();
        Intent logoutIntent = new Intent(getContext(), MainActivity.class);
        startActivity(logoutIntent);
    }

    private class DumpDatabase extends AsyncTask<Void, Void, Void>{

        private Context context;

        public DumpDatabase(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TagDatabaseHelper tdb = new TagDatabaseHelper(context);
            tdb.dumpTable();
            return null;
        }
    }

    private class DumpTextMessageDatabase extends  AsyncTask<Void, Void, Void>{

        private Context context;

        public DumpTextMessageDatabase(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TextMessageDatabaseHelper tdb = new TextMessageDatabaseHelper(context);
            tdb.dumpTable();
            return null;
        }
    }
}
