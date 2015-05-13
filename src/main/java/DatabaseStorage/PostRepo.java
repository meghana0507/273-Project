package DatabaseStorage;

import facebook.UserPosts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostRepo extends MongoRepository<UserPosts,String>{

}