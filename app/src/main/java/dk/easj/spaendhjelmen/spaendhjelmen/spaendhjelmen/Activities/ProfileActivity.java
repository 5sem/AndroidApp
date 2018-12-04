package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.TrackAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.ReadHttpTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.User;

public class ProfileActivity extends AppCompatActivity {

    //public static final ArrayList<User> userList = new ArrayList<>();
    private User user;
    private TextView TextViewUserName, TextViewProfiletext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        setTitle(user.Username);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        TextViewUserName = findViewById(R.id.TextViewUserName);
        TextViewUserName.setText(user.Username);



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "onStart: ");



    }





}
