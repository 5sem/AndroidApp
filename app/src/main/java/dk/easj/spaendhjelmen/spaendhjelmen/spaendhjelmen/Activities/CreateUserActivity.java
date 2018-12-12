package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
                //if (mAuth.getCurrentUser().getUid() != null)
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
                        PostUserToDb(Username.getText().toString(), mAuth.getCurrentUser().getUid(), "Ikke udfyldt");
                        Log.d("Firebase", "onComplete: UserCreated");
                        Log.d("Firebase", "onComplete: " + mAuth.getUid());

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(CreateUserActivity.this, "Fejl under oprettelse tjek internet forbindelsen og prøv igen", Toast.LENGTH_LONG).show();
                    }
                }
            });
}





    private void setUser(FirebaseUser user) {
        this.user = user;
    }


private void PostUserToDb(String username, String authtoken, String dec){

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("AuthToken",authtoken);
        jsonObject.put("UserName", username);
        jsonObject.put("Description",dec);
        String jsonDocument = jsonObject.toString();
        PostUserTask task = new PostUserTask();
        task.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/users", jsonDocument);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //TODO: logge direkte ind
        Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show();

    }
    catch (JSONException ex){
        Log.d("add",ex.toString());

    }


}


    private class PostUserTask extends AsyncTask<String, Void, CharSequence> {

        @Override
        protected CharSequence doInBackground(String... params) {
            String urlString = params[0];
            String jsonDocument = params[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(jsonDocument);
                osw.flush();
                osw.close();
                int responseCode = connection.getResponseCode();
                if (responseCode / 100 != 2) {
                    String responseMessage = connection.getResponseMessage();
                    throw new IOException("HTTP response code: " + responseCode + " " + responseMessage);
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                return line;
            } catch (MalformedURLException ex) {
                cancel(true);
                String message = ex.getMessage() + " " + urlString;
                Log.e("POSTEXECUTE", message);
                return message;
            } catch (IOException ex) {
                cancel(true);
                Log.e("POSTEXECUTE", ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            Toast.makeText(CreateUserActivity.this, "Fejl: " + charSequence.toString(), Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }
    }






}
