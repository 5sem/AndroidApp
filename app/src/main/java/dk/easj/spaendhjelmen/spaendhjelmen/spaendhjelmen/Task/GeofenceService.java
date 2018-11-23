package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.GPSSecureSettings;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.googleApiClient;
import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.timerGeofence;

public class GeofenceService extends IntentService {
    public static final String TAG = "GPSSecureActivity";
    private final String FILE_NAME = "GPSSecureSettings.txt";
    private GPSSecureSettings settings;
    private float radius;

    public GeofenceService() {
        super(TAG);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            //TODO Handle error
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofenceList = event.getTriggeringGeofences();
            Geofence geofence = geofenceList.get(0);
            String requestId = geofence.getRequestId();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d(TAG, "Entering geofence - " + requestId);
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {


                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        googleApiClient);
                if (mLastLocation != null) {
                    Log.d(TAG, String.valueOf(mLastLocation.getLatitude()));
                    Log.d(TAG, String.valueOf(mLastLocation.getLongitude()));
                    geofenceList.remove(0);
                    //startTimer.cancel();
                    timerGeofence.cancel();
                    Log.d(TAG, "onHandleIntent: cancel");
                    Log.d(TAG, "onHandleIntent: jeg er stoppet hilsen startimer");
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    timerGeofence.start();
                    Log.d(TAG, "onHandleIntent: kaldt start");
                    continueGeofenceMonitoring(longitude,latitude);


                    //TODO: lav nyt geo fence og reset timer
                }

                Log.d(TAG, "Exiting geofence - " + requestId);
            }
        }
    }


    //region hent fra fil
    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public GPSSecureSettings HentFraFil() {
        String jsonString = ReadContent();

        try {
            JSONObject object = new JSONObject(jsonString);

            String MobileNumber = object.getString("ContactNumber");
            String Distance = object.getString("Distance");
            String Time = object.getString("Time");


            //TODO: lav en god besked med koordinater
            GPSSecureSettings gpsSecureSettings = new GPSSecureSettings(MobileNumber, "message", Distance, Time);

            return gpsSecureSettings;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
    //endregion

    public void continueGeofenceMonitoring(double longitude, double latitude) {

        if (fileExists(this, FILE_NAME)) {
            settings = HentFraFil();
        }

        radius = Float.parseFloat(settings.getDistance());

        Geofence geofence = new Geofence.Builder()
                .setRequestId("GEO")
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(5)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(geofence).build();

        Intent intent = new Intent(this, GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Succesfully added new geofence");
                            //TODO: start timer
                        } else {
                            Log.d(TAG, "Failed to add new geofence + " + status.getStatus());
                        }
                    }
                });


    }



}

