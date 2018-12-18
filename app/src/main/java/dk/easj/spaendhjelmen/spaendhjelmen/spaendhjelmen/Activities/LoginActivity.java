package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    public static boolean Loggedin;

    public static boolean getLoggedin(){ return Loggedin;}
    public static void setLoggedinfalse(){Loggedin = false;}


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
                    Loggedin = true;
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

    public void LoginOpretBruger(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    public void LoginForgotPassword(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Email til nyt password");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.LEFT);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        final EditText msg = new EditText(this);
        // Message Properties
        msg.setGravity(Gravity.LEFT);
        msg.setTextColor(Color.BLACK);
        msg.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alertDialog.setView(msg);

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button

                FirebaseAuth.getInstance().sendPasswordResetEmail(msg.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("LoginActivity", "Email sent.");
                                    Toast.makeText(LoginActivity.this, "Email sendt", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Forkert email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });

        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(getResources().getColor(R.color.colorGreen));
        okBT.setLayoutParams(neutralBtnLP);
        okBT.setText("Send email");

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
        cancelBT.setText("Fortryd");
    }
}
