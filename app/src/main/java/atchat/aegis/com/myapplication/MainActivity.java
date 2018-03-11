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

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import application.Message.EmailPasswordPairMessage;
import application.Message.UpdateFirebaseIDMessage;
import application.Users.LoggedInUserContainer;
import application.Users.User;

public class MainActivity extends AppCompatActivity {
    public static final String FileSettingsName = "LogInFile";
    public static final String INTENT_MESSAGE = "com.aegis.logginTest.intentMessage";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        website = getString(R.string.localhost);

        badLoginTextView = (TextView) findViewById(R.id.wrongLogin);
        emailInput = (EditText) findViewById(R.id.emailText);
        passwordInput = (EditText) findViewById(R.id.passwordText);
        sendButton = (Button) findViewById(R.id.sendButton);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMe);
        Context context = this;
        settings = context.getSharedPreferences(FileSettingsName, Context.MODE_PRIVATE);
        settingsEditor = settings.edit();

        createAccountButton = (Button) findViewById(R.id.createAccount);

        String token = FirebaseInstanceId.getInstance().getToken();

//        Log.d("Main Activity", token);

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

        System.out.println("LoginEmail: " +settings.getString("loginEmail", null));
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
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                user = restTemplate.postForObject(url, loginCred, User.class);
                user.setFirebaseID(FirebaseInstanceId.getInstance().getToken());
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
            super.onPostExecute(user);
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
                Intent intent = new Intent(context, loggedIn.class);
                startActivity(intent);
            }
        }
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
        new HttpRequestTask(loginCred).execute();
    }
}
