package atchat.aegis.com.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import atchat.aegis.com.myapplication.dummy.DummyContent;

public class ListFragmentHolder extends FragmentActivity implements ListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_fragment_holder);

        if(findViewById(R.id.fragment_container) != null){

            if(savedInstanceState != null){
                return;
            }

            ListFragment lf = new ListFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, lf).commit();
            System.out.println("test");

        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
