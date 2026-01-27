
CREATE TABLE User (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(50) NOT NULL
);
INSERT INTO User (id, name, role, password) VALUES
(1, 'Angel', 'student', 'pass'),
(2, 'Petar', 'student', 'pass'),
(3, 'Marto', 'teacher', 'pass');

CREATE TABLE Grade (
    id INT PRIMARY KEY,
    studentId INT NOT NULL,
    courseName VARCHAR(50) NOT NULL,
    value DECIMAL(3,1) NOT NULL,
    FOREIGN KEY (studentId) REFERENCES User(id)
);
INSERT INTO Grade (id, studentId, courseName, value) VALUES
(1, 1, 'Maths', 5.0),
(2, 1, 'Japanese', 5.5),
(3, 1, 'Biology', 6.0),
(4, 1, 'Bulgarian', 4.3);
