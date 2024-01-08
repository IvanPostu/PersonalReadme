package ipostu.mongo.demo;

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
    private final List<Runnable> cleanupCallbacks = new LinkedList<>();

    @AfterEach
    void cleanup() {
        cleanupCallbacks.forEach(Runnable::run);
    }

    @Test
    void testCreateStudentRecord() {
        String collectionSuffix = RandomStringUtils.randomAlphabetic(10);
        String collectionName = "students" + "_" + collectionSuffix;

        MongoDatabase database = MongoTestConfiguration.getDatabase();
        database.createCollection(collectionName);

        LOG.debug("Debug log message");
        LOG.info("Info log message");
        LOG.error("Error log message");

        cleanupCallbacks.add(() -> {
            database.getCollection(collectionName).drop();
            LOG.error("Collection: {} was successfully dropped", collectionName);
        });
    }


}
