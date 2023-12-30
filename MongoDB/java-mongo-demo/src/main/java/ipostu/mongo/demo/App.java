package ipostu.mongo.demo;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            List<Document> databases = mongoClient.listDatabases().into(new LinkedList<>());
            databases.forEach(db -> System.out.println(db.toJson()));
        }
    }
}
