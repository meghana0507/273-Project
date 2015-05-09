package facebook;

import java.util.List;
public class PhotoAttributes {


    //add variables about photos
    private String photoUrl ;
    private int numberOfLikes;
    private int numberOfComments;
    private int numberOfTags;
    private List<PersonInfo> nameComments;
    private List<PersonInfo> nameLikes;
    private List<PersonInfo> nameTags;

    public String getPhotoUrl(){
        return photoUrl;
    }

    public void setPhotoUrl(String url){
        this.photoUrl = url;
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
