// Create a new database and switch to it
db = db.getSiblingDB('my_db');

// Create a new collection and insert documents
db.mycollection.insert([
    { name: 'Document 1' },
    { name: 'Document 2' },
    { name: 'Document 3' }
]);

db.createUser({
    user: 'test1',
    pwd: 'test1',
    roles: [
        { role: 'readWrite', db: 'my_db' }
    ]
});

db.createUser({
    user: 'test2',
    pwd: 't%e)s$t2',
    roles: [
        { role: 'readWrite', db: 'my_db' }
    ]
});
