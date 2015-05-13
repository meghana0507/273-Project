package DatabaseStorage;

import facebook.UserPhotos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PhotoRepo extends MongoRepository<UserPhotos,String>{

}