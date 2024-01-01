package ipostu.mongo.demo.part3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
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

        Document document1 = new Document("_id", new ObjectId())
                .append("str", "abc 123 aaa")
                .append("date", new Date());
        Document document2 = new Document("_id", new ObjectId())
                .append("str", "abc 123 aaa")
                .append("date", new Date());
        List<Document> documents = List.of(document1, document2);

        InsertManyResult insertManyResult = collection.insertMany(documents);
        Map<Integer, BsonValue> insertedDocuments = insertManyResult.getInsertedIds();
        // {0=BsonObjectId{value=6592e45c5929350e92b905d2}, 1=BsonObjectId{value=6592e45c5929350e92b905d3}}

        System.out.println("Inserted with success: " + insertedDocuments);
        mongoClient.close();
    }
}
