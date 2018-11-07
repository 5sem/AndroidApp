package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen;

import android.content.Intent;
import android.printservice.PrintService;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Track> trackList = new ArrayList<>();
    private final ArrayList<Track> searchTrackList = new ArrayList<>();
    ProgressBar pgb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        pgb = (ProgressBar) findViewById(R.id.progressbar);
        setSupportActionBar(toolbar);
        setTitle("");
    }

    //inflater meny
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void MainPageMultiSearchClicked(View view) {
        searchTrackList.clear();

        String multiSearch = ((EditText) findViewById(R.id.multiSearchEditText)).getText().toString().toLowerCase();

        //TODO: fix sortering, hopper stadig ind i hillerød ved søgning på rød
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

}
