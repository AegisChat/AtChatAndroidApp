package atchat.aegis.com.myapplication.BottomNavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

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

    private UserTemplate userTemplate;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnFoundPartnerListener onFoundPartnerListener;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver cancelMessageBroadcastReceiver;
    private BroadcastReceiver addFriendBroadcastReciever;
    private AlertDialog.Builder alertDialogBuilder;
    private String website;

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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                FriendRequestMessage friendRequestMessage = (FriendRequestMessage) intent.getSerializableExtra("FriendRequestMessage");
                alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder.setTitle(friendRequestMessage.getName() + " want to be your friend");
                alertDialogBuilder.setPositiveButton("Accept", getPositiveButtonOnClickListener(friendRequestMessage));
//                alertDialogBuilder.setNegativeButton("Deny", )
            }
        };


        //----------------------------------------------------------------------------------------------
        //Configure Location Listner
        //----------------------------------------------------------------------------------------------
        locationListener = configureLocationListener();

        //----------------------------------------------------------------------------------------------
        //Permissions to grab a users location
        //----------------------------------------------------------------------------------------------
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }else{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LoggedInUserContainer.getInstance().getUser().setLocation(new Point(location.getLongitude(), location.getLatitude()));
            Log.i("Location:", "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude());
            configureButton();
        }
    }

    public LocationListener configureLocationListener(){
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LoggedInUserContainer.getInstance().getUser().setLocation(new Point(location.getLongitude(), location.getLatitude()));
//                Log.i("BottomNavigationMenue", "Latitiude: " + location.getLatitude() +  " Longitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
            }
        };
        return locationListener;
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

    public DialogInterface.OnClickListener getPositiveButtonOnClickListener(final FriendRequestMessage message){
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AcceptFriendRequest(message).execute();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    @SuppressLint("MissingPermission")
    private void configureButton(){
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
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
            acceptFriendRequestMessage.setRecipient(friendRequestMessage.getRecipient());

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
