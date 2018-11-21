package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceService extends IntentService {
    public static final String TAG = "GeofenceString";

    public GeofenceService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()){
            //TODO Handle error
        }
        else{
            int transition = event.getGeofenceTransition();
            List<Geofence> geofenceList = event.getTriggeringGeofences();
            Geofence geofence = geofenceList.get(0);
            String requestId = geofence.getRequestId();

                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    Log.d(TAG, "Entering geofence - " + requestId);
                }
                else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                    Log.d(TAG, "Exiting geofence - " + requestId); }
                }
        }
    }

