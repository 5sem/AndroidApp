package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class NotiTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_task);

        setTitle("GPS Secure Warning");
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);






    }
}
