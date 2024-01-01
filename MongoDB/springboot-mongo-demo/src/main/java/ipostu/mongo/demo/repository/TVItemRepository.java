package ipostu.mongo.demo.repository;

import ipostu.mongo.demo.model.TVItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TVItemRepository extends MongoRepository<TVItem, String> {

    @Query("{name: '?0'}")
    TVItem findItemByName(String name);

    @Query(value = "{category: '?0'}", fields = "{'name': 1, 'quantity': 1}")
    List<TVItem> findAll(String category);

    long count();
}
