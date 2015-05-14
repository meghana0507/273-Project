package facebook;

/**
 * Created by deep on 5/11/15.
 */

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PhotoRepo extends MongoRepository<UserPhotos,String>{
 //UserPhotos save(UserPhotos saved);
}