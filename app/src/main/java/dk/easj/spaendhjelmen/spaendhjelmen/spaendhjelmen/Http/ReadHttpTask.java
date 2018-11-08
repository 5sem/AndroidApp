package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.HttpHelper;


public class ReadHttpTask extends AsyncTask<String, Void, CharSequence> {
    @Override
    protected CharSequence doInBackground(String... urls) {
        String urlString = urls[0];
        try {
            CharSequence result = HttpHelper.GetHttpResponse(urlString);
            return result;
        } catch (IOException ex) {
            cancel(true);
            String errorMessage = ex.getMessage() + "\n" + urlString;
            Log.e("SHIT", errorMessage);
            return errorMessage;
        }
    }
}

