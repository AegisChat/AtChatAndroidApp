package atchat.aegis.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import application.Message.EmailPasswordPairMessage;
import application.Message.UpdateFirebaseIDMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;
import atchat.aegis.com.myapplication.BottomNavigation.BottomNavigationMenue;

public class MainActivity extends AppCompatActivity {
    public static final String FileSettingsName = "LogInFile";
    public static final String INTENT_MESSAGE = "com.aegis.logginTest.intentMessage";
    private static final String USER_FILE_NAME = "com.aegis.user.userinfo";
    private SharedPreferences settings;

    private EditText emailInput;
    private EditText passwordInput;
    private Button sendButton;
    private CheckBox rememberMeCheckBox;
    private boolean stateOfRememberMeCheckBox;
    private SharedPreferences.Editor settingsEditor;
    private TextView badLoginTextView;
    private Button createAccountButton;

    private String website;
    private Context context;
    private RestTemplate restTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        website = getString(R.string.localhost);

        restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        badLoginTextView = (TextView) findViewById(R.id.wrongLogin);
        emailInput = (EditText) findViewById(R.id.emailText);
        passwordInput = (EditText) findViewById(R.id.passwordText);
        sendButton = (Button) findViewById(R.id.sendButton);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMe);
        Context context = this;
        settings = context.getSharedPreferences(FileSettingsName, Context.MODE_PRIVATE);
        settingsEditor = settings.edit();

        createAccountButton = (Button) findViewById(R.id.createAccount);

        Intent createdUserIntent = getIntent();
        if(createdUserIntent != null){
            if(createdUserIntent.getStringExtra(CreateNewUser_Personal_Info.INTENT_CREATE_USER_ID) == null) {

            }else if(createdUserIntent.getStringExtra(CreateNewUser_Personal_Info.INTENT_CREATE_USER_ID).equals("From_CreateNewUser_Person_Info")) {
                emailInput.setText(createdUserIntent.getStringExtra(CreateNewUser_Personal_Info.INTENT_CREATE_USER_EMAIL));
                passwordInput.setText(createdUserIntent.getStringExtra(CreateNewUser_Personal_Info.INTENT_CREATE_USER_PASSWORD));
            }
        }

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CreateNewUser_Email_And_Password.class);
                startActivity(intent);
            }

        });

        rememberMeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox) view).isChecked()){
                    stateOfRememberMeCheckBox = true;
                }else{
                    stateOfRememberMeCheckBox = false;
                }
            }
        });

        if(settings.getString("loginEmail", null) != null){

            login(sendButton);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    public void login(View v){
        EmailPasswordPairMessage loginCred = new EmailPasswordPairMessage();
        String email;
        String pass;
        boolean autoLogin = false;
        if (settings.getString("loginEmail", null) != null) {
            email = settings.getString("loginEmail", null);
            pass = settings.getString("password", null);
            loginCred.setEmail(email);
            loginCred.setPassword(pass);
            autoLogin = true;
        }else{
            loginCred.setEmail(emailInput.getText().toString());
            loginCred.setPassword(passwordInput.getText().toString());
        }
//
        new HttpRequestTask(loginCred).execute();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(){
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, User>{
        private User user;
        private EmailPasswordPairMessage loginCred;
        public HttpRequestTask (EmailPasswordPairMessage epp){
            loginCred = epp;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                System.out.println(loginCred.getEmail());
                final String url = website+"user/login";
                user = restTemplate.postForObject(url, loginCred, User.class);
                user.setFirebaseID(FirebaseInstanceId.getInstance().getToken());
//                File file = new File(context.getFilesDir(), USER_FILE_NAME);
//                FileOutputStream fileOutputStream;
//                try{
//                    fileOutputStream = openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE);
//                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//                    objectOutputStream.writeObject(user);
//                    objectOutputStream.close();
//                    fileOutputStream.close();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }

                final String updateFirebaseIDUrl = website + "user/updateFirebaseID";
                UpdateFirebaseIDMessage ufidm = new UpdateFirebaseIDMessage();
                ufidm.setSender(user.getId());
                ufidm.setFirebaseID(user.getFirebaseID());
                try {
                    restTemplate.postForObject(updateFirebaseIDUrl, ufidm, Void.class);
                }catch(Exception e){

                }
                return user;
            }catch(Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if(user == null){
                Toast.makeText(context, "Wrong Email or password", Toast.LENGTH_LONG).show();
            }else {
                if (stateOfRememberMeCheckBox) {
                    settingsEditor.putString("loginEmail", user.getEmailAddress());
                    settingsEditor.putString("password", user.getPassword());
                    settingsEditor.commit();
                }
                Toast.makeText(context, "Welcome " + user.getAlias()+"!" , Toast.LENGTH_LONG).show();
                LoggedInUserContainer userContainer = LoggedInUserContainer.getInstance();
                userContainer.setUser(user);


                Intent intent = new Intent(context, BottomNavigationMenue.class);
                startActivity(intent);
            }
        }
    }

    private class UpdateToken extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final String url = website+"user/updateFirebaseID";
            if(LoggedInUserContainer.getInstance().getUser() != null) {
                UpdateFirebaseIDMessage updateFirebaseIDMessage = new UpdateFirebaseIDMessage();
                updateFirebaseIDMessage.setFirebaseID(FirebaseInstanceId.getInstance().getToken());
                updateFirebaseIDMessage.setSender(LoggedInUserContainer.getInstance().getUser().getId());
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try {
                    restTemplate.postForObject(url, updateFirebaseIDMessage, Void.class);
                } catch (Exception e) {

                }
            }
            return null;
        }
    }
//
//    private class GetUserInfo extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            return null;
//        }
//    }
}
