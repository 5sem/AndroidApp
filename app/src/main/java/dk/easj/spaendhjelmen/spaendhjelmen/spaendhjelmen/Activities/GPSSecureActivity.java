package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

import static dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.App.channel_Alert;

public class GPSSecureActivity extends AppCompatActivity {

private NotificationManagerCompat notificationManagerCompat;
private static final int sendSmsPermissionsRequestCode = 0;

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

    public boolean CheckPermissions(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return check == PackageManager.PERMISSION_GRANTED;
    }



    public void gpsSecureTændSlukClicked(View view) {
        SendOnChannelAlert("TestTitle", "TestContext");
    }

    public void gpsSecureIndstillingerClicked(View view) {
        SendAlertSMS();
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


}
