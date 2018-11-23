package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.App.channel_Alert;
import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.GPSSecureActivity.timersms;

public class NotiTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_task);

        setTitle("GPS Secure Warning");
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);






    }

    public void StopalertClick(View view) {
        //TODO: stop kun sms timer?. Da Geofence skal forsat oprettes?

        timersms.cancel();
        Toast.makeText(this, "Sms udsat! forsæt cykling", Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}
