package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.googleApiClient;

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
                }
                Log.d(TAG, "Exiting geofence - " + requestId);
            }
        }
    }
}

