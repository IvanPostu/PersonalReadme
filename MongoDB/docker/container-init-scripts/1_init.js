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

db.developers.insertMany([{
    _id: 1,
    fname: 'John',
    lname: 'Smith',
    tech_stack: ['sql', 'git', 'python', 'linux', 'django', 'aws']
},
{
    _id: 2,
    fname: 'Michael',
    lname: 'Doe',
    tech_stack: ['git', 'python', 'sqlite', 'linux', 'flask']
}]);

db.createCollection("students");
db.createCollection("university_courses");
db.createCollection("university_courses_enrolment");

const studentsData = [
    { _id: 1, name: "John Doe", entryType: 'generated', age: 20, grade: "A" },
    { _id: 2, name: "Jane Smith", entryType: 'generated', age: 22, grade: "B" },
    { _id: 3, name: "Bob Johnson", entryType: 'generated', age: 21, grade: "C" },
    { _id: 4, name: "Alice Williams", entryType: 'generated', age: 23, grade: "A" },
    { _id: 5, name: "Charlie Brown", entryType: 'generated', age: 19, grade: "B" },
];

const universityCoursesData = [
    { _id: 1, courseName: "Computer Science", entryType: 'generated', credits: 4, professor: "Dr. Smith" },
    { _id: 2, courseName: "Mathematics", entryType: 'generated', credits: 3, professor: "Dr. Johnson" },
    { _id: 3, courseName: "Physics", entryType: 'generated', credits: 4, professor: "Dr. Williams" },
    { _id: 4, courseName: "History", entryType: 'generated', credits: 3, professor: "Dr. Brown" },
    { _id: 5, courseName: "English Literature", entryType: 'generated', credits: 3, professor: "Dr. Davis" },
];

var universityCoursesEnrolmentData = [
    { studentId: 1, courseId: 5, semester: "Spring 2023" },
    { studentId: 2, courseId: 5, semester: "Fall 2023" },
    { studentId: 3, courseId: 3, semester: "Spring 2023" },
    { studentId: 4, courseId: 2, semester: "Fall 2023" },
    { studentId: 4, courseId: 1, semester: "Spring 2023" },
];

db.students.insertMany(studentsData);
db.university_courses.insertMany(universityCoursesData);
db.university_courses_enrolment.insertMany(universityCoursesEnrolmentData);


