package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

import java.io.Serializable;

public class User implements Serializable {
    private int Id;
    public String AuthToken;
    public String Username;
    public byte[] Image;
    public int ContactNumber;
    public String ContactMessage;
    public boolean Privacy;


    public User(int id, String authToken, String username) {
        Id = id;
        AuthToken = authToken;
        Username = username;
        //Image = image;
        //ContactNumber = contactNumber;
        //ContactMessage = contactMessage;
        //Privacy = privacy;
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

    //public byte[] getImage() {
    //    return Image;
   // }

   // public void setImage(byte[] image) {
    //    Image = image;
    //}

    //public int getContactNumber() {
    //    return ContactNumber;
    //}

    //  public void setContactNumber(int contactNumber) {
    //      ContactNumber = contactNumber;
    // }

    // public String getContactMessage() {
    //     return ContactMessage;
    // }

    //public void setContactMessage(String contactMessage) {
    //     ContactMessage = contactMessage;
    // }

    // public boolean isPrivacy() {
    //     return Privacy;
    // }

    // public void setPrivacy(boolean privacy) {
    //     Privacy = privacy;
    // }
}
