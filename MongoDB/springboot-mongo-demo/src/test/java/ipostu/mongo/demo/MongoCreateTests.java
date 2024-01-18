package ipostu.mongo.demo;

import ch.qos.logback.classic.Level;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

class MongoCreateTests {
    private static final Logger LOG = LoggerFactory.getLogger(MongoCreateTests.class);
    static {
        ((ch.qos.logback.classic.Logger)LOG).setLevel(Level.DEBUG);
    }
    private static final ConnectionString connectionString = new ConnectionString("mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db");

    private final List<Runnable> cleanupCallbacks = new LinkedList<>();

    @AfterEach
    void cleanup() {
        cleanupCallbacks.forEach(Runnable::run);
    }

    @Test
    void testCreateStudentRecord() {
        String collectionSuffix = RandomStringUtils.randomAlphabetic(10);
        String collectionName = "students" + "_" + collectionSuffix;

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoDatabase database = MongoClients.create(mongoClientSettings).getDatabase("my_db");
        database.createCollection(collectionName);


        cleanupCallbacks.add(() -> {
            database.getCollection(collectionName).drop();
            LOG.info("Collection: {} was successfully dropped", collectionName);
        });
    }


}
