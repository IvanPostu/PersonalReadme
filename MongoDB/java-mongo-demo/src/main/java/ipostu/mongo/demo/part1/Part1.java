package ipostu.mongo.demo.part1;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

public class Part1 {
    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(connectionString);
        List<Document> databases = mongoClient.listDatabases().into(new LinkedList<>());
        databases.forEach(db -> System.out.println(db.toJson()));
        mongoClient.close();
    }
}