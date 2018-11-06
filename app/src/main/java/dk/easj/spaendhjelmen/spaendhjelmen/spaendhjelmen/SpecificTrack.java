package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class SpecificTrack extends AppCompatActivity {
private Track track;
private final ArrayList<UserComment> commentList = new ArrayList<>();
private TextView specific_track_information, specific_track_parkinginformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_track);

        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra("Track");


        //appbar
        setTitle(track.name);
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        specific_track_information = findViewById(R.id.specific_track_information);
        specific_track_information.setText(track.info);

        specific_track_parkinginformation = findViewById(R.id.specific_track_parkinginformation);
        specific_track_parkinginformation.setText(track.parkInfo);

    }


    public void mainFloatBtnClicked(View view) {
        openDialog();
    }

    public void openDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Opret kommentar");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        final EditText msg = new EditText(this);
        // Message Properties
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button

                String comment = msg.getText().toString();
                int userid = 1; //TODO: admin id, ændre til logged in user id
                int trackid = track.getId();

                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId",userid);
                    jsonObject.put("TrackId", trackid);
                    jsonObject.put("UserComment",comment);
                    String jsonDocument = jsonObject.toString();
                    PostCommentTask task = new PostCommentTask();
                    task.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/comments", jsonDocument);
                    //TODO: skal blive under den specifike track efter man har oprettet sin kommentar
                }
                catch (JSONException ex){
                    Log.d("add",ex.toString());

                }

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
        okBT.setText("Opret");

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
        cancelBT.setText("Fortryd");
    }

    private class PostCommentTask extends AsyncTask<String, Void, CharSequence> {

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
            Toast.makeText(SpecificTrack.this, "Kommentar Oprettet!", Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
            startActivity(getIntent());
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            Toast.makeText(SpecificTrack.this, "Fejl: " + charSequence.toString(), Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }
    }

    private class ReadTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("Id");
                    int trackid = obj.getInt("TrackId");
                    int userid = obj.getInt("UserId");
                    String usercomment = obj.getString("UserComment");

                    String getStringCreated = obj.getString("Created");

                    Calendar created = JsonDateToDate(getStringCreated);
                    String getStringEdited = obj.getString("Edited");

                    Calendar edited = JsonDateToDate(getStringEdited);

                    UserComment userComment = new UserComment(id, trackid, userid,usercomment, created, edited);
                    commentList.add(userComment);
                }
                //ListView mainCommentView = findViewById(R.id.specific_track_commentview);
                //mainCommentView.setAdapter(new CommentAdapter(SpecificTrack.this, commentList));

                ExpandableHeightListView expandableListView = (ExpandableHeightListView) findViewById(R.id.specific_track_commentview);

                expandableListView.setAdapter(new CommentAdapter(SpecificTrack.this, commentList));

                // This actually does the magic
                expandableListView.setExpanded(true);


            } catch (JSONException ex) {
                Log.e("MAINACTIVITY", ex.getMessage());
            }

        }

        @Override
        protected void onCancelled(CharSequence message) {
            Toast.makeText(SpecificTrack.this, "Fejl:" + message.toString(), Toast.LENGTH_SHORT).show();
            Log.e("MAINACTIVITY", message.toString());
        }
    }

    //henter informationer fra rest service
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "onStart: ");
        commentList.clear();
        ReadTask task = new ReadTask();
        task.execute("https://spaendhjelmenrest.azurewebsites.net/service1.svc/comments/" + track.getId());
        Log.d("on start", "onStart: " + track.getId());
    }

    //converterer jsonstring til tid
    public static Calendar JsonDateToDate(String jsonDate) {
        //  "/Date(1321867151710+0100)/"
        int idx1 = jsonDate.indexOf("(");
        int idx2 = jsonDate.indexOf(")") - 5;
        String s = jsonDate.substring(idx1 + 1, idx2);
        long timeInMilliSeconds = Long.valueOf(s);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return calendar;
    }

}
