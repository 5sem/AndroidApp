package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.TrackAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.ReadHttpTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Track> trackList = new ArrayList<>();
    private final ArrayList<Track> searchTrackList = new ArrayList<>();
    private  EditText multiSearchEditText;
    private ImageButton DeleteBtn;
    private final String TAG = "MainActiviy";
    private ProgressBar pgb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        pgb = findViewById(R.id.progressbar);
        multiSearchEditText = findViewById(R.id.multiSearchEditText);
        DeleteBtn = findViewById(R.id.toolbarmain_ImageBtnDelete);
        setSupportActionBar(toolbar);
        setTitle("");

        multiSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View arg0, boolean hasfocus){
                if (hasfocus){
                    Log.d(TAG, "has focus");
                    DeleteBtn.setVisibility(View.VISIBLE);
                }else{
                    Log.d(TAG, "not focus");
                    DeleteBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        multiSearchEditText.clearFocus();
    }

    //henter informationer fra rest service
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "onStart: ");
        trackList.clear();
        ReadTask task = new ReadTask();
        task.execute("https://spaendhjelmenrest.azurewebsites.net/service1.svc/tracks");
    }

    //inflater meny
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //region Multi serach

    public void MainPageMultiSearchClicked(View view) {
        searchTrackList.clear();
        String multiSearch = multiSearchEditText.getText().toString().toLowerCase();

        for (Track t : trackList){
            if (multiSearch.equals("rød") || multiSearch.equals("grøn") || multiSearch.equals("sort") || multiSearch.equals("blå"))
            {
                if (multiSearch.equals("rød")) {

                    if (t.colorCode.contains("red"))
                        searchTrackList.add(t);
                }
                if (multiSearch.equals("sort")) {

                    if (t.colorCode.contains("black"))
                        searchTrackList.add(t);
                }
                if (multiSearch.equals("grøn")) {

                    if (t.colorCode.contains("green"))
                        searchTrackList.add(t);
                }
                if (multiSearch.equals("blå")) {

                    if (t.colorCode.contains("blue"))
                        searchTrackList.add(t);

                }
            }
               else if (t.city.toLowerCase().contains(multiSearch)){
                    searchTrackList.add(t);
            }
            else if(t.name.toLowerCase().contains(multiSearch)){
                searchTrackList.add(t);
            }
            else{

            }
        }
        ListView mainListView = findViewById(R.id.mainListView);

        mainListView.setAdapter(new TrackAdapter(MainActivity.this, searchTrackList));
        pgb.setVisibility(View.GONE);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), SpecificTrack.class);
                Track track = searchTrackList.get(position);
                intent.putExtra("Track", track);
                startActivity(intent);

            }
        });
        multiSearchEditText.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    public void MainPageMultiSearchClearClicked(View view) {

        EditText multisearch = findViewById(R.id.multiSearchEditText);
        multisearch.setText("");
        ListView mainListView = findViewById(R.id.mainListView);

        mainListView.setAdapter(new TrackAdapter(MainActivity.this, trackList));
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), SpecificTrack.class);
                Track track = trackList.get(position);
                intent.putExtra("Track", track);
                startActivity(intent);

            }
        });
    }

    //endregion

    //region ReadTask Class

    private class ReadTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("Id");
                    int pictureid = obj.getInt("PictureId");
                    String name = obj.getString("Name");
                    String info = obj.getString("Info");
                    double longitude = obj.getDouble("Longitude");
                    double latitude = obj.getDouble("Latitude");
                    String address = obj.getString("Address");
                    String colorcode = obj.getString("ColorCode");
                    double length = obj.getDouble("Length");
                    double maxheight = obj.getDouble("MaxHeight");
                    String parkinfo = obj.getString("ParkInfo");
                    String regional = obj.getString("Regional");
                    int postalcode = obj.getInt("PostalCode");
                    String city = obj.getString("City");

                    Track track = new Track(id,pictureid,name,info,longitude, latitude,address,colorcode,length,maxheight,parkinfo,regional,postalcode,city);
                    trackList.add(track);
                }
                ListView mainListView = findViewById(R.id.mainListView);

                mainListView.setAdapter(new TrackAdapter(MainActivity.this, trackList));
                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), SpecificTrack.class);
                        Track track = trackList.get(position);
                        intent.putExtra("Track", track);

                        startActivity(intent);
                    }
                });


            } catch (JSONException ex) {
                Log.e("MAINACTIVITY", ex.getMessage());
            }

        }

        @Override
        protected void onCancelled(CharSequence message) {
            Toast.makeText(MainActivity.this, "Fejl:" + message.toString(), Toast.LENGTH_SHORT).show();
            Log.e("MAINACTIVITY", message.toString());
        }
    }

    //endregion

}
