package facebook;

/**
 * Created by Meghana on 5/12/2015.
 */

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
interface TopFriendsRepository extends MongoRepository<TopFriendsList, String>{
    TopFriendsList save(TopFriendsList saved);
}
