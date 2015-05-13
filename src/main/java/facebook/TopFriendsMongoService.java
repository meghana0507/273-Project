package facebook;

/**
 * Created by Meghana on 5/12/2015.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final class TopFriendsMongoService implements TopFriendsService{
    private final TopFriendsRepository repo;

    @Autowired
    TopFriendsMongoService(TopFriendsRepository repo){
        this.repo = repo;
    }

    @Override
    public TopFriendsList create(TopFriendsList li)
    {
        repo.save(li);
        return li;
    }
}
