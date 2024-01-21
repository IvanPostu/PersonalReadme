package ipostu.mongo.demo;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class SortByCountAggregationPipelineTest {

    private static final Logger LOG = LoggerFactory.getLogger(MongoCreateTests.class);

    static {
        ((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);
    }

    private static final ConnectionString REGULAR_USER_CONNECTION_STRING = new ConnectionString(
            "mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db");

    private final List<Runnable> cleanupCallbacks = new LinkedList<>();

    @AfterEach
    void cleanup() {
        cleanupCallbacks.forEach(Runnable::run);
    }

    @Test
    void testSortByCountAggregationPipeline() throws Exception {
        try (MongoClient mongoClient = MongoClients.create(REGULAR_USER_CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase("my_db");
            MongoCollection<Document> collection = generateTemporaryCollection(database);
            cleanupCallbacks.add(collection::drop);

            List<Map<String, Object>> dataset = DatasetLoader
                    .loadDatasetFromResource("BasicUsersDataset.json", new TypeReference<>() {
                    });
            int i = 9;
        }
    }

    private MongoCollection<Document> generateTemporaryCollection(MongoDatabase database) {
        String collectionName = "basicUsers_" + RandomStringUtils.randomAlphanumeric(16);
        database.createCollection(collectionName);

        return database.getCollection(collectionName);
    }
}
