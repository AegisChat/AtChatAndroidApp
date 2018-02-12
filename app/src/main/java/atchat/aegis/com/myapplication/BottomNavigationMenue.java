package atchat.aegis.com.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import application.Users.User;
import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.PairingFragment.SwipeUp;
import atchat.aegis.com.myapplication.TagListFragment.TagListFragment;

public class BottomNavigationMenue extends AppCompatActivity implements UserTemplateTestListFragment.OnListFragmentInteractionListener, UserTemplateFragment.OnFragmentInteractionListener
, TextMessangerFragment.OnFragmentInteractionListener, TagListFragment.OnFragmentInteractionListener, SwipeUp.OnFragmentInteractionListener{

    private UserTemplate userTemplate;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    TagListFragment tlf = new TagListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tlf).commit();
                    return true;
                case R.id.navigation_dashboard:
                    UserTemplateTestListFragment t = new UserTemplateTestListFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.contentLayout, t).commit();
                    return true;
                case R.id.navigation_notifications:
//                    TextMessangerFragment tmf = new TextMessangerFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tmf).commit();
                    return true;
                case R.id.navigation_search:

                    return true;
                case R.id.navigation_Email:
                    SwipeUp sw = new SwipeUp();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, sw).commit();
//                    SettingsFragment settingsFragment = new SettingsFragment();
//                    FragmentManager manager = getSupportFragmentManager();
//                    manager.beginTransaction().replace(R.id.contentLayout, settingsFragment, settingsFragment.getTag()).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_menue);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onListFragmentInteraction(UserTemplate userTemplate) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("userTemplate", userTemplate);
        UserTemplateFragment utf = new UserTemplateFragment();
        utf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, utf).commit();
        Log.i("UserTemplate", userTemplate.getName());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
