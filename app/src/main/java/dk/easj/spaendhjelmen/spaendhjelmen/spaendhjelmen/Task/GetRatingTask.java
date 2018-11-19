package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities.SpecificTrack;

public class GetRatingTask extends AsyncTask<String,Integer,Integer> {

private WeakReference<SpecificTrack> activityWeakReference;

    public GetRatingTask(SpecificTrack activity) {
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
            while ((inputline = in.readLine()) != null){
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
        SpecificTrack activity = activityWeakReference.get();
        if (s.equals(null)) activity.ratingBar.setRating(0);
        else activity.ratingBar.setRating(s);
    }
}
