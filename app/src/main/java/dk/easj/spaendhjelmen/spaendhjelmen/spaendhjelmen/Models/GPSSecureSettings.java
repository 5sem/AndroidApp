package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

public class GPSSecureSettings {
    String ContactNumber;
    String ContactMessaage;
    String Distance;
    String Time;

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getContactMessaage() {
        return "Message";
    }

    public void setContactMessaage(String contactMessaage) {
        ContactMessaage = contactMessaage;
    }

    public GPSSecureSettings(String contactNumber, String contactMessaage, String distance, String time) {
        ContactNumber = contactNumber;
        ContactMessaage = contactMessaage;
        Distance = distance;
        Time = time;
    }

    public GPSSecureSettings(){

    }

}
