package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class SpecificTrack extends AppCompatActivity {
private Track track;
private TextView specific_track_information, specific_track_parkinginformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_track);

        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra("Track");


        //appbar
        setTitle(track.name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        specific_track_information = findViewById(R.id.specific_track_information);
        specific_track_information.setText(track.info);

        specific_track_parkinginformation = findViewById(R.id.specific_track_parkinginformation);
        specific_track_parkinginformation.setText(track.parkInfo);

    }


    public void mainFloatBtnClicked(View view) {

    }
}
