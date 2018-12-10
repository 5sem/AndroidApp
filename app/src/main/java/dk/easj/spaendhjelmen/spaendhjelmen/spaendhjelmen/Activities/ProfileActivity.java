package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.TrackAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.ReadHttpTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.User;

public class ProfileActivity extends AppCompatActivity {

    //public static final ArrayList<User> userList = new ArrayList<>();
    private User user;
    private EditText profile_profiletext_edittext;
    private Switch profilePrivacySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        setTitle(user.Username);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        profile_profiletext_edittext = findViewById(R.id.profile_profiletext_edittext);
        profile_profiletext_edittext.setText(user.Description);

        profilePrivacySwitch = findViewById(R.id.profilePrivacySwitch);
        profilePrivacySwitch.setChecked(user.Privacy);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void profileBtnSave(View view) {
        String newDescription;
        Boolean isPrivate;

        profile_profiletext_edittext = findViewById(R.id.profile_profiletext_edittext);
        newDescription = profile_profiletext_edittext.getText().toString();


        profilePrivacySwitch = findViewById(R.id.profilePrivacySwitch);


        if (profilePrivacySwitch.isChecked()){
            isPrivate = true;
        }
        else{
            isPrivate = false;
        }

        try{
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Description", newDescription);
            jsonObject.put("Privacy", isPrivate);

            UpdateProfileTask task = new UpdateProfileTask();
            task.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/users/"+user.getId(), jsonObject.toString());


        }
        catch (JSONException ex){
            Log.d("add",ex.toString());

        }
    }





    private class UpdateProfileTask extends AsyncTask<String, Void, CharSequence> {

        @Override
        protected CharSequence doInBackground(String... params) {
            String urlString = params[0];
            String jsonDocument = params[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
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
                Log.e("UPDATEEXECUTE", message);
                return message;
            } catch (IOException ex) {
                cancel(true);
                Log.e("UPDATEEXECUTE", ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            Toast.makeText(ProfileActivity.this, "Profil opdateret!", Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            Toast.makeText(ProfileActivity.this, "Fejl: " + charSequence.toString(), Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }
    }











}
