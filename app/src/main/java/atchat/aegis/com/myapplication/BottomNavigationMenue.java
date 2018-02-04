package atchat.aegis.com.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import application.Users.UserTemplate;

public class BottomNavigationMenue extends AppCompatActivity implements UserTemplateTestListFragment.OnListFragmentInteractionListener{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    UserTemplateTestListFragment t = new UserTemplateTestListFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.contentLayout, t).commit();
                    return true;
                case R.id.navigation_notifications:
                    return true;
                case R.id.navigation_search:
                    return true;
                case R.id.navigation_Email:

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

    }
}
