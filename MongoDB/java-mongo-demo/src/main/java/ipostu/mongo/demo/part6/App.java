package ipostu.mongo.demo.part6;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import static ipostu.mongo.demo.Common.getMongoConnectionString;

public class App {
    public static void main(String[] args) {
        String connectionString = getMongoConnectionString();
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase database = mongoClient.getDatabase("my_db");

        MongoCollection<Document> collection = database.getCollection("accounts");
        Bson query  = Filters.eq("account_type","type1");
        Bson updates  = Updates.combine(Updates.set("minimum_balance",100));
        UpdateResult upResult = collection.updateMany(query, updates);

        System.out.println(upResult);
        mongoClient.close();
    }
}
