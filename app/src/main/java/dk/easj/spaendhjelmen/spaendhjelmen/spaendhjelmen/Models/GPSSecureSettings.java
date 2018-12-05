package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

public class GPSSecureSettings {
    String ContactNumber1;
    String ContactNumber2;
    String ContactNumber3;
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

    public String getContactNumber1() {
        return ContactNumber1;
    }

    public void setContactNumber1(String contactNumber) {
        ContactNumber1 = contactNumber;
    }

    public String getContactNumber2() {
        return ContactNumber2;
    }

    public void setContactNumber2(String contactNumber) {
        ContactNumber2 = contactNumber;
    }

    public String getContactNumber3() {
        return ContactNumber3;
    }

    public void setContactNumber3(String contactNumber) {
        ContactNumber3 = contactNumber;
    }

    public String getContactMessaage() {
        return ContactMessaage;
    }

    public void setContactMessaage(String contactMessaage) {
        ContactMessaage = contactMessaage;
    }

    public GPSSecureSettings(String contactNumber1,String contactNumber2,String contactNumber3, String contactMessaage, String distance, String time) {
        ContactNumber1 = contactNumber1;
        ContactNumber2 = contactNumber2;
        ContactNumber3 = contactNumber3;
        ContactMessaage = contactMessaage;
        Distance = distance;
        Time = time;
    }

    public GPSSecureSettings(){

    }

}
