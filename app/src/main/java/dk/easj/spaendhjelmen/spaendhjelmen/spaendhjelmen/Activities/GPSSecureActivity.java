package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.app.Notification;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.App.channel_Alert;

public class GPSSecureActivity extends AppCompatActivity {

private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        setContentView(R.layout.activity_gpssecure);

    }

    public void SendOnChannelAlert(String title, String context){
        Notification notification = new NotificationCompat.Builder(this, channel_Alert)
                .setSmallIcon(R.drawable.ic_warning_yellow_24dp)
                .setContentTitle(title)
                .setContentText(context)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .build();

        notificationManagerCompat.notify(1,notification);
    }

    public void gpsSecureTændSlukClicked(View view) {
        SendOnChannelAlert("TestTitle", "TestContext");
    }

    public void gpsSecureIndstillingerClicked(View view) {
    }



}
