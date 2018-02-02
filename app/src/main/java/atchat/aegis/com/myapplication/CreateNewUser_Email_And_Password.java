package atchat.aegis.com.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CreateNewUser_Email_And_Password extends AppCompatActivity {

    private TextView emailAddressTextView;
    private TextView passwordTextView;
    private TextView passwordPrerequisiteLengthTextView;
    private TextView passwordPrerequisiteNumberTextView;
    private TextView passwordPrerequisiteUpperTextView;
    private TextView passwordPrerequisiteLowerTextView;

    private EditText emailAddressEditText;
    private EditText passwordEditText;

//    private EditText emailAddressEditText;
//    private EditText passwordEditText;
//    private EditText firstNameEditText;
//    private EditText lastNameEditText;
//    private EditText aliasEditView;

    private String passwordPrerequisiteString;

    private final String EMAIL_ADDRESS_STRING = "Email Address";
    private final String PASSWORD_STRING = "Password";
    private final String FIRST_NAME_STRING = "First Name";
    private final String LAST_NAME_STRING = "Last Name";
    private final String ALIAS_STRING = "Username";

    private final int PASSWORD_LENGTH_REQUIREMENT = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user__email__and__password);

        emailAddressTextView = (TextView) findViewById(R.id.email_TextView);
        passwordTextView = (TextView) findViewById(R.id.password_TextView);
        passwordPrerequisiteLengthTextView = (TextView) findViewById(R.id.password_not_long_enough);
        passwordPrerequisiteNumberTextView = (TextView) findViewById(R.id.password_needs_number);
        passwordPrerequisiteUpperTextView = (TextView) findViewById(R.id.password_needs_upper_case);
        passwordPrerequisiteLowerTextView = (TextView) findViewById(R.id.password_needs_lower_case);

        emailAddressEditText = (EditText) findViewById(R.id.email_EditText);
        passwordEditText = (EditText) findViewById(R.id.password_EditText);

        passwordTextView.setText("Password");

        passwordPrerequisiteLengthTextView.setText("Must be at least "+ PASSWORD_LENGTH_REQUIREMENT + " characters long");
        passwordPrerequisiteNumberTextView.setText("Must contain at least one number");
        passwordPrerequisiteUpperTextView.setText("Must contain at least 1 upper case");
        passwordPrerequisiteLowerTextView.setText("Must contain at least 1 lower case");

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



}
