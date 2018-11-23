package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.GPSSecureSettings;

public class GPSSecureSettingsActivity extends AppCompatActivity {

    private static final String TAG = "GPSSecureSettingsAct";
    private EditText MobileNumber;
    private final String FILE_NAME = "GPSSecureSettings.txt";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpssecureettings);
        setTitle("Indstillinger");
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        final Spinner spinnerDistance = (Spinner) findViewById(R.id.gpssecuresettings_Distance);
        final Spinner spinnerTid = (Spinner) findViewById(R.id.gpssecuresettings_Tid);
        MobileNumber = (EditText) findViewById(R.id.gpssecuresettings_TelefonNummer);

        String[] distance = new String[]{
                "Vælg distance i meter", "10", "15", "20", "25", "50", "100", "150", "200"
        };

        String[] tid = new String[]{
                "Vælg antal minutter", "1", "2", "3", "4", "5", "10", "15", "20", "25", "30", "45", "60"
        };


        final List<String> distanceList = new ArrayList<>(Arrays.asList(distance));
        final ArrayAdapter<String> distanceArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, distanceList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        distanceArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerDistance.setAdapter(distanceArrayAdapter);

        final List<String> tidList = new ArrayList<>(Arrays.asList(tid));
        final ArrayAdapter<String> tidArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, tidList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        tidArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerTid.setAdapter(tidArrayAdapter);

        if (fileExists(this, FILE_NAME)){
            GPSSecureSettings gpsSecureSettings = HentFraFil();
            setSpinText(spinnerDistance, gpsSecureSettings.getDistance());
            setSpinText(spinnerTid, gpsSecureSettings.getTime());
            MobileNumber.setText(gpsSecureSettings.getContactNumber());
        }

    }


    //region File, save read sammenlign


    public void SaveContent(String content) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
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
        Log.d(TAG, "SaveContent: done");
    }

    public String ReadContent() {
        FileInputStream fis = null;
        String Rtn = "";
        try {
            fis = openFileInput(FILE_NAME);
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

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    //TODO: mangler kontrol! du kan gemme null
    public void gpssecuresettingsgemClicked(View view) {

        Spinner spinnerTid = findViewById(R.id.gpssecuresettings_Tid);
        Spinner spinnerDistance = findViewById(R.id.gpssecuresettings_Distance);
        EditText mobileNumber = findViewById(R.id.gpssecuresettings_TelefonNummer);

        String spinnerTidValue = spinnerTid.getSelectedItem().toString();
        String spinnerDistanceValue = spinnerDistance.getSelectedItem().toString();
        String mobileNumberValue = mobileNumber.getText().toString();

        GPSSecureSettings gpsSecureSettings = new GPSSecureSettings();
        gpsSecureSettings.setTime(spinnerTidValue);
        gpsSecureSettings.setDistance(spinnerDistanceValue);
        gpsSecureSettings.setContactNumber(mobileNumberValue);


        Gson gson = new Gson();
        String json = gson.toJson(gpsSecureSettings);

        SaveContent(json);

        Intent intent = new Intent(this, GPSSecureActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Gemt!", Toast.LENGTH_LONG).show();

    }

    public GPSSecureSettings HentFraFil() {
        String jsonString = ReadContent();

        try {
            JSONObject object = new JSONObject(jsonString);

            String MobileNumber = object.getString("ContactNumber");
            String Distance = object.getString("Distance");
            String Time = object.getString("Time");

            GPSSecureSettings gpsSecureSettings = new GPSSecureSettings(MobileNumber, "message", Distance, Time);

            return gpsSecureSettings;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //endregion



    public void setSpinText(Spinner spin, String text)
    {
        for(int i= 0; i < spin.getAdapter().getCount(); i++)
        {
            if(spin.getAdapter().getItem(i).toString().contains(text))
            {
                spin.setSelection(i);
            }
        }

    }
}
