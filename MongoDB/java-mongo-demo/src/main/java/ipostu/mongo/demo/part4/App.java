package ipostu.mongo.demo.part4;

import com.mongodb.client.*;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;

public class App {
    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase database = mongoClient.getDatabase("my_db");
        MongoCollection<Document> collection = database.getCollection("accounts");

        try (MongoCursor<Document> cursor = collection
                .find(and(gte("balance", 1000), eq("account_type", "checking")))
                .iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }

//        {"_id": {"$oid": "6592ec5d174f95ce4987fa56"}, "account_type": "checking", "balance": 2000.0}
        Document firstDocument = collection
                .find(and(gte("balance", 1000), eq("account_type", "checking")))
                .first();

//        {"_id": {"$oid": "6592ec5d174f95ce4987fa56"}, "account_type": "checking", "balance": 2000.0}
//        {"_id": {"$oid": "6592ec5d174f95ce4987fa57"}, "account_type": "checking", "balance": 3000.0}
        mongoClient.close();
    }
}
