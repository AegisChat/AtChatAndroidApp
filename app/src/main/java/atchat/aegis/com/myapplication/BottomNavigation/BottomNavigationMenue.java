package atchat.aegis.com.myapplication.BottomNavigation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import application.Message.AcceptFriendRequestMessage;
import application.Message.DenyFriendRequestMessage;
import application.Message.FoundPartnerMessage;
import application.Message.FriendRequestMessage;
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
        ContactMessageListFragment.OnContactMessageListFragmentInteractionListener {

    private static final String PAIRING_FRAGMENT_KEY = "PairingState";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000*5;

    private UserTemplate userTemplate;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnFoundPartnerListener onFoundPartnerListener;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver cancelMessageBroadcastReceiver;
    private BroadcastReceiver addFriendBroadcastReciever;
    private AlertDialog.Builder alertDialogBuilder;
    private String website;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = TagListFragment.newInstance();
//                    fragment = TagListFragment.
//                    TagListFragment tlf = new TagListFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tlf).commit();
//                    return true;
                    break;
                case R.id.navigation_dashboard:
                    fragment = ContactListFragment.newInstance();
//                    fragment = TextMessengerFragment.newInstance();
//                    TextMessengerFragment tmf = new TextMessengerFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tmf).commit();
//                    UserTemplateTestListFragment t = new UserTemplateTestListFragment();
//                    FragmentManager manager = getSupportFragmentManager();
//                    manager.beginTransaction().replace(R.id.contentLayout, t).commit();
//                    return true;
                    break;
                case R.id.navigation_notifications:
                    if(!LoggedInUserContainer.getInstance().getUser().isPaired())
                        fragment = PairingFragment.newInstance();
                    else{
                        UserTemplate userTemplate = LoggedInUserContainer.getInstance().getUser().getLastPairedPerson();
                        String userName = userTemplate.getName();
                        UUID conversantsUUID = userTemplate.getId();
                        Bundle bundle = new Bundle();
                        bundle.putString(TextMessengerFragment.USERNAME_ARGUMENT, userName);
                        bundle.putString(TextMessengerFragment.UUID_ARGUMENT, conversantsUUID.toString());
                        fragment = TextMessengerFragment.newInstance(userName, conversantsUUID.toString());
                    }
//                    PairingFragment sw = new PairingFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, sw).commit();
//                    TextMessengerFragment tmf = new TextMessengerFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tmf).commit();
//                    return true;
                    break;
                case R.id.navigation_search:
                    fragment = ContactMessageListFragment.newInstance();
//                    return true;
                    break;
                case R.id.navigation_Email:
                    fragment = SettingsFragment.newInstance();
//                    SettingsFragment settingsFragment = new SettingsFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, settingsFragment, settingsFragment.getTag()).commit();
//                    return true;
                    break;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.replace(R.id.contentLayout, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("FoundPartnerMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(cancelMessageBroadcastReceiver, new IntentFilter("CancelPairMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(addFriendBroadcastReciever, new IntentFilter("FriendRequestMessage"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelMessageBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addFriendBroadcastReciever);
        unregisterForLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        registerForLocationUpdates();

    }

    @SuppressLint("MissingPermission")
    public void registerForLocationUpdates(){
        FusedLocationProviderClient locationProviderClient = getFusedLocationProviderClient();
        LocationRequest locationRequest = LocationRequest.create();
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Looper looper = Looper.myLooper();
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
    }

    public void unregisterForLocationUpdates(){
        if(fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @NonNull
    public FusedLocationProviderClient getFusedLocationProviderClient(){
        if(fusedLocationProviderClient == null){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        return
                fusedLocationProviderClient;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            updatePosition(lastLocation);
        }
    };

    public void updatePosition(Location location){
        LoggedInUserContainer.getInstance().getUser().setLocation(new Point(location.getLongitude(), location.getLatitude()));
        Log.i("Location:", "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude());
    }

    public void startTextMessageFragment(final FoundPartnerMessage foundPartnerMessage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserTemplate userTemplate = foundPartnerMessage.getPartner();
                String userName = userTemplate.getName();
                UUID conversantsUUID = userTemplate.getId();
                Bundle bundle = new Bundle();
                bundle.putString(TextMessengerFragment.USERNAME_ARGUMENT, userName);
                bundle.putString(TextMessengerFragment.UUID_ARGUMENT, conversantsUUID.toString());
                Fragment fragment = TextMessengerFragment.newInstance(userName, conversantsUUID.toString());
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

//    @Override
//    public void onListFragmentInteraction(UserTemplate userTemplate) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("userTemplate", userTemplate);
//        UserTemplateFragment utf = new UserTemplateFragment();
//        utf.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, utf).commit();
//        Log.i("UserTemplate", userTemplate.getName());
//    }

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
                toast.show();
            }
        };
        return onClickListener;
    }

    public DialogInterface.OnClickListener getNegativeButtonOnClickListener(final FriendRequestMessage message){
        final Context context = this;
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DenyFriendRequest(message).execute();
                String toastMessage = message.getName() + " has denied you friend request";
                Toast toast = Toast.makeText(context, toastMessage, toastMessage.length());
                toast.show();
            }
        };
        return onClickListener;
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
