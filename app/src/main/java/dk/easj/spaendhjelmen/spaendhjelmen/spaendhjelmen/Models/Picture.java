package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Models;

public class Picture {
    private int Id;
    private String Name;
    private byte[] Image;
    private int TrackId;

    public Picture(int id, String name, byte[] image, int trackid){
        this.Id =id;
        this.Name = name;
        this.Image = image;
        this.TrackId = trackid;
    }

    public int getId(){
        return Id;
    }

    public String GetName(){
        return Name;
    }
    public void SetName(String name){
        this.Name = name;
    }

    public byte[] GetImage(){
        return Image;
    }
    public void SetImage(byte[] image){
        this.Image = image;
    }

    public int GetTrackId(){
        return TrackId;
    }
    public void SetTrackId(int trackid){
        this.TrackId = trackid;
    }
}
