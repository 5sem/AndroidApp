package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
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

import java.util.List;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.googleApiClient;
import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.startTimer;

public class GeofenceService extends IntentService {
    public static final String TAG = "GeofenceString";

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
                    startTimer.cancel();

                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    startTimer.schedule(new TimerGeofence(),0, 6000);
                    continueGeofenceMonitoring(longitude,latitude,5);


                    //TODO: lav nyt geo fence og reset timer
                }

                Log.d(TAG, "Exiting geofence - " + requestId);
            }
        }
    }


    public void continueGeofenceMonitoring(double longitude, double latitude, float radius) {

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

