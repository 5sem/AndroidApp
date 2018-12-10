package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

import android.support.v4.media.MediaDescriptionCompat;

import java.io.Serializable;

public class User implements Serializable {
    private int Id;
    public String AuthToken;
    public String Username;
    //public byte[] Image;
    public String Description;
    public boolean Privacy;


    public User(int id, String authToken, String username, String description, boolean privacy) {
        Id = id;
        AuthToken = authToken;
        Username = username;
        Description = description;
        Privacy = privacy;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isPrivacy() {
        return Privacy;
    }

    public void setPrivacy(boolean privacy) {
        Privacy = privacy;
    }
}
