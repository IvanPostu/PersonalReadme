package ipostu.mongo.demo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoTestConfiguration {
    private static final MongoClient MONGO_CLIENT = createClient();

    private MongoTestConfiguration() {
    }

    public static MongoClient getMongoClient() {
        return MONGO_CLIENT;
    }

    public static MongoDatabase getDatabase() {
        return getMongoClient().getDatabase("my_db");
    }

    private static MongoClient createClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}
