package ipostu.mongo.demo;

import ipostu.mongo.demo.model.TVItem;
import ipostu.mongo.demo.repository.TVItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Random;

@EnableMongoRepositories("ipostu.mongo.demo.repository")
@SpringBootApplication
public class MongoDemoApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDemoApplication.class);

    @Autowired
    private TVItemRepository tvItemRepository;

    public static void main(String[] args) {
        SpringApplication.run(MongoDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();
        LOG.warn("123");
        LOG.error("123");
        LOG.warn("123");
        LOG.warn("123");
        tvItemRepository.deleteAll();
        String name1 = "test1" + random.nextInt(20);

        //create
        tvItemRepository.insert(new TVItem(null, name1, 21, "category1"));
        tvItemRepository.insert(new TVItem(null, "test2" + random.nextInt(20), 22, "category1"));
        tvItemRepository.insert(new TVItem(null, "test3" + random.nextInt(20), 23, "category2"));
        tvItemRepository.insert(new TVItem(null, "test4" + random.nextInt(20), 24, "category2"));

        var elements = tvItemRepository.findAll();

        //2
        var elementsForCategory2 = tvItemRepository.findAll("category2");
        //2
        var elementsForCategory1 = tvItemRepository.findAll("category1");

        var test1Element = tvItemRepository.findItemByName(name1);
        test1Element.setName("abc1");
        tvItemRepository.save(test1Element);

        long count = tvItemRepository.count();
        elementsForCategory1 = tvItemRepository.findAll("category1");
    }
}
