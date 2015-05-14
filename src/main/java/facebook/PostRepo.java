package facebook;

/**
 * Created by deep on 5/12/15.
 */
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostRepo extends MongoRepository<UserPosts,String>{

}