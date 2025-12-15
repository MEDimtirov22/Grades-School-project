
CREATE TABLE User (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(50) NOT NULL
);
INSERT INTO User (id, name, role, password) VALUES
(1, 'ivan', 'teacher', 'pass'),
(2, 'petar', 'student', 'p');
CREATE TABLE Grade (
    id INT PRIMARY KEY,
    studentId INT NOT NULL,
    courseName VARCHAR(50) NOT NULL,
    value DECIMAL(3,1) NOT NULL,
    FOREIGN KEY (studentId) REFERENCES User(id)
);
INSERT INTO Grade (id, studentId, courseName, value) VALUES
(1, 2, 'Mathematics', 5.5);
