package atchat.aegis.com.myapplication.BottomNavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.UUID;

import application.Message.FoundPartnerMessage;
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
                    fragment = PairingFragment.newInstance();
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
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelMessageBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_menue);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_notifications);

        //Found Partner Message Broadcast Reciever
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FoundPartnerMessage foundPartnerMessage = (FoundPartnerMessage) intent.getExtras().getSerializable("FoundPartnerMessage");
                startTextMessageFragment(foundPartnerMessage);
            }
        };

        cancelMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                endTextMessageFragment();
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
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
        SharedPreferences sharedPreferences = getSharedPreferences(PAIRING_FRAGMENT_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(PAIRING_FRAGMENT_KEY, 1);
        sharedPreferencesEditor.commit();
        super.onDestroy();
    }
}
