package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import dk.easj.spaendhjelmen.spaendhjelmen.R;
import dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models.Track;

public class TrackAdapter extends BaseAdapter {
    ArrayList<Track> trackList;
    Context context;

    public TrackAdapter(Context context, ArrayList<Track> trackList){
        this.context = context;
        this.trackList =trackList;
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.track_list_item, null);
        }
        Track track =trackList.get(position);


        ImageView imageView =(ImageView) convertView.findViewById(R.id.imageRegional);
        TextView txtColorcode = (TextView) convertView.findViewById(R.id.colorCode);
        TextView txtHeadline = (TextView) convertView.findViewById(R.id.txtHeadline);
        TextView txtLenght = (TextView) convertView.findViewById(R.id.txtLenght);
        TextView txtCity = (TextView) convertView.findViewById(R.id.txtCity);
        TextView txtRegional = (TextView) convertView.findViewById(R.id.txtRegional);

        txtColorcode.setBackgroundColor(Color.parseColor(track.colorCode));

        if (track.regional.equals("Sjælland")){
            imageView.setImageResource(R.drawable.sjaelland);
        } else if(track.regional.equals("Fyn")) {
            imageView.setImageResource(R.drawable.fyn);
        }else if(track.regional.equals("Jylland")){
            imageView.setImageResource(R.drawable.jylland);
        }



        txtRegional.setText("Landsdel: " + track.regional);
        txtCity.setText("By: "+track.city);
        txtHeadline.setText(track.name);
        txtLenght.setText("Længde: " + Double.toString(track.length) + " Km");
        return convertView;
    }

    // download image
}
