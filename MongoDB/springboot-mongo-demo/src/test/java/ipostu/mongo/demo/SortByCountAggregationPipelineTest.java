package ipostu.mongo.demo;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertManyResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    @MethodSource("aggregateOperations")
    @ParameterizedTest(name = "{0}")
    void testSortByCountAggregationPipeline(String name, Function<MongoCollection<Document>, AggregateIterable<Document>> aggregateCollection) throws Exception {
        MongoClient mongoClient = MongoClients.create(REGULAR_USER_CONNECTION_STRING);
        MongoCollection<Document> collection = null;
        try {
            MongoDatabase database = mongoClient.getDatabase("my_db");
            collection = generateTemporaryCollection(database);
            prepareDataset("BasicUsersDataset.json", collection, (insertManyResult) ->
                    assertThat(insertManyResult.getInsertedIds()).hasSize(20));

            List<Document> queryResult = new ArrayList<>(20);
            aggregateCollection.apply(collection).into(queryResult);

            assertThat(queryResult).hasSize(19);
            assertThat(queryResult.get(0)).isEqualTo(new Document("_id", "Paris").append("count", 2));
            assertThat(queryResult).contains(
                    new Document("_id", "Paris").append("count", 2),
                    new Document("_id", "Tokyo").append("count", 1),
                    new Document("_id", "Sydney").append("count", 1),
                    new Document("_id", "Seoul").append("count", 1),
                    new Document("_id", "Riyadh").append("count", 1),
                    new Document("_id", "Rio de Janeiro").append("count", 1),
                    new Document("_id", "New York").append("count", 1),
                    new Document("_id", "Nairobi").append("count", 1),
                    new Document("_id", "Mumbai").append("count", 1),
                    new Document("_id", "Moscow").append("count", 1),
                    new Document("_id", "Mexico City").append("count", 1),
                    new Document("_id", "Madrid").append("count", 1),
                    new Document("_id", "London").append("count", 1),
                    new Document("_id", "Lisbon").append("count", 1),
                    new Document("_id", "Cairo").append("count", 1),
                    new Document("_id", "Buenos Aires").append("count", 1),
                    new Document("_id", "Bogotá").append("count", 1),
                    new Document("_id", "Berlin").append("count", 1),
                    new Document("_id", "Beijing").append("count", 1)
            );
        } finally {
            if (collection != null) {
                collection.drop();
            }
            mongoClient.close();
        }
    }

    private void prepareDataset(String datasetResourcePath, MongoCollection<Document> collection,
                                Consumer<InsertManyResult> insertResultConsumer) throws IOException {

        List<Map<String, Object>> dataset = DatasetLoader
                .loadDatasetFromResource(datasetResourcePath, new TypeReference<>() {
                });
        List<Document> documentsToBeInserted = dataset.stream().map(Document::new)
                .toList();
        InsertManyResult insertManyResult = collection.insertMany(documentsToBeInserted);
        insertResultConsumer.accept(insertManyResult);
    }

    private MongoCollection<Document> generateTemporaryCollection(MongoDatabase database) {
        String collectionName = "basicUsers_" + RandomStringUtils.randomAlphanumeric(16);
        database.createCollection(collectionName);

        return database.getCollection(collectionName);
    }

    private static Arguments[] aggregateOperations() {
        Function<MongoCollection<Document>, AggregateIterable<Document>> aggregateUsingSortByCount =
                collection -> collection.aggregate(List.of(new Document("$sortByCount", "$city")));
        Function<MongoCollection<Document>, AggregateIterable<Document>> aggregateUsingSeparatePipelines =
                collection -> collection.aggregate(List.of(
                        new Document("$group",
                                new Document(Map.of(
                                        "_id", "$city",
                                        "count", new Document("$sum", Integer.valueOf(1))))),
                        new Document("$sort", new Document(Map.of("count", Integer.valueOf(-1))))));
        Function<MongoCollection<Document>, AggregateIterable<Document>> refactoredAggregateUsingSortByCount =
                collection -> {
                    Bson sortByCount = Aggregates.sortByCount("$city");
                    return collection.aggregate(List.of(sortByCount));
                };
        Function<MongoCollection<Document>, AggregateIterable<Document>> refactoredAggregateUsingSeparatePipelines =
                collection -> {
                    Bson groupPipeline = Aggregates.group("$city", Accumulators.sum("count", Integer.valueOf(1)));
                    Bson sortPipeline = Aggregates.sort(Sorts.orderBy(Sorts.descending("count")));
                    return collection.aggregate(List.of(groupPipeline, sortPipeline));
                };


        return new Arguments[]{
                arguments("aggregateUsingSortByCount", aggregateUsingSortByCount),
                arguments("aggregateUsingSeparatePipelines", aggregateUsingSeparatePipelines),
                arguments("refactoredAggregateUsingSortByCount", refactoredAggregateUsingSortByCount),
                arguments("refactoredAggregateUsingSeparatePipelines", refactoredAggregateUsingSeparatePipelines),
        };
    }
}
