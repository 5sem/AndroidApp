package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.GPSSecureSettings;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task.GeofenceService;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.App.channel_Alert;

public class GPSSecureActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManagerCompat;
    private static final int sendSmsPermissionsRequestCode = 0;
    private final String FILE_NAME = "GPSSecureSettings.txt";
    private static final String TAG = "GPSSecureActivity";
    private static final String GEOFENCE_ID = "GEO";

    GoogleApiClient googleApiClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        setContentView(R.layout.activity_gpssecure);
        setTitle("GPS Secure");
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "Connected to GoogleApiClient");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Suspended connection to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Failed to connect to GoogleApiClient - " + connectionResult.getErrorMessage());
                    }
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services not available - show dialog to ask user to download it");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d(TAG, "Google Play Services is available - no actions is required");
        }
    }

    public void SendOnChannelAlert(String title, String context) {
        Notification notification = new NotificationCompat.Builder(this, channel_Alert)
                .setSmallIcon(R.drawable.ic_warning_yellow_24dp)
                .setContentTitle(title)
                .setContentText(context)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    public boolean CheckPermissions(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return check == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();
        googleApiClient.reconnect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onstop called");
        super.onStop();
        googleApiClient.disconnect();
    }

    private void startLocationMonitoring() {
        Log.d(TAG, "startLocation called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(5000)
                    .setFastestInterval(2500)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "Location update lat/long " + location.getLatitude() + " " + location.getLongitude());
                        }
                    });
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException - " + e.getMessage());
        }
    }

    private void startGeofenceMonitoring(double latitude, double longitude, float radius) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(geofence).build();

        Intent intent = new Intent(this, GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "GoogleApiClient is not connected");
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "Succesfully added geofence");
                            } else {
                                Log.d(TAG, "Failed to add geofence + " + status.getStatus());
                            }
                        }
                    });
        }
    }

    private void stopGeofenceMonitoring(){
        Log.d(TAG, "stopMonitoring called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);
    }

    public void gpsSecureTændSlukClicked(View view) {
        //Start geofencing her
        if (!fileExists(this,FILE_NAME)){
            Toast.makeText(this, "Indstillinger ikke konfigureret!", Toast.LENGTH_LONG).show();
        }
        else {
            //TODO Geofencing implementeres her
            //Toast.makeText(this, "Geofencing kommer snart!", Toast.LENGTH_SHORT).show();
            startGeofenceMonitoring(9,9,1000);
            startLocationMonitoring();
        }
    }

    public void gpsSecureIndstillingerClicked(View view) {
        Intent intent = new Intent(this, GPSSecureSettingsActivity.class);
        startActivity(intent);
    }

    private void SendAlertSMS() {
        if (CheckPermissions(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("69",null,"message",null,null);
        }
        else{
            ActivityCompat.requestPermissions(GPSSecureActivity.this,new String[]{Manifest.permission.SEND_SMS},sendSmsPermissionsRequestCode);
        }
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


}
