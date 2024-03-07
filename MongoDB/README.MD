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
$ mongo "mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db"

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
$ db.collection.findAndModify({query:{...}, update:{...}, new:true|false(default), upsert:true|false(default)});
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
# or
$ db.collections.find().count() # deprecated

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
// local field is the field from `employees` collection
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

### Find total amount of orders made in month of February 2023
```js
db.orders.aggregate([
   {
      match: {
         $gte: ISODate("2023-02-01T00:00:000Z"),
         $lt: ISODate("2023-03-01T00:00:000Z")
      }
   },
   {
      $group: {
         _id: null,
         total_amount: {
            $sum: "$total_amount"
         }
      }
   }
])
```

### `$addFields` and `$add` example
```js
db.players.aggregate([
   {
      $addFields: {
         total_score: {
            $sum: "$scores"
         },
         avg_score: {
            $avg: "$scores"
         }
      }
   },
   {
      $addFields: {
         total_score_with_bonus: {
            $add: ["$total_score", "$bonus"]
         }
      }
   }
])
```

### Find all movies that have more votes than the year in which they were released
```js
// Example of document:
/*
{
   _id: ObjectId('573a1390f29313caabcd60e4'),
   genres: [ 'Comedy', 'Drama' ],
   title: 'The Immigrant',
   year: 1917,
   imdb: { rating: 7.1, votes: 4560, id: 7133 },
   countries: [ 'USA' ]
}
*/

db.movies.find({
   $expr: {
      $gt: ["$imdb.votes", "$year"]
   }
})
```

### Example of text search query
```js
db.movies.find(
   {text: {$search: 'Blah blah ...'}},
   {score: {$meta: 'textScore'}}
).sort({score: {$meta: 'textScore'}}).limit(3); // extract only first top 3 relevant scores
```

### Retrieve total sales for each customers
```js
db.orders.aggregate([
   {
      $unwind: "$items"
   },
   {
      $group: {
         _id: "$customer_name",
         total_sales: {
            $sum: {
               $multiply: ["$items.quantity", "$items.price"]
            }
         }
      }
   }
])
```

### Avoid fields with same name in `$lookup` stage
```js
db.orders.aggregate([
  {
    $lookup: {
      from: "customers",
      let: { orderCustomerId: "$customerId" }, // Alias for the field in the "orders" collection
      pipeline: [
        {
          $match: {
            $expr: {
              $eq: ["$customerId", "$$orderCustomerId"]
            }
          }
        },
        // Add more stages if needed for projecting or modifying the customer data
      ],
      as: "customerDetails"
    }
  }
]);
```
### Total quantity and total revenue for each month
```js
db.sales.aggregate([
   {
      $group: {
         _id: {
            month: {
               $month: "$sale_date"
            },
            year: {
               $year: "$sale_date"
            }
         },
         totalQuantity: {
            $sum: "$quantity_sold"
         },
         totalRevenue: {
            $sum: "$total_revenue"
         }
      }
   }
]);
```

### Attribute Pattern
```js
// Problem
/**
{
    title: "Star Wars",
    director: "George Lucas",
    release_US: ISODate("1977-05-20T01:00:00+01:00"),
    release_France: ISODate("1977-10-19T01:00:00+01:00"),
    release_Italy: ISODate("1977-10-20T01:00:00+01:00"),
    release_UK: ISODate("1977-12-27T01:00:00+01:00"),
}
Indexes
{release_US: 1}
{release_France: 1}
{release_Italy: 1}
 * /
```
```js
// Fix using Attribute pattern
/**
{
    title: "Star Wars",
    director: "George Lucas",
    …
    releases: [
        {
        location: "USA",
        date: ISODate("1977-05-20T01:00:00+01:00")
        },
        {
        location: "France",
        date: ISODate("1977-10-19T01:00:00+01:00")
        },
        {
        location: "Italy",
        date: ISODate("1977-10-20T01:00:00+01:00")
        },
        {
        location: "UK",
        date: ISODate("1977-12-27T01:00:00+01:00")
        },
    ],
}
Index
{ "releases.location": 1, "releases.date": 1}
*/
```

### Total quantity and total revenue for each month
```js
// update with positional operator
db.students.updateOne(
   { _id: ObjectId("5f0a7e80d8c9c7b5a48c49e1"), "courses.name": "Math" },
   { $set: { "courses.$.grade": 95 } }
)

db.students.updateOne(
   { _id: ObjectId("5f0a7e80d8c9c7b5a48c49e1") },
   { $set: { "courses.2.grade": 95 } }
)
```

### Get 10 random documents from a collection
```js
db.university_courses_enrolment.aggregate([{ "$sample": {size: 10} }])
```

### Example of query to return all books titles that end with 'ian'
```js
db.books.find({title: {$regex: /.*ian$/}})
```

### Average price of items purchased by each customer
```js
const collectionName = 'test_00091' + Math.floor(Math.random()*9999)
db.createCollection(collectionName);

db.getCollection(collectionName).insertMany([
    {
        "_id": ObjectId("5f5f95aae2e85f9e7b33be5a"),
        "customer_id": "C001",
        "item": "shirt",
        "price": 29.99,
        "quantity": 2,
        "date": ISODate("2022-03-01T08:00:00Z")
    },
    {
        "_id": ObjectId("5f5f95aae2e85f9e7b33be5b"),
        "customer_id": "C002",
        "item": "pants",
        "price": 39.99,
        "quantity": 1,
        "date": ISODate("2022-03-02T10:30:00Z")
    },
    {
        "_id": ObjectId("5f5f95aae2e85f9e7b33be5c"),
        "customer_id": "C003",
        "item": "shoes",
        "price": 59.99,
        "quantity": 1,
        "date": ISODate("2022-03-03T15:45:00Z")
    },
    {
        "_id": ObjectId("5f5f95aae2e85f9e7b33be5d"),
        "customer_id": "C001",
        "item": "hat",
        "price": 14.99,
        "quantity": 3,
        "date": ISODate("2022-03-04T12:20:00Z")
    }
]);

db.getCollection(collectionName).aggregate([{
   $group: {
      _id: "$customer_id", 
      avgPrice: {
         $avg: {
            $multiply: [
               "$price", "$quantity"
            ]
         }
      }
   }
}])

db.getCollection(collectionName).drop();
```

### 
```js
const collectionName = 'test_00091' + Math.floor(Math.random()*9999)

db.createCollection(collectionName);

db.getCollection(collectionName).insertMany([
    {
        "username": "user1",
        "email": "user1@example.com",
        "age": 25,
        "country": "USA"
    },
    {
        "username": "user2",
        "email": "user2@example.com",
        "age": 30,
        "country": "Canada"
    },
    {
        "username": "user3",
        "email": "user3@example.com",
        "age": 22,
        "country": "UK"
    },
    {
        "username": "user4",
        "email": "user4@example.com",
        "age": 28,
        "country": "Australia"
    }
]);

db.createView(collectionName+ "_view", collectionName,
[
   {
      $project: {
         "username": 1,
         "email": 1,
         "age": 1
      }
   }
]);
// or 
db.runCommand( { create: collectionName+"_view", viewOn: collectionName, pipeline: [
   {
      $project: {
         "username": 1,
         "email": 1,
         "age": 1
      }
   }
], /*collation: {...}*/ } )

db.getCollection(collectionName+"_view").find()

// MongoServerError: Namespace my_db.test_000917680_view is a view, not a collection
db.getCollection(collectionName+"_view").updateOne({username: 'user4'}, {$set: {age: 30}})

db.getCollection(collectionName+"_view").drop()

db.getCollection(collectionName).drop();
```

### Example of query to return all movies that include both genres 'Crime' and 'Mystery'
```js
db.movies.find( { genres: { $all: ["Crime", "Mystery"] } } )
```

### Create a capped collection
```js
db.createCollection('latest_news', {'capped': true, 'size': 10000, 'max': 3})
```

### Use first element of inner array
```js
db.sales.find( { 'items.tags.0': 'office' } )
```

### Find documents with exactly 5 languages (5 items in an array)
```js
db.movies.find( { languages: { $size: 5 } } )
```

### Create 2dsphere geospatial index
```js
db.shipwrecks.createIndex({'coordinates': '2dsphere'})
```

### Explain the query
```js
db.companies.insertMany([
   {"_id": 1, "name": "Facebook"}, 
   {"_id": 1, "name": "Twitter"}, 
   {"_id": 2, "name": "Tesla"},
   {"_id": 3, "name": "Amazon"}
], {"ordered": false})

// This insert is unordered
// MongoDB will insert each document with a unique _id value into the collection
// Three documents will be inserted into the collection. The documents with _id values 1 (the first one with "Facebook"), 2, and 3 will be successfully inserted. The second document with _id 1 ("Twitter") will cause a duplicate key error and will not be inserted because the operation is unordered and it allows the remaining documents to be inserted even after encountering an error.
```

### $unset example
```js
db.users.update(
   { "_id": ObjectId("5f88255ab9a5b85a3c86f61a") },
   { $unset: { "age": "" } }
)

// After running this operation, the document will be modified as follows:
{
   "_id": ObjectId("5f88255ab9a5b85a3c86f61a"),
   "username": "john_doe",
   "email": "john@example.com"
}
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

## Tips and Tricks
1. In a query which filter by indexed fields and in projection is specified exactly these fields (indexed), then it will work faster because it will extract data from index structure. Example of index `{title: 1, year: 1}` query: `db.movies.find({title: {$in: ['a', 'b']}}, {_id: 0, title: 1, year: 1})`
2. MongoDB can use more than one index to optimize query.

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
- **Change Stream** - feature which allows applications to subscribe to all data changes on a single collection or database and immediately react to them.
- **Read concerns** - a.k.a. isolation levels on read (`db.collection.find().readConcern(<level>)`)
  - `local` - is used as default on read, the operation returns most recent data available on the node, ignoring the state of this data on other nodes in the replica set
  - `available` - works exactly as `local` but optimized for sharded cluster
  - `majority` - guarantee that the data has been acknowledged by a majority of the replica set members
  - `linearizable` - guarantee that the data has been acknowledged by a majority of the replica set members, waits until write operation finishes
  - `snapshot` - guarantee that the data has been acknowledged by a majority of the replica set members, waits until write operations with `majority` concern finishes (provide session consistency)
- **Computed pattern** - In MongoDB, a computed pattern refers to the use of aggregation pipelines or other mechanisms to derive and generate calculated or computed fields within a document or a result set based on specified criteria or expressions. It involves the dynamic creation or transformation of data based on predefined rules or computations.
- **The Outlier Pattern** - involves handling exceptional cases or outliers that don't fit within the standard data model.
- **Schema Versioning Pattern** - is typically used when making changes to the structure of the data model while maintaining backward compatibility with existing data. 
- **Cardinality** - uniqueness of the values in a column that is being indexed
  - `high` - values almost are unique
  - `low` - a lot of repeated values


## Data types
- All JSON data types
- Dates
- Numbers
- ObjectId

## _id
- Every document requires an `_id` field which acts as a primary key.
- Must be unique
- Can be of any type except array
- If the document doesn't include the `_id` field then MongoDB automatically generates an `ObjectId` for the `_id` field. 
- Default type is `ObjectId` but can be used any other type
- 12 bytes `(timestamp + (machine identifier + process identifier) + counter)`
  - first 4 bytes - seconds since the Unix epoch
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
37) Which approach should be used for time-intensive aggregation read to avoid high load on the primary database?
   - **Answer:** To use secondary read preference for aggregation, to offload operation to a secondary member of the replica set. IMPORTANT!!! operation will have some delay in data freshness.
38) What does `$group` stage in aggregation pipeline do?
   - **Answer:** It creates distinct groups based on the unique values, it allows to apply aggregation operation for each group
39) What does `db.<...coll>.insertMany([...])` return?
   - **Answer:** `{ acknowledged: true, insertedIds: { '0': ObjectId("659b0df1970235d545291683"), ... } }`
40) What is the most efficient way to retrieve documents based on a text search on several fields?
   - **Answer:** 
     - Create a text index on the fields
     - Use `$text` operator to perform search
41) How to force MongoDB to use a specific index or full collection scan or reverse collection scan? 
   - **Answer:** 
     - To use index: `db.users.find().hint( { age: 1 }` or `db.users.find().hint( "age_1" )`
     - To perform full collection scan: `db.users.find().hint( { $natural : 1 } )`
     - To perform reversed collection scan: `db.users.find().hint( { $natural : -1 } )`
42) What kind of tree data structure does MongoDB use for simple indexes?
   - **Answer:** BTree
43) What is the main purpose of the `$where` operator?
   - **Answer:** To filter query using a js function
     - Ignores indexes
     - Introduce security wholes
     - Can be used only if scripting is enabled
44) What does `mongorestore` command do?
   - **Answer:** 
     - Restore data from binary backup created by `mongodump`
     - To add a collection from BSON file to MongoDB cluster
45) What is the difference between `findAndModify` query with and without `{new: true}` option? 
   - **Answer:** 
     - without or `{new: false}` returns original document before update
     - `{new: true}` returns modified document
46) What does `$unwind` stage in aggregation pipeline do?
   - **Answer:** It deconstructs nested array, creating a separate document for each item
47) How does MongoDB handle empty values on `sort()` stage?
   - **Answer:** Empty values are considered as `null` and they are sorted last, regardless of the sort direction ASC or DESC.
48) In which case should be used `Attribute Pattern`?
   - **Answer:** When some fields have similar characteristics and there is a requirement to search across those fields.
49) What is the RAM limit of aggregation pipeline stage?
   - **Answer:** 100MB, it means that the memory consumption for each stage should not exceed this limit, or else it may throw an Error.
50) `$sortByCount` is equivalent to? DESC is important!!!
   - **Answer:** `$group` + `$sort`
     - `{ $sortByCount:  <expression> }`
     - `    { $group: { _id: <expression>, count: { $sum: 1 } } }, { $sort: { count: -1 } }`
51) What mechanism can be used to export data from one collection to another?
   - **Answer:** 
     - In the same database: `$out` aggregation stage
     - In different database: `mongoexport` and `mongoimport`
52) Can `insertMany` specify explicitly to insert ordered or unordered records?
   - **Answer:** Yes, with `ordered` flag
53) Which is the default value for the flag `ordered` for `insertMany` operation?
   - **Answer:** `true`
54) What is the limit of indexes that mongo can use for a query?
   - **Answer:** For a query mongo can't use more than one index
55) In which cases are used hash indexes?
   - **Answer:** 
     - Hash indexes are used for hash based sharding
     - Beneficial for equality operation
     - NOT for queries on multiple fields
     - DO NOT support range queries
56) What happen if capped collection reach its maximum size?
   - **Answer:** It removes the oldest documents.
57) Can documents in capped collection be deleted manually?
   - **Answer:** No, manual deletion is prohibited.
58) Can the size of capped collection be changed?
   - **Answer:** No, it is defined only once on creation.
59) Can a capped collection be sharded?
   - **Answer:** No, it can't be sharded.
60) Enumerate main advantages of horizontal scaling?
   - **Answer:** 
     - Is cheaper than scaling vertically (adding a new machine to the cluster is generally cheaper than buying a more powerful hardware)
     - Horizontally scaling cost increases linearly
61) Can find using regex use an index to improve performance?
   - **Answer:** Yes
62) How to dump a collection to BSON file?
   - **Answer:** `mongodump --db dbName --collection collName`
63) Which command is used to export/import data to CSV or JSON?
   - **Answer:** `mongoexport` and `mongoimport`
64) How does MongoDB handle sorting on fields with arrays as values in a collection ?
   - **Answer:** By the first element, if the first element are of different type, then it will be sorted by lexicographical order.
65) What is the difference between sparse and non-sparse index?
   - **Answer:** 
     - `sparse` - `db.university_courses.createIndex({entryType: 1}, {sparse: true})` documents for that the indexed field(s) is not present are ignored
     - `non-sparse` - by default, all documents are indexed
66) How is calculated namespace value for a collection?
   - **Answer:** `db_name.collection_name`
67) When sharding a MongoDB collection, what is the main factor that determines the distribution of data across shards?
   - **Answer:** The value of shard key
68) What data structure is used to store the newly created index?
   - **Answer:** B-Tree
69) In which cases should be used The Bucket Pattern?
   - **Answer:** 
     - To split a big documents into sub-documents.
     - For hierarchical structures
70) In a MongoDB database with referenced relationships, what is the advantage of using a separate collection for each entity over using a single collection for all entities?
   - **Answer:** Improved maintainability and reduced complexity of updates to related entities
71) What happens to the index entries after `db.collection.deleteMany()`?
   - **Answer:** They are immediately removed from the index
72) What is the difference between the `db.collection.deleteOne()` and `db.collection.deleteMany()` methods in MongoDB when it comes to document removal from a replica set?
   - **Answer:** `deleteOne()` removes the document from all nodes in the replica set, while `deleteMany()` removes the document from the primary node only
73) In MongoDB, what is the best practice for performing maintenance tasks such as backup and index rebuilds?
   - **Answer:** Use a replica set and perform the maintenance tasks on a secondary node
74) Which of the following is considered an anti-pattern in MongoDB data modeling for real-time data processing applications?
   - **Answer:** Over-normalizing data to reduce the chances of data inconsistencies
75) Which of the following is considered an anti-pattern in MongoDB data modeling for hierarchical data structures?
   - **Answer:** Using nested arrays to model hierarchical data due to risk of of exceeding the maximum document size
76) How does the db.collection.deleteMany() method in MongoDB handle document references in other collections when deleting documents from a sharded collection?
   - **Answer:** It raises an error if any references to the deleted documents exist in other collections
77) How does using a MongoDB replica set improve read performance and what needs to be considered?
   - **Answer:** A MongoDB replica set improves read performance by enabling read operations to be distributed across multiple secondary nodes. However, read operations on secondary nodes may return stale data.
78) Suppose we have a `products` collection what is the best shard key field?
   - **Answer:** `product_code` as the shard key can be a good option because it has unique values for each product. Using a shard key with high cardinality, such as `product_code`, can distribute the data evenly across shards, minimizing hotspots and ensuring efficient query performance.
79) How to get execution statistics for the query plan using `explain()` statement? 
   - **Answer:** Example `db.accounts.find().sort({account_id: -1}).explain("executionStats")`
80) Is `db.accounts.find().sort({account_id: -1}).explain("queryPlanner")` same as `db.accounts.find().sort({account_id: -1}).explain()` ? 
   - **Answer:** Yes
81) Which read preference options may not result in out-of-date data?
   - **Answer:** `primary`
82) What does the statement `rs.add(node3, {priority: 0, hidden: true, "bulidIndexes": false})` do?
   - **Answer:** 
     - `priority: 0` means the node won't participate in elections for primary node
     - `hidden: true` makes the node hidden, clients can't connect to them
     - `"bulidIndexes": false` disabling index replication
83) The find statement `db.movies.find( { genres: "Drama" } ).sort( { title: 1 } )` can use the index `{ title: 1 }`?
   - **Answer:** Yes
84) What is the process for adding a new member to an existing MongoDB replica set?
   - **Answer:** Connect to the primary member, add the new member to the replica set configuration, initiate a resync on the new member, and wait for the new member to become a secondary.
85) How can be launched mongod instance with a specific configuration file?
   - **Answer:** 
     - `mongod -f blah.conf`
     - `mongod --config blah.conf`
86) How to stop the operation with the `opid` = 14252?
   - **Answer:** `db.killOp(14252)`
87) Where is collection metadata stored in a sharded cluster?
   - **Answer:** On the configuration servers.
88) Which of the following options represents the default configurations for the MongoDB mongod process?
   - **Answer:** Default port: 27017, Authentication enabled: true, WiredTiger storage engine, Journaling enabled
89) What is the purpose of the mongos process in a MongoDB sharded cluster, and how does it handle write operations?
   - **Answer:** The `mongos` process is responsible for routing both read and write operations to the appropriate shards in the cluster, and it uses a write concern mechanism to ensure data durability.
90) What is the purpose of a covered query in a MongoDB sharded cluster?
   - **Answer:** To ensure that the query can be executed entirely within a single shard
91) What is the impact of choosing a monotonically increasing shard key for a MongoDB sharded cluster that experiences a high write volume?
   - **Answer:** It increases the risk of hot spotting.
92) Which of the following roles provides minimal privileges needed for backing up data in MongoDB?
   - **Answer:** `backup`
93) What is the role of the mongod process in MongoDB?
   - **Answer:** The `mongod` process is responsible for managing client connections and executing database operations .
94) What is the correct process for upgrading a MongoDB replica set to a new version of MongoDB?
   - **Answer:** Upgrade the secondary members one at a time, wait for the upgraded members to catch up to the primary, and then upgrade the primary member. 
95) What is the proper process for migrating a MongoDB sharded cluster to a new set of servers?
   - **Answer:** Use the migration tool provided by MongoDB to transfer the data and configuration from the current sharded cluster to the new sharded cluster.
96) What command should you use to check the status of the replica set?
   - **Answer:** `rs.status()`
97) What is the impact of choosing a shard key that has a low cardinality for a MongoDB sharded cluster that experiences a high write volume?
   - **Answer:** It increases the risk of hot-spotting.
98) Which of the following actions are granted to the built-in userAdmin role?
   - **Answer:** 
     - `dropUser`, `createUser`, `dropRole`, `createRole`
99) How to shutdown the server?
   - **Answer:** 
     - `use admin;`
     - `db.shutdownServer()`
100) In MongoDB's sharding architecture, what factors should be considered when choosing the primary shard for a newly created sharded collection?
   - **Answer:** 
     - The available disk space on each shard
     - The read and write capacity of each shard
     - The existing distribution of data across shards
101) How to display detailed information about the `accounts` collection (number of documents, collection size, number of indexes)
   - **Answer:** `db.accounts.stats()`
102) What is the role of an arbiter in a MongoDB replica set?
   - **Answer:** 
     - To facilitate elections for primary node selection
     - To increase the number of voting members in a replica set
103) When using the `explain()` method to obtain a query plan for a query, what does the `"winningPlan"` field indicate in the output of `explain()`?
   - **Answer:** The lowest cost query plan considered by the query optimizer
104) What should you take into account when call `    mongod --port 35000 --dbpath /home/user/mongodb/data --fork` ?
   - **Answer:** to specify `--logpath`. When starting a MongoDB instance using the `--fork` option, the `--logpath` option must also be specified. The `--fork` option allows the process to run in the background, and the `--logpath` option specifies the path to the log file where the mongod process will write its output. Without a `--logpath`, the `--fork` operation does not know where to redirect its output, hence the mongod instance will not start.
105) What is the role of the oplog in MongoDB Replica Sets?
   - **Answer:** It is a capped collection that stores all write operations performed on the primary node, allowing secondaries to stay up-to-date with the primary. 
106) In a MongoDB replica set, what is the purpose of the arbiter?
   - **Answer:** The arbiter acts as a tiebreaker during the election of a new primary replica set member
107) How to specify path to db in config file?
   - **Answer:** `storage: dbPath: /var/mongodb/db`
108) At what level in MongoDB, the WiredTiger storage engine provides concurrency?
   - **Answer:** Document level concurrency because it uses an approach called MultiVersion Concurrency Control (MVCC) to handle multiple requests to the same document.
109) In MongoDB, how does the query optimizer determine which index to use for a query that matches multiple indexes?
   - **Answer:** The query optimizer uses the index with the most selective conditions. 
110) What is the role of the `replication oplog` in the context of MongoDB replication?
   - **Answer:** The `replication oplog` is used to track changes to the data in the primary node, allowing the secondary nodes to stay in sync.
111) What is the minimum size for the `oplog` of a newly added member in order for it to successfully sync with the primary?
   - **Answer:**  In order for a new member to successfully sync with the primary, it needs to have an `oplog` that is at least as large as the largest oplog of any existing secondary
112) What is the purpose of the --replSet option in the context of MongoDB server administration?
   - **Answer:** The `--replSet` option is used to configure the MongoDB server to create a new replica set as the primary member.
113) How to create the initial database users in MongoDB while authentication is enabled?
   - **Answer:** 
     - Connect to MongoDB via localhost.
     - Switch to `admin` database.
     - Create a user administrator with the role `userAdminAnyDatabase`
     - Authenticate as the new user administrator.
     - Create additional users as the user administrator.
114) How does the WiredTiger storage engine in MongoDB manage disk space utilization?
   - **Answer:** By using a dynamic approach to disk space allocation.
115) What is the proper process for rotating the SSL/TLS certificates on a MongoDB server?
   - **Answer:** Stop the mongod process, replace the SSL/TLS certificates on the server, and then restart the mongod process using the `--tlsCAFile` option to specify the location of the new CA certificate. 
116) Which of the following best describes the purpose of the "queryPlanner" stage in a MongoDB query plan?
   - **Answer:** It generates a list of candidate query plans and calculates the cost of each plan.
117) How to manage Databases, Collections, and Documents in Atlas Data Explorer?
   - **Answer:** 
     - From the left panel in Atlas Data Explorer, click "Databases."
     - Click "Browse Collections."
     - Click "Create Database."
     - Enter a database name and a collection name, and then click the Create button.
     - To insert a document, select the collection where you want to insert it and then click "Insert Document."
118) How to configure MongoDB to enforce authentication by adding the correct configuration file option
   - **Answer:** `net: authorization: enabled`
119) When should you use the `mongos` process in MongoDB server administration?
   - **Answer:** MongoDB uses a sharded cluster architecture to horizontally scale data storage and processing across multiple servers. In a sharded cluster, data is partitioned across multiple shards, each of which is a standalone replica set.
     - The `mongos` process acts as a routing service for clients connecting to a sharded cluster, directing read and write operations to the appropriate shard based on the shard key. The mongos process does not store data, but instead acts as a coordinator for the sharded cluster. When connecting to a sharded cluster, clients should connect to the `mongos` instances rather than directly to the individual shards. This enables the `mongos` process to direct operations to the correct shard, balance the load across the cluster, and ensure data consistency. It's important to note that the `mongos` process should only be used in a sharded cluster configuration, and not in a standalone or replica set configuration. Additionally, the `mongos` process should be deployed in a highly available configuration, with multiple instances running in different network locations, to ensure high availability and fault tolerance.
120)  What should be done in case if one of the shards is experiencing high disk usage, which is causing performance issues for the cluster?
   - **Answer:** Migrate the data from the affected shard to a new shard with more disk space
121) What should be done in case if primary member of one of the replica sets has run out of disk space
   - **Answer:** Remove the primary member from the replica set and add a new member in its place.
122) What is the recommended approach for handling slow queries in a MongoDB deployment?
   - **Answer:** By using the MongoDB profiler to identify slow queries and optimize the schema.
123) What is the impact of choosing a compound shard key for a MongoDB sharded cluster?
   - **Answer:** It allows for more fine-grained control over the data distribution.
124) What is the preferred method for managing disk space usage in a MongoDB deployment with multiple replicas?
   - **Answer:** Enabling the use of disk quotas in the operating system to limit the disk space usage of each replica set member
125) What is replication lag?
   - **Answer:** a delay between an operation on the primary and the application of that operation from the oplog to the secondary
126) What is the purpose of the mongos process in a MongoDB sharded cluster?
   - **Answer:** To provide a centralized query interface for clients connecting to the sharded cluster
127) Which MongoDB role is specifically designed to provide the ability to monitor and retrieve server statistics, to access the system profile collection and to view the server's list of databases and their statistics?
   - **Answer:** `clusterMonitor` role
128) What is the impact of choosing a shard key that has low write activity for a MongoDB sharded cluster that experiences a high read volume?
   - **Answer:** It improves the overall read performance of the cluster.
129) What is the best way to ensure data consistency and availability in a MongoDB deployment that spans multiple geographic locations?
   - **Answer:** By using a MongoDB sharded cluster with multiple replica sets
130) How does MongoDB store information about the location of documents in the cluster?
   - **Answer:** MongoDB groups documents into chunks, which are broken down by a shard key. MongoDB doesn't store information about the location of each document, only information about the range of keys stored on each shard.
131) How to display detailed information about the currently used database (number of collections, database size, number of indexes)?
   - **Answer:** `db.stats()`
132) What are targeted queries in MongoDB?
   - **Answer:** Queries that are sent to a single shard or a selected subset of shards based on the shard key.
133) What is the name of a special global variable designed to work with sharding in MongoDB?
   - **Answer:** `sh`
134) How can you optimize the performance of a MongoDB deployment that experiences high read and write load?
   - **Answer:** By enabling the use of in-memory storage for frequently accessed data
135) How does the `mongos` process handle read operations in a MongoDB sharded cluster, and what factors can impact its performance?
   - **Answer:** The `mongos` process routes read operations to multiple shards in the cluster based on the distribution of the data, and its performance can be impacted by the number of shards, the distribution of data across the shards, and the load on the shards being queried.
136) MongoDB deployment that consists of a replica set with three members: primary, secondary, and arbiter. The primary member experiences a hardware failure and becomes unavailable. Which of the following steps should be taken to restore the replica set to a fully functional state?
   - **Answer:** Promoting the secondary member to become the new primary
137) What could be the cause of increased number of page faults?
   - **Answer:** The database server has too little RAM
138) What set of commands should be run to set up a replica set and add 2 other nodes?
   - **Answer:** `rs.initiate(); rs.add(node2); rs.add(node3);`
139) Which tool can be used for monitoring the health and performance of the database?
   - **Answer:** Ops Manager is a comprehensive tool specifically designed for managing and monitoring MongoDB at scale. It provides real-time reporting, custom alerts, and metrics that give insights into performance and help you identify potential issues before they become problems.
140) The application requires a high level of durability to ensure data integrity. 
   - **Answer:** Enabling journaling with the write concern set to "majority"
141) What command should be run on the primary before restarting it?
   - **Answer:** `rs.stepDown()` to make it secondary
142) Which of the following built-in roles provides the greatest cluster management access?
   - **Answer:** `clusterAdmin`
143) How can be configured the behavior of the `mongos` process in a MongoDB sharded cluster when it comes to handling read requests?
   - **Answer:** By setting the `readPreference` configuration option
144) In a MongoDB database with referenced relationships, what is the advantage of using a separate collection for each entity over using a single collection for all entities?
   - **Answer:** Improved maintainability and reduced complexity of updates to related entities.
145) In a sharded MongoDB cluster, what considerations should be taken into account when selecting a shard key?
   - **Answer:** 
     - The shard key should distribute the data evenly across the shards.
     - The shard key should be immutable to ensure consistent data distribution.
     - The shard key should have a high cardinality to avoid hotspots.
     - The shard key should be a single field rather than a compound key (recommended)
146) What is the built-in database called config in MongoDB for?
   - **Answer:** The config database is used to store information about shards in shared MongoDB cluster. 
147) What role does the `mongos` process play in a MongoDB sharded cluster, and how does it ensure data consistency between shards?
   - **Answer:** The `mongos` process is responsible for routing both read and write operations to the appropriate shards in the cluster, and it ensures data consistency between shards by using a distributed lock mechanism.
148) How does indexes work for values of varying data types?
   - **Answer:** Values are ordered by data type, then by value.
149) What is an arbiter in replica set?
   - **Answer:** Special type of replica set member. An arbiter doesn't have a copy of the data set and can't become a primary but it participates in elections for primary.
150) What is the role of the `userAdminAnyDatabase` role in MongoDB?
   - **Answer:** It allows a user to perform administrative tasks on any database in the deployment.
151) What is the purpose of the nojournal option in the context of MongoDB server administration?
   - **Answer:** The nojournal option is used to configure the MongoDB server to not use journaling for write operations
152) Which built-in role is specifically designed to grant a user the privileges needed to create and modify the database's design, but does not permit any data modification actions on the existing data?
   - **Answer:** `dbAdmin`
153) In cases of connectivity issues with a MongoDB deployment. Which steps should be taken to troubleshoot and resolve the problem?
   - **Answer:** 
     - Check the network connectivity between the client and the MongoDB server.
     - Verify that the MongoDB server is running and accessible.
     - Review the firewall configuration to ensure it allows traffic on the MongoDB port.
154) Which of the following tasks can help improve the performance and efficiency of the database?
   - **Answer:** 
     - Compact the database files.
     - Create compound indexes. 
     - Utilize the WiredTiger storage engine. 
155) Which of the following commands are valid to start a `mongod`?
   - **Answer:** 
     - `mongod --logpath /var/log/mongo/mongod.log --dbpath /data/db --fork `
     - `mongod -f /etc/mongod.conf`
156) How to use `mongodump` utility?
   - **Answer:** `mongodump --db yourDB --out /backup`
157) In MongoDB, what is the purpose of the WiredTiger storage engine and how does it differ from the MMAPv1 storage engine?
   - **Answer:** The `WiredTiger` storage engine is primarily focused on data durability and supports journaling for crash recovery. It differs from the `MMAPv1` storage engine by utilizing a copy-on-write mechanism for data modifications.
158) What is the purpose of the MongoDB key file in a replica set deployment?
   - **Answer:** To secure the replica set election process.
159) How to specify `keyfile`?
   - **Answer:** 
```
       security:
         keyFile: /var/mongodb/pki/node1/keyfile
```
161)   Which method should you use to reconfigure a replica set by applying a new replica set configuration?
   - **Answer:** `rs.reconfig()`
162)   How is data distributed across shards in a sharded MongoDB cluster?
   - **Answer:** By hashed index
163)   Configuration that you should choose for your production MongoDB cluster to ensure that your systems are highly available.
   - **Answer:** 
```
- deploy config servers as a 3 member replica set

- deploy each shard as a 3 member replica set

- deploy one or more mongos routers
```
163) Which method should you use to display the replica set configuration?
   - **Answer:** `rs.conf()`
164) What operation is required before sharding a collection by running the `sh.shardCollection()` command in the mongo shell?
   - **Answer:** create an index on the shard key
165) What method from the config API allows you to set a configuration option in mongosh?
   - **Answer:** E.g. `config.set('enableTelemetry', false)`
166) Select advantages of using sharding in MongoDB?
   - **Answer:** 
     - Increase storage capacity
     - Increase read/write throughput
167) What method is used to change databases within a script by using the load() method in mongosh?
   - **Answer:** `db.getSiblingDB()`
168) Which of the following statements describe a B tree?
   - **Answer:** 
     -  B trees sort the stored data in ascending sequential order from left to right
     -  Nodes in a B tree can have more than two child nodes
     -  A B tree is a self-balancing tree data structure
169) What arguments `.explain()` can have?
   - **Answer:** 
     - `queryPlanner` - default, it includes `queryPlan`, `winningPlan` and `rejectedPlans`
     - `executionStats` shows how many documents were examined during query execution, also includes `queryPlanner` result
     - `allPlansExecution` shows  `queryPlanner`, `executionStats` and all possible plans with related score
170) Which of the following fields can help us determine the effectiveness of an index?
   - **Answer:** 
     - ` nReturned`
     - ` totalDocsExamined`
     - ` executionStages`
     - ` totalKeysExamined`
171) What will happen if `...find().sort()` operates with a data set bigger than 100MB?
   - **Answer:** It will fail, to fix this should be use `...find().sort().allowDiskUse()`
172) The SORT stage will be present in the executionStages object of the explain('executionStats') output if a blocking (in-memory) sort took place.
   - **Answer:** The presence of the SORT stage in the executionStages object of the explain('executionStats') output means that MongoDB had to sort the documents in memory, which can be computationally expensive.
173) How to make an wildcard index for polymorphic field `product_attributes`?
   - **Answer:** `db.products.createIndex({ "product_attributes.$**" : 1 })`
174) What benefits introduce wildcard index?
   - **Answer:** Wildcard indexes can support queries against any field, even if that field is unknown at the time of querying
175) How to create a partial index?
   - **Answer:** 
```
db.zips.createIndex(
  { state: 1 },
  { partialFilterExpression: { pop: { $gte: 10000 } } }
)
```
176)   How does a clustered index in MongoDB differ from a regular index?
   - **Answer:** 
     -  Clustered indexes arrange documents in order based on their index key.
     -  Clustered indexes store the index key alongside the documents themselves.
     -  Clustered index keys eliminate the need for an additional TTL (time to live) index.
177)   What is the correct definition of a time series collection?
   - **Answer:** Time series collections efficiently store time series data. In time series collections, writes are organized so that data from the same source is stored alongside other data points from a similar point in time.
178)   What happens when the database profiler is enabled on a database?
   - **Answer:** Operations are captured and recorded inside the database under a capped collection named system.profile
179)   What will the following command return? `db.customers.aggregate([{ $indexStats: {} }]);`
   - **Answer:**  An array of documents, each representing an index specification document.
180)   What is the minimum privilege you need to download logs from an Atlas cluster?
   - **Answer:** Project Data Access Read Only
181)   Which of the following statements are true regarding the slowms property?
   - **Answer:** 
     - The slowms property defines the maximum amount of time for an operation to complete before it’s considered slow.
     -  The default value for the slowms property is set to 100 milliseconds.
182)   How to rotate logs for a self-managed mongod instance?
   - **Answer:** 
     - db.adminCommand( { logRotate : 1 } )
     - sudo kill -SIGUSR1 $(pidof mongod)
183)   In MongoDB, you are responsible for monitoring the health and performance of your database. Which of the following tools would be the most appropriate to use for real-time performance monitoring?
   - **Answer:** MongoDB Ops Manager
184)   How does WiredTiger handle index updates in MongoDB?
   - **Answer:** By updating the index in place.
185)   What set of commands do you need to run to set up a replica set and add 2 other nodes?
   - **Answer:** `rs.initiate(); rs.add(node2); rs.add(node3);`
186)   How to use wildcard index for whole collection? 
   - **Answer:** 
```sh
db.products.createIndex(
  { "$**": 1 },
  { wildcardProjection: { _id: 1, stock: 0, prices: 1 } } # 0 to, exclude 1 to include
)
```
187)   How to create compound index with wildcard?
   - **Answer:** 
```sh
db.products.createIndex({
  stock: 1,
  "product_attributes.$**" : 1 # only one wildcard per compound is allowed
})
```
188)   Characteristics of wildcard indexes?
   - **Answer:** 
     - Can be one or more per collection
     - Different indexes can caver same field
     - Cannot use TTL
     - Cannot be unique 
189)   Can `_id` be used as a partial index?
   - **Answer:** No, because MongoDB uses it for its internal operations
190)   Can sparse index be unique?
   - **Answer:** Yes, but it doesn't apply to documents that omit the indexed field.
191)   Explain what cluster index is?
   - **Answer:** 
     - A mechanism which makes the collection clustered
       - Stores documents in cluster index order
       - Stores clustered index key alongside the document
     - Can only be created when cluster collection is built
192) What benefits introduce cluster index?
   - **Answer:** 
     - Improve CRUD performance
     - Reduces disk usage (because there is not additional BTree created like in non-cluster indexes)
     - Reduces I/O (because data and index are stored together)
     - Improves memory cache usage
     - Improved TTL performance when configured as a TTL index 
193)   What limitations has cluster index?
   - **Answer:** 
     - Can be created only when clustered collection is created
     - Only one cluster index per cluster collection
     - Cluster index is not automatically used by `queryPlanner` when the secondary eligible index exists
     - Can't be created on `caped` collection
     - Cant' be hidden
194)   What is time series collection?
   - **Answer:** 
     - Collection that is clustered base on time-series
195)   What is time-series data?
   - **Answer:** any kind of data that changes over time
196)   Which are main 3 components that consist time-series?
   - **Answer:** 
     - Time 
     - Metadata
     - Measurements
197)   How to create a time-series collection?
   - **Answer:** 
```js
db.createCollection('weather', {
   timeseries: {
      timeField: 'timestamp', // required/ field of collection of type ISODate
      metaField: 'metadata',
      granularity: 'hours' // default is seconds
   },
});
```
198)   Can cluster index be used in combination with secondary index?
   - **Answer:** Yes, a good example os find by second index together with sorting based on cluster index
199)   What is MongoDB Database Tools suite? 
   - **Answer:** 
     -  MongoDB Database Tools are a suite of command-line utilities for working with MongoDB.
     -  MongoDB Database Tools allow you to import and export data, restore backups, and view diagnostics for your deployments.
     -  Version compatibility between MongoDB Database Tools and the target MongoDB server is crucial to ensure interoperability and data integrity. 
200)   How does `mongodump` works?
   - **Answer:** `mongodump <options> <connection-string>`
     - **options:**
       - `--out` - directory where to dump
       - `--db` - to chose a single db instead of entire cluster
       - `--collection`
       - `--readPreference`
       - `--gzip`
       - `--archive`
       - `--oplog` 
         - works only on entire cluster, not compatible with `--db` and `--collection`
         - fails if there is collection rename or an aggregation with `$out` option
201)   In which situations can be used `mongodump` for backup/restore?
   - **Answer:** For small non-clustered database
202)   What should be used for production quality backup/recovery?
   - **Answer:** 
     - MongoDB Atlas
     - MongoDB Cloud Manager
     - MongoDB Ops Manager
203)   Does `mongodump` keep indexes?
   - **Answer:** No
204)   Is the example of `mongodump` command correct?
   - **Answer:** Yes,
```sh
$ mongodump \
   -v \ # for verbosity
   --gzip \
   --archive=backup.gz \
   "connection_string"
```
205)   Where does mongodump put dump file by default?
   - **Answer:** Inside `dump` folder in the current working directory
206)   Which of the following commands will back up only the `grades` collection from the `sample_training` database in a dump directory?
   - **Answer:** 
     - `mongodump --db sample_training --collection grades `
     - `mongodump --collection grades "mongodb+srv://dbaTestAdmin@m0-example-cluster.iy0a1o4.mongodb.net/sample_training"` **only in case of Atlas Cluster**
207)   What should be taken into consideration when `mongorestore` is used?
   - **Answer:** 
     - Version compatibility between source and target cluster
     - Version compatibility between `mongodump` and `mongorestore` cluster
     - Handles network partition poorly
     - Limited support for point-in-time restores
208)   How `mongorestore` works?
   - **Answer:** 
     - Does not update matching documents but does insert new ones
     - Does not restore `system.profile`
     - Can trigger index builds (time consuming)
209)   What is the syntax of `mongorestore`? 
   - **Answer:** `mongorestore <options> <connection_string> <directory-or-file-to-restore-from>`
210)   Enumerate `mongorestore` options
   - **Answer:** 
     - `--nsInclude` - indicates what collections to include/exclude during the restore process
     - `--nsExclude` - indicates what collections to include/exclude during the restore process
     - `--drop` - removes collections if exists and create empty ones and then upload data, restored documents will get new `_id`
     - `--noIndexRestore`
     - `--writeConcern` - default is `majority`
     - `--gzip` - only if source data is compressed
     - `--archive` - to use a single file instead of directory
     - `--oplogReplay`
211)   Example of mongorestore command
   - **Answer:** 
```sh
mongorestore \ 
   -v \
   --gzip \
   --archive=backup.gz \
   --drop \
   "connection_string"
```
212)   What does the next command `mongorestore --nsInclude=sample_training.grades dump/`?
   - **Answer:** It will restore only the grades collection from the `sample_training` database from a `dump` directory
213)   How to export data using `mongoexport` command?
   - **Answer:** 
```sh
mongoexport \
   -v \
   --collection transaction \
   --query '{ "transaction_count": {$gte: 50} }'
   --out export.json \
   --jsonFormat canonical \
   "connection_string"
```
214) How to import data using `mongoimport` command?
   - **Answer:** 
```sh
mongoimport \
   -v \
   --collection newCollection \
   --type json \
   --mode insert \
   --drop \
   --file export.json \
   "connection_string"
```
215)   What does `mongostat` do?
   - **Answer:** Collect realtime statistics about the cluster
```sh
$ mongostat -h 'localhost:27017' -u root --authenticationDatabase admin

insert query update delete getmore command dirty used flushes vsize   res qrw arw net_in net_out conn                time
    *0    *0     *0     *0       0     0|0  0.0% 0.0%       0 1.44G 84.0M 0|0 0|0   111b   49.8k    3 Feb 18 18:07:47.463
    *0    *0     *0     *0       0     0|0  0.0% 0.0%       0 1.44G 84.0M 0|0 0|0   111b   49.9k    3 Feb 18 18:07:48.463
    *0    *0     *0     *0       0     1|0  0.0% 0.0%       0 1.44G 84.0M 0|0 0|0   112b   50.0k    3 Feb 18 18:07:49.462
```
216)   What does `mongostat -o='insert=new_col_name'` do?
   - **Answer:** It show only insert column with the new alias for it.
217)   What does the next command do?
   - `mongostat -o='host,opcounters.insert.rate()=Insert Rate,opcounters.query.rate()=Query Rate,opcounters.command.rate()=Command Rate' --rowcount=3 -h 'localhost:27017' -u root --authenticationDatabase admin`
   - **Answer:** Shows the next 3 records of diagnostic:
```
host                       Insert Rate Query Rate Command Rate
localhost:27017           0          0            0
localhost:27017           0          0            1
localhost:27017           0          0            1

```
218)   Write an example of `mongotop` command!
   - **Answer:** 
```
$ mongotop “mongodb+srv://dbaTestAdmin@cluster0.mntqoh9.mongodb.net” 2 --rowcount=3

                    ns    total    read    write    2024-02-18T18:29:03Z
    admin.system.roles      0ms     0ms      0ms                        
    admin.system.users      0ms     0ms      0ms                        
  admin.system.version      0ms     0ms      0ms                        
config.system.sessions      0ms     0ms      0ms                        
   config.transactions      0ms     0ms      0ms                        
  local.system.replset      0ms     0ms      0ms   
```
219)   Which of the following commands will show the read and write activity of ar MongoDB instance at `mongodb+srv://username@businesscluster.iy0a1o4.mongodb.net` every 30 seconds?
   - **Answer:** mongotop 30 --uri='mongodb+srv://username@businesscluster.iy0a1o4.mongodb.net' 
220)   Which of the following best describes the MongoDB Database Tool `mongotop`?
   - **Answer:** `mongotop` provides a method to track the amount of time a MongoDB instance spends reading and writing data
221)   What is `bsondump`? 
   - **Answer:** cli converter of BSON files to JSON or any other human readable format
222)   Give an example of `bsondump` command
   - **Answer:** 
```sh
bsondump \
   --pretty \
   --type=debug \
   dump/sample_analytics/accounts.bson
```
223)   Command which will output a prettified JSON file from the grades.bson file?
   - **Answer:** `bsondump --outFile=grades.json -–pretty grades.bson`
224)   How to upload a file to GridFS?
   - **Answer:** `mongofiles -v put test.txt --host 'localhost' --port 27017  --db my_db -u test1`
225)   What is GridFS?
   - **Answer:** A specification for storing files in mongodb
226)   WHat is `mongofiles`?
   - **Answer:** A cli tool which provide an interface between local filesystem and GridFS
227)   Who does GridFS works?
   - **Answer:** It creates 2 collections `fs.chunks` and `fs.files`.
     - In `fs.files` it stores file metadata like name, size, created date
     - In `fs.chunks` it stores binary chunks for that file, each chunk has a size of 255Kb and a reference to related `fs.files` document.
228)   What commands supports `mongofiles` clie tool?
   - **Answer:** 
     - `--list`
     - `--search`
     - `--get`
     - `--put`
     - `--delete`
229)   Which of the following commands will list all of the files in the GridFS collection in the grades database that start with the word "final"?
   - **Answer:** `mongofiles -d=grades list final `
230)   How to Start, Stop, and Check the Status of mongod?
   - **Answer:** 
     - `sudo systemctl start mongod`
     - `sudo systemctl status mongod`
     - `sudo systemctl stop mongod`
231)   What is `mongod`?
   - **Answer:**  `mongod` is the primary daemon process for the MongoDB system.
232)   How to change `/etc/mongod.conf` and to apply changes?
   - **Answer:** 
     - `sudo nano /etc/mongod.conf`
     - `sudo ss -ltp | grep mongo`
     - `sudo systemctl restart -l mongod`
233)   You want to create your first database user. What database should you create the first database user on?
   - **Answer:** ` admin` - The localhost exception allows you to enable access control and then create only the first user or role in the system on the admin database. Once the first user is created, the localhost exception is disabled, so it’s important that this user has privileges to create other users.
234)   How to identify the log path for mongodb logs?
   - **Answer:** `db.serverCmdLineOpts().parsed.systemLog.path`
235)   How to access Logs in mongosh?
   - **Answer:** `show logs`, `show log <type>`, `show log global`
236)   Which are considered core metrics for MongoDB deployment?
   - **Answer:** 
     - Query Targeting (how many documents were scanned and how many returned, the ideal value is 1, but if were scaned 100 and returned 1 then it is a problem)
     - Storage
       - Disk space percent free
       - Disk IOPS
       - Disk Queue Depth
       - Disk Latency
     - CPU utilization
     - Memory utilization
     - Replication lag
237)   Which of the following is the ideal value for objects scanned when reviewing the Query Targeting metrics?
   - **Answer:** 1
238)   You need to check if the storage subsystem has become a bottleneck. Which of the following storage metrics will help you determine if operations are waiting to be serviced?
   - **Answer:**  Disk Queue Depth 
239)   Which are metrics that are less important but also required?
   - **Answer:** 
     - Opcounters - operations run per second on a mongodb deployment
     - Network Traffic
     - Connections - opened to the db
     - Tickets Available - concurrent read and write operations available to the db storage.
240)   Which of the following metrics allows you to view the rate at which different types of operations are being performed within your deployment?
   - **Answer:** Opcounters
241)   Which of the following metrics should you watch if you wish to monitor network traffic for your deployment?
   - **Answer:** 
     -  bytesIn
     -   bytesOut
     -   numRequests
242)   You are creating new alerts for an M5 (shared-tier) cluster and you successfully configure an alert for a Query Targeting metric. However, you notice that the alert isn’t being triggered, even though several queries are exceeding the threshold you set. Which of the following explains why the new alert isn’t being triggered?
   - **Answer:**  The Query Target alert condition will not trigger an alert for an M5 cluster
243)   You have been granted the clusterAdmin role, will you be able to configure new alert conditions for a specific host?
   - **Answer:** No, the Project Owner role is required to configure any new alerts for a specific host.
244)   You receive a notification from Atlas that an alert has been triggered for your project and you wish to acknowledge the alert with the Atlas CLI. What information is required to successfully acknowledge the alert with the atlas alerts acknowledge command?
   - **Answer:** The atlas alerts acknowledge command requires `id` to successfully acknowledge an alert
245)   Under which of the following circumstances will an Atlas alert’s status change to CLOSED?
   - **Answer:**  When the alert condition is resolved
246)   MongoDB Atlas gathers active operation metrics from the database instance using which of the following commands?
   - **Answer:**  currentOp
247)  You wish to view the total number of active connections to your deployment while in the MongoDB Shell. Which of the following commands should you use?
   - **Answer:**  serverStatus
248)   A business has determined it can tolerate the database being down for no more than one hour. What is this value called?
   - **Answer:**  Recovery Time Objective
249)   A business has determined that it can tolerate losing four hours of data. What is this value called?
   - **Answer:**  Recovery Point Objective
250)   Which of the following are key elements of a backup plan?
   - **Answer:** 
     - How to back up the data
     - How often the data needs to be backed up
     - How long to retain the backup data
251)   Before you create a snapshot, you need to lock your MongoDB deployment
   - **Answer:** True, You need to lock your MongoDB deployment by using the db.fsyncLock() command. This prevents additional write operations while creating the snapshot. After creating the snapshot, remember to unlock your deployment by using the db.fsyncUnlock() command.
252)   You want to create a complete copy of your data at a specific point in time. How can you accomplish this?
   - **Answer:**  Create a snapshot of your data
253)   Which Linux command can be used to restore an archived volume snapshot?
   - **Answer:** `dd`
254)   Volume snapshots should be stored separately from the MongoDB deployment.
   - **Answer:** True, It’s a good idea to store your backups on a separate server from the MongoDB deployment. This allows you to easily access your backups in case your MongoDB deployment server becomes unavailable. It also allows you to save server resources for your deployment server.
255) You want to add a new index to your replica set. Which type of environment should you test in before releasing to users?
   - **Answer:** Pre-production
256)   Which of the following can result in rolling maintenance?
   - **Answer:** 
     - Creating a new index
     - Upgrading the MongoDB version
     - Upgrading the operating system
257)   Which of the following is true about MongoDB maintenance?
   - **Answer:** Nodes can be updated individually while the others continue running. 
258)   You want to upgrade your MongoDB driver. What action should you take before doing so?
   - **Answer:**  Check the compatibility between the driver and MongoDB version
259) Which characteristics affect the proper functioning of an application?
   - **Answer:** 
     - Performance improvements
     - Regressions in driver upgrades
260)   You want to upgrade your MongoDB database version to 6.0 for your three-member replica set. Which of the following should you do before upgrading?
   - **Answer:** 
     - Confirm that MongoDB version 5.0 is installed (You must install versions in consecutive order. For example, if you are running 4.4, you must first upgrade to 5.0 before upgrading to 6.0.)
     - Create a pre-production environment for your application that uses the updated version
     - Confirm the feature compatibility version of each member (Before upgrading, you should confirm that each member of the replica set has the same feature compatibility version. The feature compatibility version enables or disables the features that persist data and are incompatible with earlier versions of MongoDB.)
     - Confirm the state of each member. Before upgrading, ensure that no replica set member is in the ROLLBACK or RECOVERING state. If it’s not clear what the state of the member is, the risk of corrupting or losing data rises significantly.
261)   You have completed your pre-upgrade checklist and are ready to upgrade. What should you do first?
   - **Answer:** Gracefully shut down the first secondary member that you plan to upgrade
262)   You have an Atlas cluster and need to grant specific users access to specific projects. Where can you do this in Atlas?
   - **Answer:** Organizations tab.
263)   You want to retrieve the status of your oplog, including details such as the configured size of your oplog and the first recorded event time. Which command should you use?
   - **Answer:** `rs.printReplicationInfo()`
264)   Which of the following are causes of replication lag?
   - **Answer:** 
     - `Network latency`
     - `Disk throughput`
     - `Write concerns`
265)   You want data to be read only from your secondary nodes. How do you achieve this?
   - **Answer:**  Set the read preference to secondary.
266)   What is the difference between read concern and read preference?
   - **Answer:** 
     - concern - is specified in read/write query
     - preference - is specified in connection string of server config. 
       - `primary`, `secondary`, `primaryPreferred`, `secondaryPreferred`
267)   You want to update the primary node of a replica set. How do you initiate an election?
   - **Answer:** `rs.stepDown()`
268)   Which of the following commands initiates a replica set?
   - **Answer:** `rs.initiate()`
269)   What is the purpose of auditing in the context of database security?
   - **Answer:** 
     -  Support the analysis of security incidents
     -  Comply with regulatory requirements
270)   What is localhost exception?
   - **Answer:** A mechanism which allow to connect to the localhost interface of mongodb in case there isn't any users and roles, it means that the first user should be created for admin database and should have `userAdminAnyDatabase` role
271)   How to enter password securely in `mongosh` session?
   - **Answer:** By using `passwordPrompt()`
272)   How to Enable Access Control?
   - **Answer:** Edit `/etc/mongod.conf` and add:
      ``` 
      #security:
      security:
         authorization: enabled
      ```
      save and `sudo systemctl restart mongod`
273)   You assign a user the userAdminAnyDatabase role. What types of actions can the user take with this role only?
   - **Answer:** 
     - Create users and roles
     - Modify users and roles
274)   How to revoke role from the user?
   - **Answer:** 
      ```
      db.revokeRolesFromUser(
         "financeUser",
         [
            { role: "read", db: "sample_training" }
         ]
      )
      ```
275)   How to get user information?
   - **Answer:** `db.getUser("financeUser")`
276)   You want to create users on a self-managed MongoDB deployment. What do you need to do before you can do so?
   - **Answer:** 
     -  Enable access control
     -  Create a user administrator
277)   How to configure audit in mongodb?
   - **Answer:** 
      ```
      auditLog:
         destination: file
         format: JSON
         path: /var/log/mongodb/auditLog.json
      ```
278)   Which destinations can audit events be printed to when using a self-managed MongoDB instance?
   - **Answer:**  Console, syslog, BSON/JSON file
279)   What are some limitations of encryption at rest?
   - **Answer:** 
     - Managing encryption keys can be a challenge.
     - It does not protect against attacks on data in memory.
     - It does not protect against insider threats.
280)   Enumerate encryption mechanisms of mongodb
   - **Answer:** 
     - CLient-Side Field Encryption - additional layer of security on client level
     - TLS
     - Encryption at rest (only for Enterprise version) - Encryption at rest protects data in storage by encrypting the data files on the database server, along with any backup copies of the data
281)   How does MongoDB support encryption of data in transit?
   - **Answer:** 
     - Client-Side Field Level Encryption (CSFLE) 
     - TLS
282)   How does MongoDB support in-use encryption?
   - **Answer:** 
     - Automatic encryption (CSFLE) Enterprise version
     - Client-Side Field Level Encryption (CSFLE) 
283)   How does MongoDB provide encryption at rest? 
   - **Answer:** 
     - Client-Side Field Level Encryption (CSFLE)
     - Encrypted Storage Engine
284)   What is the best practice for backing up data in MongoDB in a production environment?
   - **Answer:** Use a replica set and take a backup of a secondary node
285)   What is the recommended method to store user credentials in a MongoDB deployment with multiple replica sets?
   - **Answer:** By storing credentials in a hashed format in a dedicated authentication database
286)   How to configure MongoDB to enforce internal SCRAM authentication?
   - **Answer:** 
      ```
      security:
         keyFile: /var/mongodb/pki/node1/keyfile
      ```
287)   In a MongoDB sharded cluster, how is the routing of client operations to the appropriate shard determined?
   - **Answer:** By the `mongos` process 
288)   What is the configuration that should choosen for production MongoDB cluster to ensure that systems are highly available?
   - **Answer:** 
     -  deploy config servers as a 3 member replica set
     -  deploy each shard as a 3 member replica set
     -  deploy one or more `mongos` routers
289)   How many documents will be inserted by:
         ```
               db.companies.insertMany([
                  {"_id": 1, "name": "Facebook"},
                  {"_id": 2, "name": "Twitter"}, 
                  {"_id": 2, "name": "Tesla"}
               ])
         ```
   - **Answer:** 2
290)   When performing a MongoDB database backup, what are the key considerations to ensure data integrity and consistency?
   - **Answer:** 
     - Take a snapshot of the database files using the operating system's file system-level backup mechanism
     - Use the mongodump command to create a logical backup of the data
     - Enable the "journal" option to ensure durability and crash recovery
291)   What is the purpose of the `mongod` process in MongoDB?
   - **Answer:** To manage the MongoDB database server
292)   What is the built-in database called `config` in MongoDB for?
   - **Answer:** The config database is used to store information about shards in shared MongoDB cluster.
293)   The minimum recommended configuration for a replica set is?
   - **Answer:** three member replica set - one primary and two secondary members
294)   What role does the mongos process play in a MongoDB sharded cluster, and how does it ensure data consistency between shards?
   - **Answer:** The `mongos` process is responsible for routing both read and write operations to the appropriate shards in the cluster, and it ensures data consistency between shards by using a distributed lock mechanism
295)   What will happen when the network partition is resolved and node B becomes available again?
   - **Answer:** The replica set will be reconfigured to elect a new primary node, based on the priorities and delays of the nodes. 
296)   In a MongoDB sharded cluster, what is the correct process for adding a new shard to the cluster?
   - **Answer:** Connect to the `mongos` router and run the sh.`addShard()` method to add the new shard to the cluster.
297)   In MongoDB, which of the following data modeling techniques is the best for representing a many-to-many relationship between two entities?
   - **Answer:** Creating a separate collection for the relationship between the two entities.
298)   What is the main advantage of sharding in MongoDB?
   - **Answer:** It allows horizontal scaling of data storage capacity.
299)   You are managing a MongoDB deployment that experiences frequent write-heavy workloads. The application requires a high level of durability to ensure data integrity. Which of the following options would be the most suitable for achieving durability in this scenario?
   - **Answer:** Enabling journaling with the write concern set to "majority" 
300)   ________
   - **Answer:** AAAAAnswr
301)   ________
   - **Answer:** AAAAAnswr
302)   ________
   - **Answer:** AAAAAnswr
303)   ________
   - **Answer:** AAAAAnswr
304)   ________
   - **Answer:** AAAAAnswr

## Monitoring using Precona MongoDB Exporter, Prometheus and Grafana

### Install Percona MongoDB Exporter on Ubuntu Linux
```sh
$ wget
https://github.com/percona/mongodb_exporter/releases/download/v0.39.0/mongodb_exporter-0.39.0.linux-amd64.tar.gz
$ tar xvzf mongodb_exporter-0.39.0.linux-amd64.tar.gz
$ sudo mv mongodb_exporter-0.39.0.linux-amd64/mongodb_exporter /usr/local/bin/
```

### Create a Percona user for MongoDB
```sh
mongosh

use admin

db.createUser({user: "test",pwd: "testing",roles: [{ role: "clusterMonitor", db: "admin" },{ role: "read", db: "local" }]})
```

## Create a Service for Percona MongoDB Exporter
```sh
$ sudo nano /lib/systemd/system/mongodb_exporter.service # Create a new service file for the mongodb_exporter

# [Unit]
# Description=MongoDB Exporter
# User=prometheus
# 
# [Service]
# Type=simple
# Restart=always
# ExecStart=/usr/local/bin/mongodb_exporter \
# --collect-all \
# --mongodb.uri=mongodb://test:testing@localhost:27017
# 
# [Install]
# WantedBy=multi-user.target

$ sudo systemctl daemon-reload
$ sudo systemctl start mongodb_exporter
$ sudo systemctl enable mongodb_exporter # to start automatically on boot
$ sudo systemctl status --full mongodb_exporter
$ curl http://localhost:9216/metrics  # Confirm that MongoDB metrics are being collected and available via the mongodb_exporter /metrics endpoint

```

### Install Prometheus on ubuntu linux
```sh
$ sudo apt update
$ sudo apt install --yes prometheus
$ sudo systemctl status --full prometheus
$ curl http://localhost:9090/-/ready # check that the service is ready
```

### Edit the Prometheus Configuration File
```sh
$ sudo nano /etc/prometheus/prometheus.yml
#...
# scrape_configs:
# ...
#   - job_name: 'mongodb_exporter'
#     static_configs:
#       - targets: ['localhost:9216']
#...

$ sudo systemctl restart prometheus
$ curl http://localhost:9090/api/v1/targets | jq --raw-output '.data.activeTargets[] | .scrapeUrl + " " + .health' # Use the Prometheus server API to confirm that the local MongoDB exporter target is present and healthy
```

### Install Grafana on Ubuntu Linux
```sh
$ sudo apt-get install --yes apt-transport-https software-properties-common wget
$ sudo wget -q -O /usr/share/keyrings/grafana.key https://apt.grafana.com/gpg.key
$ echo "deb [signed-by=/usr/share/keyrings/grafana.key] https://apt.grafana.com stable main" | sudo tee -a /etc/apt/sources.list.d/grafana.list
$ sudo apt update
$ sudo apt install --yes grafana
```

### Start Grafana as a Service
```sh
$ sudo systemctl start grafana-server
$ sudo systemctl enable grafana-server
$ sudo systemctl status --full grafana-server
$ curl http://localhost:3000/api/health
```

### Add Prometheus to the Grafana Server
```sh
curl \
  --header 'Content-Type: application/json' \
  --user 'admin:admin' \
  --request 'POST' \
  --data '{"name": "Prometheus", "type": "prometheus", "url": "http://localhost:9090", "access": "proxy"}' \
  http://localhost:3000/api/datasources
```

## Command Line Metrics
```sh
db.runCommand(
   {
     serverStatus: 1
   }
);

db.runCommand( { serverStatus: 1 } ).connections;

db.adminCommand(
   {
     currentOp: true,
     "$all": true
   }
);

db.adminCommand(
   {
     killOp: 1,
     op: <opid>,
     comment: <any>
   }
);
```


## Automating Log Rotation with the logrotate linux's Service

1. Open `sudo vim /etc/mongod.conf` and add that:
```
...
systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log
  logRotate: reopen
...
```
2. `sudo vim /etc/logrotate.d/mongod.conf`
```
/var/log/mongodb/mongod.log {
   daily 
   size 
   rotate 10 
   missingok
   compress 
   compresscmd /usr/bin/bzip2 
   uncompresscmd /usr/bin/bunzip2 # command to uncompress the file
   compressoptions -9 # options for the compression utility
   compressext .bz2 # file format of the compressed archive
   delaycompress # wait to compress files until it's an opportune time
   notifempty # don't bother compressing if the log file is empty
   create 640 mongodb mongodb # creates the log  file with specific permissions
   sharedscripts # don't run multiple rotations at once
   postrotate # tell mongod to rotate, remove empty files
       /bin/kill -SIGUSR1 `cat /var/run/mongodb/mongod.pid 2>/dev/null` >/dev/null 2>&1
       find /var/log/mongodb -type f -size 0 -regextype posix-awk -regex "^\/var\/log\/mongodb\/mongod\.log\.[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}$" -execdir rm {} \; >/dev/null 2>&1
   endscript # end of the script
}
```
3. `sudo systemctl restart mongod`
4. __Testing the logrotate Configuration__
   - `sudo tail -F /var/log/mongodb/mongod.log the mongod process:`
   - `sudo kill -SIGUSR1 $(pidof mongod)`
   - Notice the following line, indicating that the log was reopened `tail: /var/log/mongodb/mongod.log: file truncated;`

## Filesystem Snapshot Volumes on a MongoDB Server - dd tool

### Lock the Database
```sh
mongosh

db.fsyncLock();

exit
```

### Create a Snapshot Volume
```sh
sudo lvcreate --size 100M --snapshot --name mdb-snapshot /dev/vg0/mdb;
```

### Unlock the Database
```sh
mongosh

db.fsyncUnlock();
```

### Archive the Snapshot
```sh
exit

sudo dd status=progress if=/dev/vg0/mdb-snapshot | gzip > mdb-snapshot.gz
```

### Restore the Archived Snapshot
```sh
$ sudo lvcreate --size 1G --name mdb-new vg0;

$ gzip -d -c mdb-snapshot.gz | sudo dd status=progress of=/dev/vg0/mdb-new

$ sudo systemctl stop -l mongod; sudo systemctl status -l mongod;

$ sudo rm -r /var/lib/mongodb/*

$ sudo umount /var/lib/mongodb

$ sudo mount /dev/vg0/mdb-new /var/lib/mongodb

$ sudo systemctl start -l mongod; sudo systemctl status -l mongod;

$ mongosh

$ show dbs
```
## Filesystem Archives on a MongoDB Server - tar
### Lock the Database
```sh
mongosh

db.fsyncLock();

exit
```

### Create a Snapshot Volume
`sudo lvcreate --size 100M --snapshot --name mdb-snapshot /dev/vg0/mdb;`

### Unlock the Database
```sh
mongosh

db.fsyncUnlock();

# mount the snapshot volume taken previously as read-only on the directory that you just created:
sudo mount -t xfs -o nouuid,ro /dev/vg0/mdb-snapshot /tmp/mongodbsnap/

# use tar to create a new archive of all the files in the mongodbsnap directory
sudo tar -czvf mdb-snapshot.tar.gz -C /tmp/mongodbsnap/ .
```

### Restore the Archived Snapshot
```sh
sudo mkdir /mdb

sudo tar -xzf mdb-snapshot.tar.gz -C /mdb

# Then stop the MongoDB service before updating the dbPath in the MongoDB configuration file. Then, make the mongodb user and group the owner of the mdb directory and all of its files. Otherwise, the service will fail to start.
sudo systemctl stop -l mongod; sudo systemctl status -l mongod;

sudo chown -R mongodb:mongodb /mdb

# Next, open the mongod.conf file and change the dbPath to /mdb
sudo nano /etc/mongod.conf

storage:
  dbPath: /mdb

# Start the MongoDB service and connect to the deployment. Then run show dbs to confirm that the databases have been restored:
sudo systemctl start -l mongod; sudo systemctl status -l mongod;

mongosh

show dbs
```


### Specify the Write Concern on an Individual Operation
```sh
db.cats.insertOne({ name: "Mac", color: "black", age: 6 }, { writeConcern:
{ w: "majority" , wtimeout: 3000 } }); # 3000 milliseconds
```

### Set the Default Read and Write Concerns
```sh
db.adminCommand({
    setDefaultRWConcern : 1,
    defaultReadConcern: { level : "majority" },
    defaultWriteConcern: { w: "majority" }
  })
```

### Set the Read Preference in connection string
`mongodb://db0.example.com,db1.example.com,db2.example.com/?replicaSet=myRepl&readPreference=secondary&maxStalenessSeconds=120`




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
<img src="./forReadme/mongo1.png" />

## Exercise 2 
- Connect to local MongoDB using Compass GUI with connection string: `mongodb://root:root@localhost:27017/admin`
<img src="./forReadme/mongo3.png" />

## Exercise 3
- Create the first admin database user
```sh
$ sudo systemctl start mongod

$ mongosh

use admin
db.createUser(
  {
    user: "dbaTestAdmin",
    pwd: "dbaTestPassword", # or passwordPrompt()
    roles: [ { role: 'root', db: 'admin' } ]
  }
)
show users
db.adminCommand(
   {
      createUser: "dbaTestAdmin",
      pwd: "dbaTestPassword",
      roles: [
      { role: "userAdminAnyDatabase", db: "admin" }
      ]
   }
)
# to stop mongod service
db.adminCommand( { shutdown: 1 } )

```

## Exercise 4
- First localhost session has all roles, but it should create one admin user and to perform for its name, because localhost usor after that won't have priveledges
<img src="./forReadme/mongo4.png" />

## Exercise 5 Replica set
<img src="./forReadme/mongo5.png" />
<img src="./forReadme/mongo6.png" />

Restart each mongod service `sudo systemctl restart mongod`

Check db status `sudo systemctl status mongod`

In `mongosh`:
<img src="./forReadme/mongo7.png" />
<img src="./forReadme/mongo8.png" />

Connect to the replica set
<img src="./forReadme/mongo9.png" />

Run:
- `rs.status()`
- `rs.stepDown()` to make current PRIMARY as a SECONDARY
- `rs.status()` show that the new node is PRIMARY
---

The command `db.hello()` to show the status of the current mongod instance
<img src="./forReadme/mongo10.png" />

The command `rs.conf()`
<img src="./forReadme/mongo11.png" />

The command to change the config, to not forget to run `rs.reconfig(config)`
<img src="./forReadme/mongo12.png" />

Replacing replica set member, to not forget to run `rs.reconfig(config)`
<img src="./forReadme/mongo13.png" />

To show the status of the replica set run: `rs.status()`

Configure TLS
<img src="./forReadme/mongo14.png" />

# References:
- https://www.mongodb.com/docs/drivers/