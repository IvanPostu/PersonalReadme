// Create a new database and switch to it
db = db.getSiblingDB('my_db');

// Create a new collection and insertMany documents
db.mycollection.insertMany([
    { name: 'Document 1' },
    { name: 'Document 2' },
    { name: 'Document 3' }
]);

db.accounts.insertMany([
    { account_id: "MDB12234728", account_type: 'type1', balance: 2000 },
    { account_id: "MDB12234727", account_type: 'type1', balance: 1000 },
    { account_id: "MDB12234726", account_type: 'checking', balance: 500 },
    { account_id: "MDB12234725", account_type: 'checking', balance: 2000 },
    { account_id: "MDB12234724", account_type: 'checking', balance: 3000 },
    { account_id: "MDB12234723", account_type: 'checking', balance: 100 }
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
