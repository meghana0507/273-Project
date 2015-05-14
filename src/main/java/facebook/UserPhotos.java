package facebook;

import java.util.ArrayList;

/**
 * Created by Deep Trivedi on 4/23/2015.
 */
public class UserPhotos {
    private String name;
    private String id;

    ArrayList<PhotoAttributes> photos = new ArrayList<PhotoAttributes>();

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public ArrayList<PhotoAttributes> getPhotos(){
        return photos;
    }

    public void setPhotos(ArrayList<PhotoAttributes> photos){
        this.photos = photos;
    }
}

