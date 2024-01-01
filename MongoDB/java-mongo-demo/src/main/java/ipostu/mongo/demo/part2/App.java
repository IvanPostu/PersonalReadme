package ipostu.mongo.demo.part2;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase database = mongoClient.getDatabase("my_db");
        MongoCollection<Document> collection = database.getCollection("mycollection");

        Document document = new Document("_id", new ObjectId())
                .append("str", "abc 123 aaa")
                .append("date", new Date());

        InsertOneResult inserted = collection.insertOne(document);
        BsonValue id = inserted.getInsertedId();
        System.out.println("Inserted with success: " + id);
        mongoClient.close();
    }

}
