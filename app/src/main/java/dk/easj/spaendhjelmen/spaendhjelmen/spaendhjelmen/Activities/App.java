package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String channel_Alert = "Channel Alert";


    @Override
    public void onCreate() {
        super.onCreate();
        CreateNotificationChannel();
    }

    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channel_Alert, "Alert", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("SpændHjelmen GPSSecure notifikation");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }



}
