package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class CommentAdapter extends BaseAdapter {

    private static final DateFormat dateFormat = DateFormat.getDateInstance();
    ArrayList<UserComment> commentList;
    Context context;

    public CommentAdapter(Context context, ArrayList<UserComment> commentList){
        this.context = context;
        this.commentList =commentList;
    }

    @Override
    public int getCount() {
            return commentList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.comment_list_item, null);
        }
        UserComment comment =commentList.get(position);


        ImageView imageView =(ImageView) convertView.findViewById(R.id.comment_ImageProflePic);
        TextView txtnavn = (TextView) convertView.findViewById(R.id.comment_txtViewNavn);
        TextView txtcomment = (TextView) convertView.findViewById(R.id.comment_txtViewComment);
        TextView txtedited = (TextView) convertView.findViewById(R.id.Comment_TxtViewEdited);
        TextView txtcreated = (TextView) convertView.findViewById(R.id.Comment_TxtViewCreated);

        txtnavn.setText(Integer.toString(comment.userId));
        //TODO: husk at ændre userid til navn
        txtcomment.setText(comment.usercomment);
        txtedited.setText(dateFormat.format(comment.edited.getTimeInMillis()));
        txtcreated.setText(dateFormat.format(comment.created.getTimeInMillis()));


        return convertView;
    }
}
