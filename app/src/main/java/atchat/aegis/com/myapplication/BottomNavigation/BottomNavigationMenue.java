package atchat.aegis.com.myapplication.BottomNavigation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import application.Message.AcceptFriendRequestMessage;
import application.Message.DenyFriendRequestMessage;
import application.Message.FoundPartnerMessage;
import application.Message.FriendRequestMessage;
import application.Tag.Tag;
import application.Users.LoggedInUserContainer;
import application.Users.Point;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.BottomNavigation.ContactListFragment.ContactListFragment;
import atchat.aegis.com.myapplication.BottomNavigation.ContactMessageListFragment.ContactMessageListFragment;
import atchat.aegis.com.myapplication.BottomNavigation.PairingFragment.PairingFragment;
import atchat.aegis.com.myapplication.BottomNavigation.TagListFragment.TagListFragment;
import atchat.aegis.com.myapplication.BottomNavigation.TextMessanger.TextMessengerFragment;
import atchat.aegis.com.myapplication.R;
import atchat.aegis.com.myapplication.SettingsFragment;
import atchat.aegis.com.myapplication.SettingsFragment.onSettingsFragmentInteractionListener;

public class BottomNavigationMenue extends AppCompatActivity implements
        ContactListFragment.OnContactListFragmentInteractionListener,
        TextMessengerFragment.OnFragmentInteractionListener,
        TagListFragment.OnFragmentInteractionListener,
        PairingFragment.OnFragmentInteractionListener,
        onSettingsFragmentInteractionListener,
        ContactMessageListFragment.OnContactMessageListFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String PAIRING_FRAGMENT_KEY = "PairingState";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private UserTemplate userTemplate;
    private OnFoundPartnerListener onFoundPartnerListener;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver cancelMessageBroadcastReceiver;
    private BroadcastReceiver addFriendBroadcastReciever;
    private AlertDialog.Builder alertDialogBuilder;
    private String website;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = TagListFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    fragment = ContactListFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    if(!LoggedInUserContainer.getInstance().getUser().isPaired())
                        fragment = PairingFragment.newInstance();
                    else{
                        UserTemplate userTemplate = LoggedInUserContainer.getInstance().getUser().getLastPairedPerson();
                        String userName = userTemplate.getName();
                        UUID conversantsUUID = userTemplate.getId();
                        List<Tag> tagList = userTemplate.getTags();
                        StringBuilder stringBuilder =  new StringBuilder();
                        for(Tag tag : tagList){
                            stringBuilder.append(tag.toString());
                            stringBuilder.append(", ");
                        }
                        stringBuilder.deleteCharAt(stringBuilder.length() -1);
                        stringBuilder.deleteCharAt(stringBuilder.length() -2);
                        Bundle bundle = new Bundle();
                        bundle.putString(TextMessengerFragment.USERNAME_ARGUMENT, userName);
                        bundle.putString(TextMessengerFragment.UUID_ARGUMENT, conversantsUUID.toString());
                        fragment = TextMessengerFragment.newInstance(userName, conversantsUUID.toString(), stringBuilder.toString());
                    }
                    break;
                case R.id.navigation_search:
                    fragment = ContactMessageListFragment.newInstance();
                    break;
                case R.id.navigation_Email:
                    fragment = SettingsFragment.newInstance();
                    break;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000)
//                .setFastestInterval(5000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updatePosition(location);
            }
        };

        website = getString(R.string.localhost);

        //----------------------------------------------------------------------------------------------
        //Initilization of variables
        //----------------------------------------------------------------------------------------------
        setContentView(R.layout.activity_bottom_navigation_menue);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_notifications);

        //----------------------------------------------------------------------------------------------
        //Found Partner Message Broadcast Reciever
        //----------------------------------------------------------------------------------------------
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FoundPartnerMessage foundPartnerMessage = (FoundPartnerMessage) intent.getExtras().getSerializable("FoundPartnerMessage");
                startTextMessageFragment(foundPartnerMessage);
            }
        };

        //----------------------------------------------------------------------------------------------
        //Cancel TextMessageFragment Broadcast Reciever
        //----------------------------------------------------------------------------------------------
        cancelMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                endTextMessageFragment();
            }
        };

        addFriendBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BottomNavigationMenue", "Friend broadcast recieved");
                FriendRequestMessage friendRequestMessage = (FriendRequestMessage) intent.getSerializableExtra("FriendRequestMessage");
                friendRequestAlertBox(friendRequestMessage);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient != null && fusedLocationProviderClient != null){
            registerForLocationUpdates();
            Log.i("BottomNavMenu", "onResume: registerForLocationUpdates");
        }else{
            buildGoogleApiClient();
            Log.i("BottomNavMenu", "onResume: BuildGoogleApiClient started");
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("FoundPartnerMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(cancelMessageBroadcastReceiver, new IntentFilter("CancelPairMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(addFriendBroadcastReciever, new IntentFilter("FriendRequestMessage"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelMessageBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addFriendBroadcastReciever);
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        registerForLocationUpdates();
                    }
                }
            }
        }
    }

    private void updatePosition(Location lastLocation) {
        LoggedInUserContainer.getInstance().getUser().setLocation(new Point(lastLocation.getLongitude(), lastLocation.getLatitude()));
        Log.i("BottomNavMenue", "Longitude: " + lastLocation.getLongitude() + " Latitude: "  + lastLocation.getLatitude());
    }

    private void registerForLocationUpdates() {
        Log.i("BottomNavMenu", "RegisterForLocationUpdates");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            Log.i("BottomNavMenu", "Permission granted");
        }else{
            Log.i("BottomNavMenu", "Permission denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void unregisterForLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    public void startTextMessageFragment(final FoundPartnerMessage foundPartnerMessage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserTemplate userTemplate = foundPartnerMessage.getPartner();
                String userName = userTemplate.getName();
                UUID conversantsUUID = userTemplate.getId();
                List<Tag> tagList = userTemplate.getTags();
                StringBuilder stringBuilder =  new StringBuilder();
                for(Tag tag : tagList){
                    stringBuilder.append(tag.toString());
                    stringBuilder.append(", ");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() -1);
                stringBuilder.deleteCharAt(stringBuilder.length() -2);

                Log.i("BottomNavFragment", stringBuilder.toString());
                Bundle bundle = new Bundle();
                bundle.putString(TextMessengerFragment.USERNAME_ARGUMENT, userName);
                bundle.putString(TextMessengerFragment.UUID_ARGUMENT, conversantsUUID.toString());
                bundle.putString(TextMessengerFragment.TAGS_ARGUMENT , stringBuilder.toString());
                Fragment fragment = TextMessengerFragment.newInstance(userName, conversantsUUID.toString(), stringBuilder.toString());
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
            }
        });
    }

    public void endTextMessageFragment(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoggedInUserContainer.getInstance().getUser().setPaired(false);
                Fragment fragment = new PairingFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
            }
        });
    }

    public void friendRequestAlertBox(FriendRequestMessage friendRequestMessage){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(friendRequestMessage.getName() + " want to be your friend");
        alertDialogBuilder.setPositiveButton("Accept", getPositiveButtonOnClickListener(friendRequestMessage));
        alertDialogBuilder.setNegativeButton("Deny", getNegativeButtonOnClickListener(friendRequestMessage));
        alertDialogBuilder.show();
    }

    public DialogInterface.OnClickListener getPositiveButtonOnClickListener(final FriendRequestMessage message){
        final Context context = this;
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AcceptFriendRequest(message).execute();
                String toastMessage = "You are now friends with " + message.getName();
                Toast toast = Toast.makeText(context, toastMessage, toastMessage.length());
                View view = toast.getView();
                view.setBackgroundColor(getResources().getColor(R.color.AtChatYellow));
                toast.show();
            }
        };
        return onClickListener;
    }

    public DialogInterface.OnClickListener getNegativeButtonOnClickListener(final FriendRequestMessage message){
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DenyFriendRequest(message).execute();
            }
        };
        return onClickListener;
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onContactMessageListFragmentInteractionListener(Uri uri) {

    }

    @Override
    public void onContactListFragmentInteractionListener(Uri uri) {

    }


    @Override
    protected void onDestroy() {
        LoggedInUserContainer.getInstance().getUser().setPaired(false);
        SharedPreferences sharedPreferences = getSharedPreferences(PAIRING_FRAGMENT_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(PAIRING_FRAGMENT_KEY, 1);
        sharedPreferencesEditor.commit();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        registerForLocationUpdates();
        Log.i("BottomNavMenu","onConnection");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("BottomNavMenu","onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("BottomNavMenu","onConnectionFailed");
    }

    public class AcceptFriendRequest extends AsyncTask<Void, Void, Void>{

        private FriendRequestMessage friendRequestMessage;

        public AcceptFriendRequest(FriendRequestMessage friendRequestMessage){
            this.friendRequestMessage = friendRequestMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/acceptFriendRequest";
            AcceptFriendRequestMessage acceptFriendRequestMessage = new AcceptFriendRequestMessage();
            acceptFriendRequestMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            acceptFriendRequestMessage.setFriendRequestID(friendRequestMessage.getRecipient());
            acceptFriendRequestMessage.setRecipient(friendRequestMessage.getSender());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, acceptFriendRequestMessage, Boolean.class);
            }catch (Exception e){

            }
            return null;
        }
    }

    public class DenyFriendRequest extends AsyncTask<Void, Void, Void>{

        private FriendRequestMessage friendRequestMessage;

        public DenyFriendRequest(FriendRequestMessage friendRequestMessage){
            this.friendRequestMessage = friendRequestMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"userActions/denyFriendRequest";
            DenyFriendRequestMessage denyFriendRequestMessage = new DenyFriendRequestMessage();
            denyFriendRequestMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            denyFriendRequestMessage.setFriendRequestID(friendRequestMessage.getRecipient());
            denyFriendRequestMessage.setRecipient(friendRequestMessage.getRecipient());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                restTemplate.postForObject(url, denyFriendRequestMessage, Boolean.class);
            }catch (Exception e){

            }
            return null;
        }
    }
}
