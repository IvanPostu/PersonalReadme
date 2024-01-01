package ipostu.mongo.demo.part5;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class App {
    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase database = mongoClient.getDatabase("my_db");
        MongoCollection<Document> collection = database.getCollection("accounts");



//        {"_id": {"$oid": "6592ec5d174f95ce4987fa56"}, "account_type": "checking", "balance": 2000.0}
        mongoClient.close();
    }
}
