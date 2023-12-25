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

## Questions 

1) What is unbounded document?
    - Unbounded means that document grows infinetly due to poor designed embedding relationship to other collection of elements, like "Article and Multiple Comments which are stored inside the article"


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


## Lab 1 
- Create a DB and 2 collections, insert 2 records and update 1 through MongoDB-Atlas
<img src="./mongo1.png" alt="Image Description" >

