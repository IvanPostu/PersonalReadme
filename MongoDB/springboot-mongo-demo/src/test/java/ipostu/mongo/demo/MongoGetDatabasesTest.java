package ipostu.mongo.demo;

import ch.qos.logback.classic.Level;
import com.mongodb.ConnectionString;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoGetDatabasesTest {
    private static final Logger LOG = LoggerFactory.getLogger(MongoCreateTests.class);
    private static final Set<String> REQUIRED_COLLECTIONS_TO_BE_PRESENT = Set.of(
            "accounts",
            "developers",
            "mycollection",
            "students",
            "university_courses",
            "university_courses_enrolment"
    );

    static {
        ((ch.qos.logback.classic.Logger) LOG).setLevel(Level.DEBUG);
    }

    private static final ConnectionString REGULAR_USER_CONNECTION_STRING = new ConnectionString(
            "mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db");
    private static final ConnectionString ADMIN_USER_CONNECTION_STRING = new ConnectionString(
            "mongodb://root:root@localhost:27017/admin");

    @Test
    void testGetAllCollectionsForRegularUserDatabaseTest() {
        try (MongoClient mongoClient = MongoClients.create(REGULAR_USER_CONNECTION_STRING)) {
            List<Document> collections = mongoClient.getDatabase("my_db").listCollections().into(new LinkedList<>());
            List<String> collectionNames = collections.stream()
                    .peek(collectionDocument -> {
                        assertThat(collectionDocument)
                                .containsOnlyKeys("name", "type", "options", "info", "idIndex");
                    })
                    .map(collectionDocument -> collectionDocument.getString("name"))
                    .toList();
            assertThat(collectionNames).contains(REQUIRED_COLLECTIONS_TO_BE_PRESENT.toArray(String[]::new));
        }
    }

    @Test
    void testAllExpectedDatabasesForAdminUserArePresent() {
        try (MongoClient mongoClient = MongoClients.create(ADMIN_USER_CONNECTION_STRING)) {
            ListDatabasesIterable<Document> databasesIterable = mongoClient.listDatabases();

            List<String> collectedDatabaseNames = new LinkedList<>();
            try (MongoCursor<Document> databaseIterator = databasesIterable.iterator()) {
                while (databaseIterator.hasNext()) {
                    Document databaseDocument = databaseIterator.next();

                    assertThat(databaseDocument)
                            .hasSize(3)
                            .containsKey("name")
                            .containsKey("sizeOnDisk")
                            .containsKey("empty");

                    String databaseName = databaseDocument.getString("name");
                    collectedDatabaseNames.add(databaseName);
                }
            }

            assertThat(collectedDatabaseNames)
                    .containsExactly("admin", "config", "local", "my_db");
        }
    }

    @Test
    void testRegularUserCanSeeOnlyHisDatabase() {
        try (MongoClient mongoClient = MongoClients.create(REGULAR_USER_CONNECTION_STRING)) {
            List<Document> databases = mongoClient.listDatabases().into(new LinkedList<>());

            assertThat(databases)
                    .hasSize(1)
                    .anySatisfy(databaseDocument -> {
                        String databaseName = databaseDocument.getString("name");
                        assertThat(databaseName).isEqualTo("my_db");
                    });
        }
    }
}
