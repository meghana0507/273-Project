package facebook;

import java.util.ArrayList;

/**
 * Created by Deep Trivedi on 4/25/2015.
 */

public class UserPosts {
    private String name;
    private String id;


    ArrayList<PostAttributes> post = new ArrayList<PostAttributes>();

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

    public ArrayList<PostAttributes> getPosts(){
        return post;
    }

    public void setPosts(ArrayList<PostAttributes> post){
        this.post = post;
    }

}
