package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.MainActivity;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.SpecificTrack;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.TrackAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.ReadHttpTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;

import static android.content.Context.MODE_PRIVATE;


public class CheckLists extends ReadHttpTask {

    public final String FILE_NAME = "Liste.txt";
    private Context context;
    ListView mainListView;

    public CheckLists(Context context, ListView mainListView) {
        this.context = context;
        this.mainListView = mainListView;
    }

    @Override
    protected void onPostExecute(CharSequence charSequence) {
       int reader = ReadContent().length();
       int rest = charSequence.length();
       if (rest != reader)
       {
           SaveContent(charSequence.toString());
           //evt lig ind på liste
           MiksAlternative();
       }
    }

    public void SaveContent(String content) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String ReadContent() {
        FileInputStream fis = null;
        String Rtn = "";
        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String content;

            while ((content = br.readLine()) != null) {
                sb.append(content);
            }
            Rtn = sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Rtn;
    }


    private void MiksAlternative() {

        String jsonString = ReadContent();

        try {

            JSONArray array = new JSONArray(jsonString);
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

                Track track = new Track(id, pictureid, name, info, longitude, latitude, address, colorcode, length, maxheight, parkinfo, regional, postalcode, city);
               MainActivity.trackList.add(track);
            }
            mainListView.setAdapter(new TrackAdapter(context, MainActivity.trackList));
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(context, SpecificTrack.class);
                    Track track = MainActivity.trackList.get(position);
                    intent.putExtra("Track", track);

                    context.startActivity(intent);
                }
            });

 

        } catch (JSONException ex) {
            Log.e("MAINACTIVITY", ex.getMessage());
        }

    }

}
