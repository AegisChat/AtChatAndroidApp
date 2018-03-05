package atchat.aegis.com.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.ContactListFragment.ContactListFragment;
import atchat.aegis.com.myapplication.ContactMessageListFragment.ContactMessageListFragment;
import atchat.aegis.com.myapplication.PairingFragment.SwipeUp;
import atchat.aegis.com.myapplication.SettingsFragment.onSettingsFragmentInteractionListener;
import atchat.aegis.com.myapplication.TagListFragment.TagListFragment;

public class BottomNavigationMenue extends AppCompatActivity implements
        ContactListFragment.OnContactListFragmentInteractionListener,
        TextMessangerFragment.OnFragmentInteractionListener,
        TagListFragment.OnFragmentInteractionListener,
        SwipeUp.OnFragmentInteractionListener,
        onSettingsFragmentInteractionListener,
        ContactMessageListFragment.OnContactMessageListFragmentInteractionListener{

    private UserTemplate userTemplate;

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
                    fragment = TextMessangerFragment.newInstance();
//                    TextMessangerFragment tmf = new TextMessangerFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, tmf).commit();
//                    UserTemplateTestListFragment t = new UserTemplateTestListFragment();
//                    FragmentManager manager = getSupportFragmentManager();
//                    manager.beginTransaction().replace(R.id.contentLayout, t).commit();
//                    return true;
                    break;
                case R.id.navigation_notifications:
                    fragment = SwipeUp.newInstance();
//                    SwipeUp sw = new SwipeUp();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, sw).commit();
//                    TextMessangerFragment tmf = new TextMessangerFragment();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_menue);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_notifications);
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
}
