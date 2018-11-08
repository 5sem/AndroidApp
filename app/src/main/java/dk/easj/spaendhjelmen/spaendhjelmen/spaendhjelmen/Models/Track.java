package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

import java.io.Serializable;

public class Track implements Serializable {
    private int id;
    public int pictureId;
    public String name;
    public String info;
    public double longitude;
    public double latitude;
    public String address;
    public String colorCode;
    public double length;
    public double maxHeight;
    public String parkInfo;
    public String regional;
    public int postalcode;
    public String city;

    public Track(int id, int pictureid, String name, String info, double longitude, double latitude, String address, String colorcode, double length, double maxheight, String parkinfo, String regional, int postalcode, String city){
        this.id =id;
        this.pictureId = pictureid;
        this.name = name;
        this.info =info;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.colorCode = colorcode;
        this.length = length;
        this.maxHeight = maxheight;
        this.parkInfo = parkinfo;
        this.regional = regional;
        this.postalcode = postalcode;
        this.city = city;

    }

    public int getId(){
        return id;
    }

    public int GetpictureId(){
        return pictureId;
    }
    public void SetpictureId(int pictureid){
        this.pictureId = pictureid;
    }

    public String Getname(){
        return name;
    }
    public void Setname(String name){
        this.name = name;
    }

    public String Getinfo(){
        return info;
    }
    public void Setinfo(String info){
        this.info = info;
    }

    public double Getlongitude(){
        return longitude;
    }
    public void Setlongitude(float longitude){
        this.longitude = longitude;
    }

    public double Getlatitude(){
        return latitude;
    }
    public void Setlatitude(float latitude){
        this.latitude = latitude;
    }

    public String Getaddress(){
        return address;
    }
    public void Setaddress(String adress){
        this.address = address;
    }

    public String Getcolorcode(){
        return colorCode;
    }
    public void Setcolorcode(String colorcode){
        this.colorCode = colorcode;
    }

    public double Getlength(){
        return length;
    }
    public void Setlength(float length){
        this.length = length;
    }

    public double Getmaxheight(){
        return maxHeight;
    }
    public void Setmaxheight(float maxHeight){
        this.maxHeight = maxHeight;
    }

    public String Getparkinfo(){
        return parkInfo;
    }
    public void Setparkinfo(String parkinfo){
        this.parkInfo = parkinfo;
    }

    public String Getregional(){
        return regional;
    }
    public void Setregional(String regional){
        this.regional = regional;
    }

    public int Getpostalcode(){
        return postalcode;
    }
    public void Setpostalcode(int postalcode){
        this.postalcode = postalcode;
    }

    public String Getcity(){
        return city;
    }
    public void Setcity(String city){
        this.city = city;
    }




}
