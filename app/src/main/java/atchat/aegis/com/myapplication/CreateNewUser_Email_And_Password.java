package atchat.aegis.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import application.Message.AcceptedEmailAddressMessage;
import application.Message.VerifyLoginMessage;

public class CreateNewUser_Email_And_Password extends AppCompatActivity {

    public static final String INTENT_EMAIL = "com.aegis.application.create_user_email";
    public static final String INTENT_PASSWORD = "com.aegis.application.create_user_password";


    private TextView emailAddressTextView;
    private TextView passwordTextView;
    private TextView passwordPrerequisiteLengthTextView;
    private TextView passwordPrerequisiteNumberTextView;
    private TextView passwordPrerequisiteUpperTextView;
    private TextView passwordPrerequisiteLowerTextView;

    private TextView verifyEmailAddressTextView;

    private EditText emailAddressEditText;
    private EditText passwordEditText;

    private Button nextLoginButton;

//    private EditText emailAddressEditText;
//    private EditText passwordEditText;
//    private EditText firstNameEditText;
//    private EditText lastNameEditText;
//    private EditText aliasEditView;

    private String passwordPrerequisiteString;

    private Context context;
    private final String EMAIL_ADDRESS_STRING = "Email Address";
    private final String PASSWORD_STRING = "Password";
    private final String FIRST_NAME_STRING = "First Name";
    private final String LAST_NAME_STRING = "Last Name";
    private final String ALIAS_STRING = "Username";

    private String website;

    private final int PASSWORD_LENGTH_REQUIREMENT = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user__email__and__password);

        website = getString(R.string.localhost);

        context = this;

        nextLoginButton = (Button) findViewById(R.id.next_login_button);

        verifyEmailAddressTextView = (TextView) findViewById(R.id.verifyEmail);

        emailAddressTextView = (TextView) findViewById(R.id.email_TextView);
        passwordTextView = (TextView) findViewById(R.id.password_TextView);
        passwordPrerequisiteLengthTextView = (TextView) findViewById(R.id.password_not_long_enough);
        passwordPrerequisiteNumberTextView = (TextView) findViewById(R.id.password_needs_number);
        passwordPrerequisiteUpperTextView = (TextView) findViewById(R.id.password_needs_upper_case);
        passwordPrerequisiteLowerTextView = (TextView) findViewById(R.id.password_needs_lower_case);

        emailAddressEditText = (EditText) findViewById(R.id.email_EditText);
        passwordEditText = (EditText) findViewById(R.id.password_EditText);

        passwordTextView.setText("Password");
        emailAddressTextView.setText("Email Address");

        passwordPrerequisiteLengthTextView.setText("Must be at least "+ PASSWORD_LENGTH_REQUIREMENT + " characters long");
        passwordPrerequisiteNumberTextView.setText("Must contain at least one number");
        passwordPrerequisiteUpperTextView.setText("Must contain at least 1 upper case");
        passwordPrerequisiteLowerTextView.setText("Must contain at least 1 lower case");

        emailAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isProperEmail(emailAddressEditText.getText().toString());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!checkPassword(passwordEditText.getText().toString())){
                    passwordTextView.setTextColor(Color.RED);
                }else{
                    passwordTextView.setTextColor(Color.GREEN);
                }
            }
        });

        nextLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AcceptedEmailAddressMessage aeam = null;
                try {
                    aeam = new VerifyEmailTask(emailAddressEditText.getText().toString()).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(aeam.isEmail()){
                    verifyEmailAddressTextView.setText("Email already exists");
                }
                else if((isProperEmail(emailAddressEditText.getText().toString()) == false)){
                    verifyEmailAddressTextView.setText("Please enter a valid email");
                }

                else {
                    verifyEmailAddressTextView.setText("You can make an account");
                    Intent intent = new Intent(context, CreateNewUser_Personal_Info.class);
                    intent.putExtra(INTENT_EMAIL, emailAddressEditText.getText().toString());
                    intent.putExtra(INTENT_PASSWORD, passwordEditText.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }

    private class VerifyEmailTask extends AsyncTask<Void, Void, AcceptedEmailAddressMessage> {

        private String email;
        public VerifyEmailTask (String email){
            this.email = email;
        }

        @Override
        protected AcceptedEmailAddressMessage doInBackground(Void... voids) {
            AcceptedEmailAddressMessage aeam = null;
            try {
                final String url = website+"user/verifyEmail";
                VerifyLoginMessage vlm = new VerifyLoginMessage(email);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                aeam = restTemplate.postForObject(url, vlm, AcceptedEmailAddressMessage.class);
            }catch(Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return aeam;
        }
    }

    private boolean checkPassword(String password){
        boolean isGoodPassword = false;
        boolean hasLength = false;
        boolean hasNumber = false;
        boolean hasUpper = false;
        boolean hasLower = false;
        char passwordChar;

        if(password.length() >= PASSWORD_LENGTH_REQUIREMENT){
            passwordPrerequisiteLengthTextView.setTextColor(Color.GREEN);
            hasLength = true;
        }else{
            passwordPrerequisiteLengthTextView.setTextColor(Color.RED);
            hasLength = false;
        }

        for(int i = 0; i < password.length(); i++){
            passwordChar = password.charAt(i);

            if(Character.isDigit(passwordChar)){
                hasNumber = true;
            }

            if(Character.isUpperCase(passwordChar)){
                hasUpper = true;
            }

            if(Character.isLowerCase(passwordChar)){
                passwordPrerequisiteLowerTextView.setTextColor(Color.GREEN);
                hasLower = true;
            }
        }

        if(hasNumber){
            passwordPrerequisiteNumberTextView.setTextColor(Color.GREEN);
        }else{
            passwordPrerequisiteNumberTextView.setTextColor(Color.RED);
        }

        if(hasUpper){
            passwordPrerequisiteUpperTextView.setTextColor(Color.GREEN);
        }else{
            passwordPrerequisiteUpperTextView.setTextColor(Color.RED);
        }

        if(hasLower){
            passwordPrerequisiteLowerTextView.setTextColor(Color.GREEN);
        }else{
            passwordPrerequisiteLowerTextView.setTextColor(Color.RED);
        }

        if(hasLength && hasNumber && hasUpper && hasLower){
            isGoodPassword = true;
            passwordTextView.setTextColor(Color.GREEN);
        }else{
            isGoodPassword = false;
            passwordTextView.setTextColor(Color.RED);
        }

        return isGoodPassword;
    }

    public boolean isProperEmail(String email){
        boolean hasProperEmailAddress = false;
        if(email.contains("@")) {
            hasProperEmailAddress = true;
            emailAddressTextView.setTextColor(Color.GREEN);
        }

        if(hasProperEmailAddress == false){
            emailAddressTextView.setTextColor(Color.RED);
        }
        return hasProperEmailAddress;
    }
}
