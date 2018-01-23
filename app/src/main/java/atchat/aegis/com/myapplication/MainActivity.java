package atchat.aegis.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

import application.Message.EmailPasswordPair;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        badLoginTextView = (TextView) findViewById(R.id.wrongLogin);
        emailInput = (EditText) findViewById(R.id.emailText);
        passwordInput = (EditText) findViewById(R.id.passwordText);
        sendButton = (Button) findViewById(R.id.sendButton);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMe);
        Context context = this;
        settings = context.getSharedPreferences(FileSettingsName, Context.MODE_PRIVATE);
        settingsEditor = settings.edit();

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

//        emailInput.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                emailInput.setText("");
//            }
//        });
//
//        passwordInput.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                passwordInput.setText("");
//            }
//        });
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, User>{

        private EmailPasswordPair loginCred;
        public HttpRequestTask (EmailPasswordPair epp){
            loginCred = epp;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                System.out.println("backgroud activated");
                System.out.println(loginCred.getEmail());
                final String url = "http://10.0.2.2:8080/user/login";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User u = restTemplate.postForObject(url, loginCred, User.class);
                if(!u.getPendingRequests().isEmpty()){
                    System.out.println(u.getPendingRequests().get(0).getClass().getName());
                }
                return u;
            }catch(Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }
    }


    public void login(View v){
        EmailPasswordPair loginCred = new EmailPasswordPair();
        String email;
        String pass;
        boolean autoLogin = false;
        if (settings.getString("loginEmail", null) != null) {
            email = settings.getString("loginEmail", null);
            pass = settings.getString("password", null);
            loginCred.setEmail(email);
            loginCred.setPassword(pass);
            System.out.println("Auto-Login: EmailAddress: "+ email +" Password: "+ pass);
            autoLogin = true;
        }else{
            loginCred.setEmail(emailInput.getText().toString());
            loginCred.setPassword(passwordInput.getText().toString());
        }

        try {
            User user = new HttpRequestTask(loginCred).execute().get();
            if(user != null) {
                if (stateOfRememberMeCheckBox) {
                    settingsEditor.putString("loginEmail", user.getEmailAddress());
                    settingsEditor.putString("password", user.getPassword());
                    settingsEditor.commit();
                }
                Intent intent = new Intent(this, loggedIn.class);
                String firstName = user.getFirstName();
                intent.putExtra(INTENT_MESSAGE, firstName);
                startActivity(intent);
            }else{
                badLoginTextView.setText("The email or password you have entered is wrong, please try again");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
