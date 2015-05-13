package facebook;

import com.restfb.types.Event;

import java.security.acl.Owner;
import java.util.List;

/**
 * Created by Deep Trivedi on 4/25/2015.
 */

public class PostAttributes {

    //add variables about photos
    private String type ;
    private String message;
    private String owner;
    private int numberOfLikes;
    private int numberOfComments;
    private int numberOfTags;
    private List<PersonInfo> nameComments;
    private List<PersonInfo> nameLikes;
    private List<PersonInfo> nameTags;

//    private int numberOfComments
//    private List<Comment> comments;

//    public String getPhotoUrl(){
    //      return photoUrl;
    ///}

    //public void setPhotoUrl(String url){
    //  this.photoUrl = url;
    //}
    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public List<PersonInfo> getLikes(){
        return nameLikes;
    }
    public List<PersonInfo> getComments(){
        return nameComments;
    }
    public List<PersonInfo> getTags(){
        return nameTags;
    }

    public void setLikes(List<PersonInfo> nameList){
        this.nameLikes = nameList;
    }
    public void setComments(List<PersonInfo> nameList){
        this.nameComments = nameList;
    }
    public void setTags(List<PersonInfo> nameList){
        this.nameTags = nameList;
    }
    public int getNumberOfLikes(){
        return numberOfLikes;
    }

    public void setNumberOfLikes(int number){
        this.numberOfLikes = number;
    }

    public int getNumberOfComments(){
        return numberOfComments;
    }

    public void setNumberOfComments(int value){
        this.numberOfComments = value;
    }
    public int getNumberOfTags(){
        return numberOfTags;
    }

    public void setNumberOfTags(int value){
        this.numberOfTags = value;
    }

}
