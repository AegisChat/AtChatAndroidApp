package atchat.aegis.com.myapplication.FCMessaging;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import application.Message.UpdateFirebaseIDMessage;
import application.Users.LoggedInUserContainer;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-03-03.
 */

public class FCMInstanceIdServer extends FirebaseInstanceIdService {

    private static final String TAG = "FCM ID Tag";
    private String website;

    @Override
    public void onTokenRefresh() {
        website = getString(R.string.localhost);
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        LoggedInUserContainer.getInstance().getUser().setFirebaseID(refreshToken);
        Log.i(TAG, refreshToken);
        updateUserFCMToken();
    }

    private void updateUserFCMToken(){
        new UpdateToken().execute();
    }

    private class UpdateToken extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"user/updateFirebaseID";
            UpdateFirebaseIDMessage updateFirebaseIDMessage = new UpdateFirebaseIDMessage();
            updateFirebaseIDMessage.setFirebaseID(FirebaseInstanceId.getInstance().getToken());
            updateFirebaseIDMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject(url, updateFirebaseIDMessage, Void.class);

            return null;
        }
    }
}
