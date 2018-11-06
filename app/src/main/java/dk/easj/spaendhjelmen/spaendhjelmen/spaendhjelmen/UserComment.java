package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen;

import java.io.Serializable;
import java.util.Calendar;

public class UserComment implements Serializable {
    private int id;
    int trackId;
    int userId;
    Calendar created;
    Calendar edited;
    String usercomment;

    public UserComment(int id, int trackid, int userid,String comment, Calendar created, Calendar edited){
        this.created = created;
        this.edited = edited;
        this.id = id;
        this.trackId = trackid;
        this.userId = userid;
        this.usercomment = comment;
    }

    public int GetId(){return id;}

    public int GettrackId(){
        return trackId;
    }
    public void SettrackId(int trackId){
        this.trackId = trackId;
    }

    public int GetuserId(){
        return userId;
    }
    public void SetuserId(int userId){this.userId = userId; }

    public Calendar Getcreated(){
        return created;
    }
    public void Setcreated(Calendar created){
        this.created = created;
    }

    public Calendar Getedited(){
        return edited;
    }
    public void Setedited(Calendar edited){
        this.edited = edited;
    }

    public String Getusercommnet(){
        return usercomment;
    }
    public void Setusercomment(String comment){
        this.usercomment= comment;
    }

}
