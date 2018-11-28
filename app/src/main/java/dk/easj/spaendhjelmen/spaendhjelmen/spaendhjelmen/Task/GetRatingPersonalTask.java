package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.SpecificTrack;

public class GetRatingPersonalTask extends AsyncTask<String, Integer, Integer> {

    private WeakReference<SpecificTrack> activityWeakReference;


    public GetRatingPersonalTask(SpecificTrack activity) {
        activityWeakReference = new WeakReference<>(activity);
    }


    @Override
    protected Integer doInBackground(String... strings) {
        String urlString = strings[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputline;
            StringBuffer response = new StringBuffer();
            while ((inputline = in.readLine()) != null) {
                response.append(inputline);
            }
            in.close();
            int INTresposne = Integer.parseInt(response.toString());
            return INTresposne;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer s) {
        final SpecificTrack activity = activityWeakReference.get();
        if (s != null) {
            activity.personalRatingBar.setRating(s);

            activity.personalRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {

                    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

                    // Set Custom Title
                    TextView title = new TextView(activity);
                    // Title Properties
                    title.setText("Bedøm rute med antal stjerner: " + rating);
                    title.setPadding(10, 10, 10, 10);   // Set Position
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.BLACK);
                    title.setTextSize(20);
                    alertDialog.setCustomTitle(title);

                    // Set Button
                    // you can more buttons
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform Action on Button
                            activity.PostToDb(rating);

                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform Action on Button
                            //TODO: gør så man kan klikke cancel også viser den  orignale rating
                        }
                    });

                    new Dialog(activity.getApplicationContext());
                    alertDialog.show();

                    // Set Properties for OK Button
                    final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
                    neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
                    okBT.setPadding(50, 10, 10, 10);   // Set Position
                    okBT.setTextColor(activity.getResources().getColor(R.color.colorGreen));
                    okBT.setLayoutParams(neutralBtnLP);
                    okBT.setText("Bedøm");

                    final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
                    negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
                    cancelBT.setTextColor(Color.RED);
                    cancelBT.setLayoutParams(negBtnLP);
                    cancelBT.setText("Fortryd");



                    Log.d("SpecificTrack", "onRatingChanged: " + rating);

                    //TODO: rest, ser om bruger allerede har noget på db? har bruger put, har bruger ikke post
                }
            });


        }
    }
}
