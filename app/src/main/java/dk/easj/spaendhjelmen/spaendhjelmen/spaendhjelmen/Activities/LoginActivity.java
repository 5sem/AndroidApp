package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;

    public static final String PREF_FILE_NAME = "loginPref";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    private SharedPreferences preferences;
    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //login
        editTextEmail = (EditText) findViewById(R.id.login_username_edittext);
        editTextPassword = (EditText) findViewById(R.id.login_password_edittext);
        progressBar = (ProgressBar) findViewById(R.id.login_progressbar);

        //firebase authendication
        mAuth = FirebaseAuth.getInstance();

        //store password
        //preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        //usernameField = findViewById(R.id.login_username_edittext);
        //passwordField = findViewById(R.id.login_password_edittext);
        //String username = preferences.getString(USERNAME, null);
        //String password = preferences.getString(PASSWORD, null);
        //if (username != null && password != null) {
        //    usernameField.setText(username);
        //    passwordField.setText(password);
        //}
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            editTextEmail.setError("Indtast Email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Email er ugyldig");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            editTextPassword.setError("Indtast Password");
            editTextPassword.requestFocus();
            return;
        }
        //progressbare visable
        progressBar.setVisibility(View.VISIBLE);

        //Authendicate user and then show the prograsbar and login, else cast exception
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void LoginOnClick(View view) {
        userLogin();

        //Remember password if checkbox is checked.
        //String username = usernameField.getText().toString();
        //String password = passwordField.getText().toString();

        //CheckBox checkBox = findViewById(R.id.login_remember_checkbox);
        //SharedPreferences.Editor editor = preferences.edit();
        //if (checkBox.isChecked()) {
        //    editor.putString(USERNAME, username);
        //    editor.putString(PASSWORD, password);

        //} else {
        //    editor.remove(USERNAME);
        //    editor.remove(PASSWORD);
        //}
        //editor.apply();



    }

    public void MainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void LoginBOpretBruger(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }
}
