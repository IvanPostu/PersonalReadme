# MongoDB

## Setup MongoDB Container
```bash

$ docker-compose up -d
$ docker-compose stop
$ docker-compose down
$ docker exec -it ipostu-mongo bash
$ mongosh --username root --password root --authenticationDatabase admin

$ show dbs        # Show the list of databases
$ use your_db     # Switch to a specific database
$ db.collection.find()  # Example query to find documents in a collection

```

## What is MongoDB
It is a general purpose document database suitable for a wide range of use cases. 

## Benefits comparing to relational databases
- Easier to map data in the program comparing to relational database
- Flexible schema model

## Terms 
- Document - a basic unit of data, displayed in JSON, stored in BSON format.
- Collection - grouping of documents
- Database - container for collections
- BSON - format in which the documents are stored (binary json)

## Data types
- All JSON data types
- Dates
- Numbers
- ObjectId

## _id
- Every document requires an `_id` field which acts as a primary key.
- Must be unique
- If the document doesn't include the `_id` field then MongoDB automatically generates an `ObjectId` for the `_id` field. 

## Schema 
- Comparing to relational databases is more flexible because requires only updating of classes to include a new field.

## Optional Schema Validation Rules
- Set constraints of the structure of documents

## Polymorphic documents
- Means that collection can store different sort of documents

## Lab 1 
- Create a DB and 2 collections, insert 2 records and update 1 through MongoDB-Atlas
<img src="./mongo1.png" alt="Image Description" >

