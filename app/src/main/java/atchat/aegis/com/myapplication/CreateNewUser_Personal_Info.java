package atchat.aegis.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import application.Message.EmailPasswordPairMessage;
import application.Message.NewUserCreatedMessage;
import application.Users.User;

public class CreateNewUser_Personal_Info extends AppCompatActivity {

    public static final String INTENT_CREATE_USER_EMAIL = "com.aegis.application.CreateNewUser_Personal_Info.Email";
    public static final String INTENT_CREATE_USER_PASSWORD = "com.aegis.application.CreateNewUser_Personal_Info.Password";
    public static final String INTENT_CREATE_USER_ID = "com.aegis.application.CreateNewUser_Personal_Info.ID";

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView aliastTextView;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText aliasEditText;

    private Button createUserButton;

    private String website;
    private String emailAddress;
    private String password;

    private Context context = this;
    private CharSequence text = "Please fill in all personal information fields";
    private int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user__personal__info);

        context = this;

        Intent intent = getIntent();
        emailAddress = intent.getStringExtra(CreateNewUser_Email_And_Password.INTENT_EMAIL);
        password = intent.getStringExtra(CreateNewUser_Email_And_Password.INTENT_PASSWORD);

        website = getString(R.string.localhost);

        firstNameTextView = (TextView) findViewById(R.id.first_name_TextView);
        lastNameTextView = (TextView) findViewById(R.id.last_name_TextView);
        aliastTextView = (TextView) findViewById(R.id.alias_TextView);

        firstNameEditText = (EditText) findViewById(R.id.first_name_EditText);
        lastNameEditText = (EditText) findViewById(R.id.last_name_EditText);
        aliasEditText = (EditText) findViewById(R.id.alias_EditText);

        createUserButton = (Button) findViewById(R.id.create_user_button);

        firstNameTextView.setText("First Name");
        lastNameTextView.setText("Last Name");
        aliastTextView.setText("User name");

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewUserCreatedMessage nucm = new NewUserCreatedMessage();
                nucm.setEmail(emailAddress);
                nucm.setPassword(password);
                if (firstNameEditText.getText().toString().trim().isEmpty() || lastNameEditText.getText().toString().trim().isEmpty() || aliasEditText.getText().toString().trim().isEmpty()) {
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    nucm.setFirstName(firstNameEditText.getText().toString());
                    nucm.setLastName(lastNameEditText.getText().toString());
                    nucm.setAlias(aliasEditText.getText().toString());

                    try {
                        NewUserCreatedMessage answer = new CreateNewUserTask(nucm).execute().get();
                        if (answer.isNewUserCreated()) {
                            Intent goToLoginIntent = new Intent(context, MainActivity.class);
                            goToLoginIntent.putExtra(INTENT_CREATE_USER_EMAIL, emailAddress);
                            goToLoginIntent.putExtra(INTENT_CREATE_USER_PASSWORD, password);
                            goToLoginIntent.putExtra(INTENT_CREATE_USER_ID, "From_CreateNewUser_Person_Info");
                            startActivity(goToLoginIntent);
                        } else {
                            Intent goToLoginIntent = new Intent(context, MainActivity.class);
                            startActivity(goToLoginIntent);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private class CreateNewUserTask extends AsyncTask<Void, Void, NewUserCreatedMessage> {

        private NewUserCreatedMessage nucm;
        public CreateNewUserTask (NewUserCreatedMessage nucm){
            this.nucm = nucm;
        }

        @Override
        protected NewUserCreatedMessage doInBackground(Void... voids) {
            try {
                final String url = website+"user/createUser";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                NewUserCreatedMessage answer = restTemplate.postForObject(url, nucm, NewUserCreatedMessage.class);
                return answer;
            }catch(Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }
    }

}
