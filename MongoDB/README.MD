# MongoDB

## Setup MongoDB Container
```bash
# to install a mongo client in isolated shell
$ nix-shell ./mongo-client.nix --run bash

# to remove recursively shared data folder if needed
$ docker-compose up --build -d
$ docker-compose stop
$ docker-compose down
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

## Data types
- All JSON data types
- Dates
- Numbers
- ObjectId

## _id
- Every document requires an `_id` field which acts as a primary key.
- Must be unique
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
Data Modeling - process of defining how data is stored and relationships that exist among different entities in data.\

## Key principle of data modeling with MongoDB
- Data that is accessed together should be stored together

## Relationship types
- One-To-One
- One-To-Many
- Many-To-Many

## Ways to model relationships 
- Embedding
- Referencing
<img src="./mongo2.png" alt="Image Description" >

## Embedding data in document (Why is good?)
- Improves reading performance
- Improves edit operation because edit will affect only one document
- Avoid unnecessary joins
## Embedding data in document (Why is bad?)
- Is possible to have unbounded documents
- Unbounded documents may exceed the BSON document threshold of 16MBs

## Questions & Answers

1) What is unbounded document?
    - Unbounded means that document grows infinitely due to poor designed embedding relationship to other collection of elements, like "Article and Multiple Comments" 
2) How a valid connection string looks like?
    - `mongodb+srv://MDBUser:university01@mdb-training-cluster.swnn5.mongodb.net?retryWrites=true&w=majority`
    - `mongodb://root:root@localhost:27017/admin`

## Java

### Theory
- `MongoClient` should be singleton because it encapsulates the logic of creating connection for each query.

### How to run POC java project
```sh
# to resolve dependencies 
$ ./mvnw dependency:resolve

# to run the program
$ ./mvnw compile exec:java -Dexec.mainClass="ipostu.mongo.demo.App" -Dmongodb.uri="mongodb://root:root@localhost:27017/admin"
```

### Get all databases
```java
String connectionString = System.getProperty("mongodb.uri");
try (MongoClient mongoClient = MongoClients.create(connectionString)) {
    List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
    databases.forEach(db -> System.out.println(db.toJson()));
}
```


## Exercise 1 
- Create a DB and 2 collections, insert 2 records and update 1 through MongoDB-Atlas
<img src="./mongo1.png" alt="Image Description" >

## Exercise 2 
- Connect to local MongoDB using Compass GUI with connection string: `mongodb://root:root@localhost:27017/admin`
<img src="./mongo3.png" alt="Image Description" >

# References:
- https://www.mongodb.com/docs/drivers/