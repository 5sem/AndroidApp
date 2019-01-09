package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.GPSSecureSettings;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task.GeofenceService;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task.NotiTaskActivity;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.App.channel_Alert;

public class GPSSecureActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManagerCompat;
    private static final int sendSmsPermissionsRequestCode = 0;
    private final String FILE_NAME = "GPSSecureSettings.txt";
    private static final String TAG = "GPSSecureActivity";
    private static final String GEOFENCE_ID = "GEO";
    public static Timer startTimer;
    public static CountDownTimer timerGeofence;
    public static CountDownTimer timersms;
    private Button tænd, sluk;
    private GPSSecureSettings settings;
    private float radius;
    private double latitude;
    private double longitude;
    String stringlongitude;
    String stringlatitude;
    public static GoogleApiClient googleApiClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        setContentView(R.layout.activity_gpssecure);
        setTitle("GPS Secure");
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        tænd = findViewById(R.id.gpsSecureTænd);
        sluk = findViewById(R.id.gpsSecureSluk);
        settings = new GPSSecureSettings();

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

        if (fileExists(this, FILE_NAME)) {
            settings = HentFraFil();
        }

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
        Intent AlertIntent = new Intent(this, NotiTaskActivity.class);
        PendingIntent AlertIntentPending = PendingIntent.getActivity(this,1,AlertIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, channel_Alert)
                .setSmallIcon(R.drawable.ic_warning_yellow_24dp)
                .setContentTitle(title)
                .setContentText(context)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setContentIntent(AlertIntentPending)
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
                            Log.d("GeofenceString", "Location update lat/long " + location.getLatitude() + " " + location.getLongitude());
                        }
                    });
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException - " + e.getMessage());
        }
    }


    private void startGeofenceMonitoring() {
        //TODO: check for sms her! og ikke nede i sms!
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

//TODO: float kan være null pga man kan gemme null
          radius = Float.parseFloat(settings.getDistance()); //settings return null


            //TODO: bliver parameter brugt...
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(GEOFENCE_ID)
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

            if (!googleApiClient.isConnected()) {
                Log.d(TAG, "GoogleApiClient is not connected");
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: husk at se om gps er tændt != tænd gps
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }

                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Succesfully added geofence");
                                    if (tænd.getVisibility() == View.VISIBLE) {
                                        tænd.setVisibility(View.GONE);
                                        sluk.setVisibility(View.VISIBLE);
                                    }
                                    //TODO: kontrol af formel
                                    int test = Integer.parseInt(settings.getTime());
                                    timerGeofence = new CountDownTimer(Integer.parseInt(settings.getTime()) * (1000*60), 1000) {


                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            Log.d(TAG, "onTick: timergeo" + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //TODO: vi bør lave den standard
                                            SendOnChannelAlert("Advarsel!", "Besvar hurtigst muligt!");
                                            //TODO: skrift countdown fra 1 min til X
                                            timersms = new CountDownTimer(6000, 1000) { //venter 1 min
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    Log.d(TAG, "onTick: smstimer" + millisUntilFinished / 1000);
                                                }

                                                @Override
                                                public void onFinish() {
                                                    SendAlertSMS();
                                                    //fyr sms af og noti
                                                    //start timer
                                                }
                                            }.start();

                                        }

                                    }.start();
                                } else {
                                    Log.d(TAG, "Failed to add geofence + " + status.getStatus());
                                }
                            }
                        });
            }
        }
    }

    public int getLocationMode(Context context) {
        try {
            Log.d(TAG, "getLocationMode: " + Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE));
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(null);
    }


    private void stopGeofenceMonitoring() {
        Log.d(TAG, "stopMonitoring called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);
    }

    public void gpsSecureTændClicked(View view) {
        //Start geofencing her
        if (!fileExists(this, FILE_NAME)) {
            Toast.makeText(this, "Indstillinger ikke konfigureret!", Toast.LENGTH_LONG).show();
        }

        //TODO: spørg om appen må bruge sms serivice
        if (!CheckPermissions(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(GPSSecureActivity.this, new String[]{Manifest.permission.SEND_SMS}, sendSmsPermissionsRequestCode);
        }

        if (getLocationMode(this) != 3) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            // Set Custom Title
            TextView title = new TextView(this);
            // Title Properties
            title.setText("Gps indstillinger");
            title.setPadding(10, 10, 10, 10);   // Set Position
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(20);
            alertDialog.setCustomTitle(title);

            // Set Message
            final TextView msg = new TextView(this);
            // Message Properties
            msg.setGravity(Gravity.CENTER_HORIZONTAL);
            msg.setTextColor(Color.BLACK);
            msg.setText("1. Tryk på 'Placerings metode'\n" +
                    "2. Vælg 'Stor nøjagtighed'");
            alertDialog.setView(msg);

            // Set Button
            // you can more buttons
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fortsæt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Perform Action on Button YES btn
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tilbage", new DialogInterface.OnClickListener() {
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
            okBT.setText("Fortsæt");

            final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
            negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
            cancelBT.setTextColor(Color.RED);
            cancelBT.setLayoutParams(negBtnLP);
            cancelBT.setText("Tilbage");

        } else {
            //TODO Geofencing implementeres her
            startGeofenceMonitoring();
            startLocationMonitoring();
        }
    }

    public void gpsSecureIndstillingerClicked(View view) {
        Intent intent = new Intent(this, GPSSecureSettingsActivity.class);
        startActivity(intent);
    }

    //TODO:hent nummer og besked fra gemt fil
    private void SendAlertSMS() {

        if (CheckPermissions(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            //TODO: ændre message i settings, husk at send cords med
            //TODO: contactnumber kan være null!
            stringlongitude = Double.toString(longitude).replaceAll(",", ".");
            stringlatitude = Double.toString(latitude).replaceAll(",", ".");

            String message = settings.getContactMessaage()+ " Følg linket for position: " + java.text.MessageFormat.format("http://maps.google.com/maps?q={0},{1}&ll={0},{1}&z=17", latitude, latitude) +" Hilsen: Spænd hjælmen";
            if (settings.getContactNumber1().length() == 8)
            smsManager.sendTextMessage(settings.getContactNumber1(), null, message, null, null);

            if (settings.getContactNumber2().length() == 8)
                smsManager.sendTextMessage(settings.getContactNumber2(), null, message, null, null);

            if (settings.getContactNumber3().length() == 8)
                smsManager.sendTextMessage(settings.getContactNumber3(), null, message, null, null);
        } else {
            ActivityCompat.requestPermissions(GPSSecureActivity.this, new String[]{Manifest.permission.SEND_SMS}, sendSmsPermissionsRequestCode);

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

            String MobileNumber1 = object.getString("ContactNumber1");
            String MobileNumber2 = object.getString("ContactNumber2");
            String MobileNumber3 = object.getString("ContactNumber3");
            String Distance = object.getString("Distance");
            String Time = object.getString("Time");
            String Message = object.getString("ContactMessaage");


            //TODO: lav en god besked med koordinater
            GPSSecureSettings gpsSecureSettings = new GPSSecureSettings(MobileNumber1,MobileNumber2,MobileNumber3, Message, Distance, Time);

            return gpsSecureSettings;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void gpsSecureSlukClicked(View view) {
        timerGeofence.cancel();
        if (timersms != null)
        {
            timersms.cancel();
        }

        stopGeofenceMonitoring();
        sluk.setVisibility(View.GONE);
        tænd.setVisibility(View.VISIBLE);
        //TODO: remove me
        Toast.makeText(this, "GPS Secure er slukket", Toast.LENGTH_SHORT).show();
    }
}
