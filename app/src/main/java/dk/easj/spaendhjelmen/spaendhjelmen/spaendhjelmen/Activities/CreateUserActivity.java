package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class CreateUserActivity extends AppCompatActivity {

    private EditText Username, Email, Password;
    private Button Opret;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);

        mAuth = FirebaseAuth.getInstance();

        Username = findViewById(R.id.createuser_username_edittext);
        Email = findViewById(R.id.createuser_email_edittext);
        Password = findViewById(R.id.createuser_password_edittext);
        Opret = findViewById(R.id.createuser_createuser);


        Opret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: fortæl bruger error - ved focusloss eller opret
                if (Username == null || Username.getText().toString().length() < 3) return;
                if (Password == null || Password.getText().toString().length() <= 6) return;
                if (Email == null || !Email.getText().toString().contains("@")) return;

                FireBaseCreateUser(Email.getText().toString(), Password.getText().toString());
                //TODO: kald task serivce her
            }
        });

    }

private void FireBaseCreateUser (String email, String password){

    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        setUser(mAuth.getCurrentUser()); //deleteme
                        Log.d("Firebase", "onComplete: UserCreated");
                        Log.d("Firebase", "onComplete: " + mAuth.getUid());

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(CreateUserActivity.this, "Fejl under oprettelse tjek internet forbindelsen og prøv iogen", Toast.LENGTH_LONG).show();
                    }
                }
            });
}



    private void setUser(FirebaseUser user) {
        this.user = user;
    }
}
