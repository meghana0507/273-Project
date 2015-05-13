package facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Meghana on 5/12/2015.
 */

public class TopFriendsList {

    List friendsList = new ArrayList();

    public TopFriendsList(){
        for(int i=0; i<5; i++)
        {
            friendsList.add(0);
        }
    }

    public void setFriendsList(List l)
    {
        Collections.copy(friendsList, l);
    }

    public List getFriendsList(){
        return friendsList;
    }
}
