# MongoDB

## Setup MongoDB Container
```bash
# to install a mongo client in isolated shell
$ nix-shell ./mongo-client.nix --run bash

# to remove recursively shared data folder if needed
$ docker-compose up --build -d
$ docker-compose stop

#cleanup 
$ docker-compose down && sudo rm -rf ./_container_data && docker image prune -a
$ docker exec -it ipostu-mongo bash
```

## Connect through cli tool
```bash
$ mongosh --host 127.0.0.1 --port 27017 --username root --password root --authenticationDatabase admin

$ mongo --host 127.0.0.1 --port 27017 -u root -p root --authenticationDatabase admin

# connect through cli using connection string
$ mongo 'mongodb://root:root@localhost:27017/admin'

$ mongo --nodb
> conn = new Mongo("127.0.0.1:27017")
> db = conn.getDB("admin")
> db.auth('root', 'root');

$ show dbs        # Show the list of databases
$ use your_db     # Switch to a specific database
$ db.collection.find()  # similar to SELECT *
```
## Execute JS
```bash
$ mongo --quiet
> x = 200
200
> x / 2
100
> Math.sin(Math.PI / 2);
1
> new Date('2019/12/13')
ISODate("2019-12-13T00:00:00Z")

> function factorial(n) {
... if (n <= 1) return 1;
... return n * factorial(n - 1);
... }
> factorial(5)
120
```

## Execute JS script
```bash
$ mongosh 127.0.0.1:27017/foo --quiet script1.js
I am script 1

# or

$ mongosh --host 127.0.0.1 --port 27017 -u root -p root
test> load("script1.js")
I am script 1

# or define a factorial function in the script2.js
test> load("script2.js")
true
test> typeof factorial
function
test> factorial(5)
120

# use video         -> db.getSisterDB("video")
# show dbs          -> db.getMongo().getDBs()
# show collections  -> db.getCollectionNames()
```
## .mongorc.js is called when the shell is ran
```js
var compliment = ["attractive", "intelligent", "like Batman"];
var index = Math.floor(Math.random()*3);
print("Hello, you're looking particularly "+compliment[index]+" today!");

$ mongo
MongoDB shell version: 4.2.1
connecting to: test
Hello, you're looking particularly like Batman today!
```
To not execute .mongorc.js, should be used the flag `-norc`

## CRUD
```js
// CREATE
> movie = {"title" : "Star Wars: Episode IV – A New Hope", "director" : "George Lucas", "year" : 1977}
> db.movies.insertOne(movie)

// READ
> db.movies.find().pretty()
> db.movies.findOne()

// UPDATE
> db.movies.updateOne({title : "Star Wars: Episode IV – A New Hope"}, {$set : {reviews: []}})

// DELETE
> db.movies.deleteOne({title : "Star Wars: Episode IV – A New Hope"})

```

```sh
# Create
$ db.collection.insertOne({...})
$ db.collection.insertMany([{...}, {...}])

# Read
$ db.collection.find({field: { $eq: value }}) # match by value
$ db.collection.find({field: value}) # shorter form
$ db.collection.find({field: { $in: [value1, value2 ...] }}) # check equality to one of ...

$ db.collection.find({field: { $gt: value }}) # greater than
$ db.collection.find({field: { $lt: value }}) # lesser than
$ db.collection.find({field: { $gte: value }}) # greater than or equal
$ db.collection.find({field: { $lte: value }}) # lesser than or equal
# E.g.:
$ db.sales.find({ "items.price": { $gt: 50}})
$ db.sales.find({ "items.price": { $lt: 50}})
$ db.sales.find({ "customer.age": { $lte: 65}})
$ db.sales.find({ "customer.age": { $gte: 65}})

$ db.collection.find({field: { $elemMatch: { $eq: "abc" } }}) # is applied to sub-collection if it is not collection then the record is ignored
$ db.sales.find({
  items: {
    $elemMatch: { name: "laptop", price: { $gt: 800 }, quantity: { $gte: 1 } },
  },
}) # other example...

$ db.collection.find({field: { $and: {...} }}) # AND
$ db.collection.find({field: { <condition1>, <condition2>, ... }}) # Also AND
$ db.collection.find({field: { $or: {...} }}) # OR
# E.g.
$ db.collection.find({field: { "sector": "Mobile Food Vendor - 881" , "address.city": "ASTORIA" , "result": "Pass"}})
$ db.routes.find({
  $and: [
    { $or: [{ dst_airport: "London" }, { src_airport: "London" }] },
    { $or: [{ stops: 0 }, { airline.name: "British Airlines"}] },
  ]
})

# Update
$ db.collection.replaceOne(filter, replacement, options);
# E.g.: 
$ db.books.replaceOne(
  {
    _id: ObjectId("1112afeb441a74a98dbbec4e"),
  },
  {
    title: "Book Name 1"
  }
)

$ db.collection.updateOne(filter, update, options);
# update parameter can be: {...}
#  $set adds a new field or replace existing one
#  $push appends a value to an array or creates an array with one element
# options parameter can be: {...}
# upsert: true|false which if is true creates a new document if it doesn't exists

# updateOne + findOne
$ db.collection.findAndModify({query:{...}, update:{...}, new:true|false});
# E.g. query and update fields are required
$ db.channels.findAndModify({
  query: { _id: ObjectId("1111a92dfee1ff300dc80bf1") },
  update: { $inc: { subscribers: 1 } },
  new: true,
  upsert: true
})
# new flag indicates that the document should be returned even if it is new or created
# upsert = insert or update

$ db.collection.updateMany(filter, update, options);
$ db.articles.updateMany(
  { publishedDate: { $lt: new Date("2019-02-22") } },
  { $set: { status: "LEGACY" } }
)

# Delete
$ db.collection.deleteOne(filter, options);
$ db.collection.deleteMany(filter, options);

```

### Sort & Limit

```sh
$ db.collection.find().sort()
$ db.collection.find().limit()
$ db.collection.find({name: 'test'}).sort({name: 1}) # name 1 means sort ASC

$ db.companies.find({category_code: 'music'}, {name: 1, number_of_employees: 1}).sort({number_of_employees: 1}).limit(3)

# Return data on all music companies, sorted alphabetically from A to Z. Ensure consistent sort order
$ db.companies.find({ category_code: "music" }).sort({ name: 1, _id: 1 });

# Return the three music companies with the highest number of employees. Ensure consistent sort order.
$ db.companies
  .find({ category_code: "music" })
  .sort({ number_of_employees: -1, _id: 1 })
  .limit(3);
```
### Projection
```sh
$ db.inspections.find({sector: {$eq: 'test'}}, {business_name: 1, result: 1, _id: 0}) # inclusion method with exclusion for `_id`

# Return all inspections with result of "Pass" or "Warning" - exclude date and zip code
$ db.inspections.find(
  { result: { $in: ["Pass", "Warning"] } },
  { date: 0, "address.zip": 0 }
)
```

### Count documents
```sh
$ db.collections.countDocuments(<query>, <optiona>)

# Count number of trips over 120 minutes by subscribers
$ db.trips.countDocuments({ tripduration: { $gt: 120 }, usertype: "Subscriber" })
```

### Aggregation pipeline & staging
```sh
$ db.collection.aggregate([
    {
        $stage1: {
            { expression1 },
            { expression2 }...
        },
        $stage2: {
            { expression1 }...
        }
    }
])

$ db.zips.aggregate([
{   
   $match: { 
      state: "CA"
    }
},
{
   $group: {
      _id: "$city",
      totalZips: { $count : { } }
   }
}
])

# Sorts the documents in descending order, the documents with the greatest pop value appear first, and limits the output to only the first five documents after sorting
$ db.zips.aggregate([
{
  $sort: {
    pop: -1
  }
},
{
  $limit:  5
}
])

$ db.zips.aggregate([
{
   $project: {
      state:1, 
      zip:1,
      population:"$pop",
      _id:0
   }
}
])

$ db.zips.aggregate([
{
   $set: {
      place: {
         $concat:["$city",",","$state"]
      },
      pop:10000
   }
}
])

$ db.zips.aggregate([
{
   $count: "total_zips"
}
])

# $out is used to keep the result in a new collection
# if collection exists then it is overwritten
$ db.zips.aggregate([
{
  $sort: {
    pop: -1
  }
},
{
  $limit:  5
},
{
   $out: 'temp_collection'
}
])
```

## Indexes

```sh
$ db.customers.createIndex({email:1}, {unique:true}) # create an unique index for email field with ASC order and unique constraint

$ db.customers.getIndexes()

$ db.customers.explain().fetch({...}) # explaint the query, the most important field is "winningPlan"

$ db.customers.explain().find({
  birthdate: {
    $gt:ISODate("1995-08-01")
    }
  }).sort({
    email:1
    })

# Create a Single field Multikey Index
$ db.customers.createIndex({
  accounts: 1
})


# example of a query and a good index for it 
$ db.customers.find({
  birthdate: {
    $gte:ISODate("1977-01-01")
    },
    active:true
    }).sort({
      birthdate:-1, 
      name:1
      })
$ db.customers.createIndex({
  active:1, 
  birthdate:-1,
  name:1
})

# hide the index
$ db.collection.hideIndex('index_name') # on write the index will be used, on read will be ignored

# drop the index
$ db.collection.dropIndex('index_name')

# drop indexes
$ db.collection.dropIndexes([
  'index1name', 'index2name', 'index3name'
  ])
$ db.collection.dropIndexes()
```

### Transactions
```sh
$ const session = db.getMongo().startSession()

$ session.startTransaction()

$ const account = session.getDatabase('< add database name here>').getCollection('<add collection name here>')

# Add database operations like .updateOne() here
$ session.commitTransaction() # or session.abortTransaction()
```

### Query to get total sales for each customer in the year 2023
```js
db.orders.aggregate([
   {
      $match: {
         order_date: {
            $gte: ISODate("2022-01-01T00:00:00Z"),
            $lt: ISODate("2022-01-01T00:00:00Z")
         }
      }
   },
   {
      $unwind: "$items"
   },
   {
      $group: {
         _id: "$customer_id",
         total_sales: {
            $sum: "$items.price"
         }
      }
   }
])
```

### Query to retrieve all orders with corresponding customer information
```js
db.orders.aggregate([
   {
      $lookup: {
         from: "customers",
         localField: "customer_id",
         foreignField: "customer_id",
         as: "customer_info"
      }
   }
])
``` 

### Query to calculate the average of each product and to sort result in descending order of average rating
```js
db.reviews.aggregate([
   {
      $group: {
         _id: "$product_id",
         avgRating: {
            $avg: "$rating"
         }
      }
   },
   {
      $sort: {
         avgRating: -1
      }
   }
])
```

### Combine documents from `employees` and `departments`
```js
db.employees.aggregate([
   {
      $lookup: {
         from: "departments",
         localField: "department_id",
         foreignField: "_id",
         as: "department_info"
      }
   }
])
```

### Use projection for nested array
```js
db.developers.find({}, {tech_stack: {$slice: [0, 3]}}) // selects all developers and first 3 items from array
```

## What is MongoDB
It is a general purpose document database suitable for a wide range of use cases. 

## Benefits comparing to relational databases
- Easier to map data in the program comparing to relational database
- Flexible schema model
- Can be easily scaled horizontal and able to manage the application connections to the necessary shard(machine) 
- Collections can have TTL (time to live) for example for sessions

## Document keys
Example of document `{"name" : "Rick", "age" : 19}`
Keys constraints:
- UTF-8 characters
- `\0` is prohibited because it is used to indicate the end ot the key by mongo
- `$` and `.` are prohibited because mongo uses them internally
- Case Sensitive
- Duplicates aren't allowed

## Terms 
- **Document** - a basic unit of data, displayed in JSON, stored in BSON format (similar to table record in RDMS).
- **Collection** - a set of documents
- **Database** - container for collections
- **BSON** - format in which the documents are stored (binary json)
- **Aggregation** - collection and summary of data
  - **Stage** - one of the built-in methods that can be completed on the data, but does not permanently alter it
  - **Aggregation pipeline** - a series of stages completed on the data in order
- **Indexes** - data structures used to improve read performance but affects write performance
  - **Single field index** - A single field index is an index on a single field of a document. MongoDB creates a single field index on the _id field by default, but additional indexes may be needed for other fields as well. A single field index can also be a multikey index if it operates on an array field.
  - **Compound index** - A single index structure holds references to multiple fields within a collection's documents. A compound index is created by specifying the fields that the index should reference, followed by the order in which the fields should be sorted. The order of the fields in the index is important because it determines the order in which the documents are returned when querying the collection. A compound index can also be a multikey index if one of the fields is an array.
  - **Multikey index** - A multikey index is an index on an array field. Each element in the array gets an index key, which supports efficient querying against array fields. Both single field and compound indexes can have an array field, so there are both multikey single field indexes and multikey compound indexes.
- **Execution stages** - main elements of `explain()` plan are:
   - `IXSCAN` stage indicates the query is using an index and what index is being selected.
   - `COLLSCAN` stage indicates a collection scan is perform, not using any indexes.
   - `FETCH` stage indicates documents are being read from the collection.
   - `SORT` stage indicates documents are being sorted in memory.
- **ACID** - represents:
  - `Atomicity` - all operations will either succeed of fail together.
  - `Consistency` - all changes are consistent with database constraints
  - `Isolation` - multiple transactions can happen in the same time without affecting each other
  - `Durability` - all changes made by a transaction are persisted and will be present after failure and recovery process (guarantee that the data is saved in non-volatile memory)

## Data types
- All JSON data types
- Dates
- Numbers
- ObjectId

## _id
- Every document requires an `_id` field which acts as a primary key.
- Must be unique
- Can be of any type exept array
- If the document doesn't include the `_id` field then MongoDB automatically generates an `ObjectId` for the `_id` field. 
- Default type is `ObjectId` but can be used any other type
- 12 bytes 
  - first 4 bytes - seconds from beginning of the epoch
  - next 5 bytes - random value
  - next 3 bytes - counter that starts from the random value

Is used this approach to have a key generator for distributed database.


## Schema 
- Comparing to relational databases is more flexible because requires only updating of classes to include a new field.

## Optional Schema Validation Rules
- Set constraints of the structure of documents

## Polymorphic documents
- Means that collection can store different sort of documents

## Data Modeling
Data Modeling - process of defining how data is stored and relationships that exist among different entities in data.

## Key principle of data modeling with MongoDB
- Data that is accessed together should be stored together

## Relationship types
- One-To-One
- One-To-Many
- Many-To-Many

## Ways to model relationships 
- Embedding
- Referencing
- <img src="./forReadme/mongo2.png" alt="Image Description" height="300px" >

## Embedding data in document (Why is good?)
- Improves reading performance
- Improves edit operation because edit will affect only one document
- Avoid unnecessary joins
## Embedding data in document (Why is bad?)
- Is possible to have unbounded documents
- Unbounded documents may exceed the BSON document threshold of 16MBs

## Questions & Answers

1) What is unbounded document?
   - **Answer:** Unbounded means that document grows infinitely due to poor designed embedding relationship to other collection of elements, like "Article and Multiple Comments" 
2) How a valid connection string looks like? 
   - **Answer:**
      - `mongodb+srv://MDBUser:university01@mdb-training-cluster.swnn5.mongodb.net?retryWrites=true&w=majority`
      - `mongodb://root:root@localhost:27017/admin`
3) Which connection string is valid `"mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db"` or `"mongodb://test2:t%e)s$t2@localhost:27017/my_db"` ?
   - **Answer:** `"mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db"` because it should be a valid urlencoded string.
4) How many instances of `MongoClient` is recommended be created in a program?
   - **Answer:** Only one because it is expensive operation and it handles multiple connections inside the instance.
5) How to insert one element in a collection?
   - **Answer:** to call `db.<collectionName>.insertOne({<document>})`
6) How to insert multiple elements in a collection?
   - **Answer:** to call `db.<collectionName>.insertMany([{<document>}, {<document>}])`
7) What happens if I call `insertOne` or `insertMany` for nonexisting element?
   - **Answer:** the collection is created automatically
8) What methods allow to extract data from collection?
   - **Answer:** `db.collection.find(...)`
9) If I have next collection: `[{a: 22}, {a: [33, 22]}]` what will return `db.collection.find({a: { $eq: 22 }})`
   - **Answer:** `[{a: 22}, {a: [33, 22]}]`
10) If I have next collection: `[{a: 22}, {a: [33, 22]}]` what will return `db.collection.find({a: { $elemMatch: { $eq: 22 } }})`
   - **Answer:** `[{a: [33, 22]}]`
11) What is the problem with this query: `db.collection.find({$or: {...}, $or: {...}})` DON'T DO IN THAT WAY!!!
   - **Answer:** The second `$or` operator will override the first one
12) How does strings are sorted in MongoDB by default for instance for `.sort(name: 1)`
   - **Answer:** Sorted alphabetically A...Za...z
13) Projection: Are inclusion and exclusion allowed together?
   - **Answer:** No, the only one exception is inclusion with explicit exclusion of `_id` attribute
14) What does `$match` aggregation stage does?
   - **Answer:** Filter for documents matching criteria
15) What does `$group` aggregation stage does?
   - **Answer:** Create a signle document for each distinct document
16) What happens if you set the `$out` stage to output to a collection that already exists?
   - **Answer:** The existing collection is overwritten by the returning collection
17) What is the main purpose of an index?
   - **Answer:** For fast data retrieval
18) In what operations the indexes are used?
   - **Answer:** equality, range-based and sorting
19) What is the negative part of using indexes?
   - **Answer:** writing and updating cause degradation of performance due to restruction of the index 
20) How many multikey indexes can have a compound index?
   - **Answer:** the limit is only one multikey index per compound index
21) Do the order of fields in compound index matters?
   - **Answer:** Yes, the order of the fields matters when creating the index and the sort order. It is recommended to list the fields in the following order: Equality, Sort, and Range.
22) What will happen if a transaction runs more than 60 seconds?
   - **Answer:** It will be canceled due to mongodb limit of transaction time (60 seconds).
23) Are database operations that affect more than one document, like ``.updateMany()` atomic?
   - **Answer:** No, to achieve that, should be used a transaction 
24) Multi-document transaction guarantees that operations have ACID?
   - **Answer:** Yes
24) In which way Spring Data helps to interact with MongoDB?
   - **Answer:** It helps with mapping documents to java pojos
25) Enumerate all BSON numeric types:
   - **Answer:** 
     - Double
     - Int32
     - Int64
     - Decimal128
26) What method should be used to get all documents from a cursor using mongosh?
   - **Answer:** `db.collection.find().toArray();`
27) How should the 'autocomplete' index be defined to look for matches at the beginning of a word on the `name` field?
   - **Answer:** `{  "mappings": {    "dynamic": false,    "fields": {         "name": [   {  "type": "autocomplete",                              "tokenization": "edgeGram"} ]    } }}`
28) How the query for Atlas search index should look like?
   - **Answer:** `db.restaurants.aggregate([{    "$search": {      "text": { "path": "name", "query": "cuban"}    } }])`
29) How a valid query for reading from one source to other looks like?
   - **Answer:** `db.getSiblingDB("my_db").test99.aggregate([{ $out: {db:'my_db', coll:'test98'}} ])`
30) For the query `db.people.find({employer : "ABC" }).sort ({last_name:1 , job:1})` which index is most optimal?
   - **Answer:** 
     - `db.people.createIndex({employer:1, last_name : 1  , job : 1 } )`
     - ` db.people.createIndex({employer:1, last_name : -1  , job : -1 } )`
31) How can one document be found using Atlas Data Explorer?
   - **Answer:** Select the collection on the left-hand side. 2. Select the "Aggregation" view. 3. Specify the first stage as `$match` and ...
32) What are two advantages to using Connection Pooling within the Java Driver?
   - **Answer:** Reduce the latency for an application and limit the number of connections to the server
33) How does MongoDB sort a collection with both numeric and string values?
   - **Answer:** By lexicographical order e.g. [2, "10", "1"] -> ["1", "10", "2"]
34) How to insert multiple documents into a collection, to bypass validation and to continue processing event if an error occurs during insertion?
   - **Answer:** `db.col.insertMany([...], {validate: false, continueOfError: true})`
35) How to drop entire collection?
   - **Answer:** `db.col.drop()`
36) Where the mongodb configuration file is located?
   - **Answer:** `etc/mongod.conf`

## Java

### Theory
- `MongoClient` should be singleton because it encapsulates the logic of creating connection for each query.

### How to run POC java project
```sh
# to resolve dependencies 
$ ./mvnw dependency:resolve

# run java program
$ ./mvnw compile exec:java -Dexec.mainClass="ipostu.mongo.demo.part2.App" -Dmongodb.uri="mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db"
```

### Get all databases
```java
String connectionString = System.getProperty("mongodb.uri");
try (MongoClient mongoClient = MongoClients.create(connectionString)) {
    List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
    databases.forEach(db -> System.out.println(db.toJson()));
}
```

### Insert one document
```java
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
```

### Insert many documents
```java
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
```

### Find many / one
```java
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
```

### Update many / one
```java
// many
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query  = Filters.eq("account_type","type1");
Bson updates  = Updates.combine(Updates.set("minimum_balance",100));
UpdateResult upResult = collection.updateMany(query, updates);

//one
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query = Filters.eq("account_id", "MDB12234728");
Bson updates = Updates.combine(Updates.set("account_status", "active"), Updates.inc("balance", 100));
UpdateResult upResult = collection.updateOne(query, updates);
```

```java
//delete one 
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query = Filters.eq("account_holder", "john doe");
DeleteResult delResult = collection.deleteOne(query);

//delete many
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query = eq("account_status", "dormant");
DeleteResult delResult = collection.deleteMany(query);
```

```java
final MongoClient client = MongoClients.create(connectionString);
final ClientSession clientSession = client.startSession();

TransactionBody txnBody = new TransactionBody<String>(){
    public String execute() {
        MongoCollection<Document> bankingCollection = client.getDatabase("bank").getCollection("accounts");

        Bson fromAccount = eq("account_id", "MDB310054629");
        Bson withdrawal = Updates.inc("balance", -200);

        Bson toAccount = eq("account_id", "MDB643731035");
        Bson deposit = Updates.inc("balance", 200);

        bankingCollection.updateOne(clientSession, fromAccount, withdrawal);
        bankingCollection.updateOne(clientSession, toAccount, deposit);

        return "Transferred funds from John Doe to Mary Doe";
    }
};

try {
    clientSession.withTransaction(txnBody);
} catch (RuntimeException e){
    System.out.println(e);
}finally{
    clientSession.close();
}
```

### Aggregation in java
```java
private static void matchStage(MongoCollection<Document> accounts){
    Bson matchStage = Aggregates.match(Filters.eq("account_id", "MDB310054629"));
    System.out.println("Display aggregation results");
    accounts.aggregate(Arrays.asList(matchStage)).forEach(document->System.out.print(document.toJson()));
}

private static void matchAndGroupStages(MongoCollection<Document> accounts){
    Bson matchStage = Aggregates.match(Filters.eq("account_id", "MDB310054629"));
    Bson groupStage = Aggregates.group("$account_type", sum("total_balance", "$balance"), avg("average_balance", "$balance"));
    System.out.println("Display aggregation results");
    accounts.aggregate(Arrays.asList(matchStage, groupStage)).forEach(document->System.out.print(document.toJson()));
}


private static void matchSortAndProjectStages(MongoCollection<Document> accounts){
    Bson matchStage =
            Aggregates.match(Filters.and(Filters.gt("balance", 1500), Filters.eq("account_type", "checking")));
    Bson sortStage = Aggregates.sort(Sorts.orderBy(descending("balance")));
    Bson projectStage = Aggregates.project(Projections.fields(Projections.include("account_id", "account_type", "balance"), Projections.computed("euro_balance", new Document("$divide", asList("$balance", 1.20F))), Projections.excludeId()));
    System.out.println("Display aggregation results");
    accounts.aggregate(asList(matchStage,sortStage, projectStage)).forEach(document -> System.out.print(document.toJson()));
}
```

## Spring Boot & Spring Data 
```sh
<dependency>	
    <groupId>org.springframework.data</groupId>	
    <artifactId>spring-boot-starter-data-mongodb</artifactId>	
    <version>2.7.11</version>	
</dependency>

or

implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

```java
// Configuration bean
// Option 1
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
 
    @Override
    protected String getDatabaseName() {
        return "test";
    }
 
    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
 
    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("com.baeldung");
    }
}

// Option 2
@Configuration
public class MongoConfiguration {
    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString("mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "my_db");
    }
}
```

```java
// Model class
@Document("tvItem")
public class TVItem {

    @Id
    private String id;

    private String name;

    private int quantity;

    private String category;

    public TVItem(String id, String name, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }

    // getters and setters
}
```

```java
// Repository entity
public interface TVItemRepository extends MongoRepository<TVItem, String> {

    @Query("{name: '?0'}")
    TVItem findItemByName(String name);

    @Query(value = "{category: '?0'}", fields = "{'name': 1, 'quantity': 1}")
    List<TVItem> findAll(String category);

    long count();
}
```

```java
// Example of usage:
@EnableMongoRepositories("ipostu.mongo.demo.repository")
@SpringBootApplication
public class MongoDemoApplication implements CommandLineRunner {

    @Autowired
    private TVItemRepository tvItemRepository;

    public static void main(String[] args) {
        SpringApplication.run(MongoDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        tvItemRepository.deleteAll();
        String name1 = "test1" + random.nextInt(20);

        //create
        tvItemRepository.insert(new TVItem(null, name1, 21, "category1"));
        tvItemRepository.insert(new TVItem(null, "test2" + random.nextInt(20), 22, "category1"));
        tvItemRepository.insert(new TVItem(null, "test3" + random.nextInt(20), 23, "category2"));
        tvItemRepository.insert(new TVItem(null, "test4" + random.nextInt(20), 24, "category2"));

        var elements = tvItemRepository.findAll();

        //2
        var elementsForCategory2 = tvItemRepository.findAll("category2");
        //2
        var elementsForCategory1 = tvItemRepository.findAll("category1");

        var test1Element = tvItemRepository.findItemByName(name1);
        test1Element.setName("abc1");
        tvItemRepository.save(test1Element);

        long count = tvItemRepository.count();
        elementsForCategory1 = tvItemRepository.findAll("category1");
    }
}
```


## Exercise 1 
- Create a DB and 2 collections, insert 2 records and update 1 through MongoDB-Atlas
<img src="./forReadme/mongo1.png" alt="Image Description" >

## Exercise 2 
- Connect to local MongoDB using Compass GUI with connection string: `mongodb://root:root@localhost:27017/admin`
<img src="./forReadme/mongo3.png" alt="Image Description" >

# References:
- https://www.mongodb.com/docs/drivers/