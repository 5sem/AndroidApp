package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.CommentAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters.ViewPagerAdapter;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Http.ReadHttpTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Picture;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.UserComment;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task.GetRatingPersonalTask;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Task.GetRatingTask;

public class SpecificTrack extends AppCompatActivity {
    private Track track;
    private UserComment userComment;
    public ArrayList<Picture> pictureList = new ArrayList<>();
    public ArrayList<Drawable> pictureListdrawable = new ArrayList<>();
    private final ArrayList<UserComment> commentList = new ArrayList<>();
    private TextView
            specific_track_information,
            specific_track_parkinginformation,
            specific_track_length,
            specific_track_city,
            specific_track_region,
            specific_track_maxHeight,
            specific_track_difficulty,
            specific_track_addr;
    public TextView specific_track_Txt_PersonalRating;
    private ImageView imgview;

    private ViewPager viewPager;
    private ProgressBar pgb;
    public RatingBar ratingBar, personalRatingBar;

    private final String TAG = "SpecificTrack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_track);

        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra("Track");

        ratingBar = findViewById(R.id.Specific_rute_rating);
        personalRatingBar = findViewById(R.id.Specific_rute_Personalrating);
        specific_track_Txt_PersonalRating = findViewById(R.id.Specific_rute_TxtpersonalRating);

        //appbar
        setTitle(track.name);
        Toolbar toolbar = findViewById(R.id.toolbartrack);
        setSupportActionBar(toolbar);

        specific_track_information = findViewById(R.id.specific_track_information);
        specific_track_information.setText(track.info);

        specific_track_parkinginformation = findViewById(R.id.specific_track_parkinginformation);
        specific_track_parkinginformation.setText(track.parkInfo);

        specific_track_length = findViewById(R.id.specific_track_length);
        String stringLength = Double.toString(track.length);
        specific_track_length.setText(stringLength + " km");

        specific_track_city = findViewById(R.id.specific_track_city);
        specific_track_city.setText(track.city);

        specific_track_region = findViewById(R.id.specific_track_region);
        specific_track_region.setText(track.regional);

        specific_track_maxHeight = findViewById(R.id.specific_track_maxHeight);
        String stringMaxHeight = Double.toString(track.maxHeight);
        specific_track_maxHeight.setText(stringMaxHeight + " m");

        specific_track_difficulty = findViewById(R.id.specific_track_difficulty);
        specific_track_difficulty.setText(colorCodeConverter(track.colorCode));

        specific_track_addr = findViewById(R.id.specific_track_addr);
        specific_track_addr.setText(track.address + " " + track.city + " " + track.postalcode);

        pgb = findViewById(R.id.specific_track_progressbar);


    }

    //henter informationer fra rest service
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "onStart: ");
        commentList.clear();
        ReadTask task = new ReadTask();
        task.execute("https://spaendhjelmenrest.azurewebsites.net/service1.svc/comments/" + track.getId());
        pictureList.clear();
        pictureListdrawable.clear();
        ReadTaskPicture taskpicture = new ReadTaskPicture();
        taskpicture.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/pictures/" + track.getId());

        //TODO: vi har endnu ikke et bruger ID fordi bruger ikke er logget ind!
        GetRatingPersonalTask getRatingPersonalTask = new GetRatingPersonalTask(this);
        //TODO: lav mig om til public rest
        getRatingPersonalTask.execute("http://localhost:49329/Service1.svc/Rating/personlig/" + "1/" + track.getId());

        GetRatingTask getRatingTask = new GetRatingTask(this);
        getRatingTask.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/Rating/" + track.getId());

        personalRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //TODO: post her
                Log.d(TAG, "onRatingChanged: " + rating);
                //TODO: rest kontrol om user allerede har rated hvis ja, put ellers post
            }
        });
    }

    public void mainFloatBtnClicked(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Opret kommentar");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        final EditText msg = new EditText(this);
        // Message Properties
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button

                String comment = msg.getText().toString();
                int userid = 1; //TODO: admin id, ændre til logged in user id
                int trackid = track.getId();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", userid);
                    jsonObject.put("TrackId", trackid);
                    jsonObject.put("UserComment", comment);
                    String jsonDocument = jsonObject.toString();
                    PostCommentTask task = new PostCommentTask();
                    task.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/comments", jsonDocument);
                } catch (JSONException ex) {
                    Log.d("add", ex.toString());

                }

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });

        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(getResources().getColor(R.color.colorGreen));
        okBT.setLayoutParams(neutralBtnLP);
        okBT.setText("Opret");

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
        cancelBT.setText("Fortryd");
    }

    //region google map

    public void openGoogleMaps(View view) {

        Uri gmmIntentUri = Uri.parse("geo:" + track.latitude + "," + track.longitude + "?q=" + Uri.encode(track.address + "," + track.city));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }


    //endregion


    public void showMenu(View view, final int idtodelete) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_comment_delete:
                        Log.i(TAG, "Slet");
                    {

                        AlertDialog alertDialog = new AlertDialog.Builder(SpecificTrack.this).create();

                        TextView title = new TextView(SpecificTrack.this);
                        title.setText("Slet kommentar?");
                        title.setPadding(10, 10, 10, 10);
                        title.setGravity(Gravity.CENTER);
                        title.setTextColor(Color.BLACK);
                        title.setTextSize(20);
                        alertDialog.setCustomTitle(title);

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Slet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                DeleteTask task = new DeleteTask();
                                task.execute("https://spaendhjelmenrest.azurewebsites.net/Service1.svc/comments/" + idtodelete);
                                finish();
                            }
                        });


                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Fortryd", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });


                        new Dialog(getApplicationContext());
                        alertDialog.show();

                        // Set Properties for OK Button
                        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
                        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
                        okBT.setPadding(50, 10, 10, 10);   // Set Position
                        okBT.setTextColor(getResources().getColor(R.color.colorGreen));
                        okBT.setLayoutParams(neutralBtnLP);
                        okBT.setText("Slet");

                        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
                        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
                        cancelBT.setTextColor(Color.RED);
                        cancelBT.setLayoutParams(negBtnLP);
                        cancelBT.setText("Fortryd");

                        break;
                    }
                    case R.id.menu_comment_edit:
                        Log.i(TAG, "Rediger");
                        break;
                    //TODO: redigere
                }
                return true;
            }
        });
        menu.inflate(R.menu.menu_comment);
        menu.show();
    }

    //region Coonverters
    //converterer jsonstring til tid
    public static Calendar JsonDateToDate(String jsonDate) {
        //  "/Date(1321867151710+0100)/"
        int idx1 = jsonDate.indexOf("(");
        int idx2 = jsonDate.indexOf(")") - 5;
        String s = jsonDate.substring(idx1 + 1, idx2);
        long timeInMilliSeconds = Long.valueOf(s);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return calendar;
    }

    public String colorCodeConverter(String color) {
        if (color.equals("red"))
            return "Rød";
        if (color.equals("black"))
            return "Sort";
        if (color.equals("blue"))
            return "Blå";
        if (color.equals("green"))
            return "Grøn";
        else {

        }
        return null;
    }
    //endregion

    //region Tasks
    private class DeleteTask extends AsyncTask<String, Void, CharSequence> {
        @Override
        protected CharSequence doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int responseCode = connection.getResponseCode();
                if (responseCode % 100 != 2) {
                    throw new IOException("Response code: " + responseCode);
                }
                return "Nothing";
            } catch (MalformedURLException e) {
                return e.getMessage() + " " + urlString;
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            Toast.makeText(SpecificTrack.this, "Kommentar Slettet!", Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
            startActivity(getIntent());
        }

    }

    private class PostCommentTask extends AsyncTask<String, Void, CharSequence> {

        @Override
        protected CharSequence doInBackground(String... params) {
            String urlString = params[0];
            String jsonDocument = params[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(jsonDocument);
                osw.flush();
                osw.close();
                int responseCode = connection.getResponseCode();
                if (responseCode / 100 != 2) {
                    String responseMessage = connection.getResponseMessage();
                    throw new IOException("HTTP response code: " + responseCode + " " + responseMessage);
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                return line;
            } catch (MalformedURLException ex) {
                cancel(true);
                String message = ex.getMessage() + " " + urlString;
                Log.e("POSTEXECUTE", message);
                return message;
            } catch (IOException ex) {
                cancel(true);
                Log.e("POSTEXECUTE", ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            Toast.makeText(SpecificTrack.this, "Kommentar Oprettet!", Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
            startActivity(getIntent());
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            Toast.makeText(SpecificTrack.this, "Fejl: " + charSequence.toString(), Toast.LENGTH_LONG).show();
            Log.d("POSTEXECUTE", charSequence.toString());
            finish();
        }
    }

    private class ReadTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("Id");
                    int trackid = obj.getInt("TrackId");
                    int userid = obj.getInt("UserId");
                    String usercomment = obj.getString("UserComment");

                    String getStringCreated = obj.getString("Created");

                    Calendar created = JsonDateToDate(getStringCreated);
                    String getStringEdited = obj.getString("Edited");

                    Calendar edited = JsonDateToDate(getStringEdited);

                    UserComment userComment = new UserComment(id, trackid, userid, usercomment, created, edited);
                    commentList.add(userComment);
                }
                TextView Kommentar = findViewById(R.id.Specific_track_TxtViewKommentar);
                if (commentList.isEmpty()) Kommentar.setVisibility(View.INVISIBLE);

                ExpandableHeightListView expandableListView = (ExpandableHeightListView) findViewById(R.id.CommentListView);
                expandableListView.setAdapter(new CommentAdapter(SpecificTrack.this, commentList));
                expandableListView.setExpanded(true);
                expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int IdToDelete = commentList.get(position).id;
                        showMenu(view, IdToDelete);
                    }
                });


            } catch (JSONException ex) {
                Log.e("MAINACTIVITY", ex.getMessage());
            }

        }

        @Override
        protected void onCancelled(CharSequence message) {
            Toast.makeText(SpecificTrack.this, "Fejl:" + message.toString(), Toast.LENGTH_SHORT).show();
            Log.e("MAINACTIVITY", message.toString());
        }
    }

    private class ReadTaskPicture extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("Id");
                    String name = obj.getString("Name");
                    JSONArray jsonarray = obj.getJSONArray("Image");

                    byte[] image = new byte[jsonarray.length()];
                    for (int counter = 0; counter < jsonarray.length(); counter++) {
                        image[counter] = (byte) (((int) jsonarray.get(counter)) & 0xFF);
                    }
                    Base64.encodeToString(image, Base64.DEFAULT);

                    int trackid = obj.getInt("TrackId");


                    Picture Pictures = new Picture(id, name, image, trackid);
                    pictureList.add(Pictures);
                    Drawable imagedrawble = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(image, 0, image.length));
                    pictureListdrawable.add(imagedrawble);
                }

                pgb.setVisibility(View.GONE);
                viewPager = findViewById(R.id.viewPagerSpecific);
                ViewGroup.LayoutParams param = viewPager.getLayoutParams();

                param.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()));
                viewPager.setLayoutParams(param);

                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SpecificTrack.this, pictureListdrawable);
                viewPager.setAdapter(viewPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
                tabLayout.setupWithViewPager(viewPager, true);

            } catch (JSONException ex) {
                Log.e("SPECIFICACTIVITY", ex.getMessage());
            }
        }

        @Override
        protected void onCancelled(CharSequence message) {
            Toast.makeText(SpecificTrack.this, "Fejl:" + message.toString(), Toast.LENGTH_SHORT).show();
            Log.e("SPECIFICACTIVITY", message.toString());
        }
    }


    //endregion
}
