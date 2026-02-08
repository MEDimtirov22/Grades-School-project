DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('student', 'teacher')),
    password TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, role)
);

CREATE TABLE grades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    course_name TEXT NOT NULL,
    value REAL NOT NULL CHECK (value >= 2.0 AND value <= 6.0),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_grades_student ON grades(student_id);
CREATE INDEX idx_grades_course  ON grades(course_name);

INSERT INTO users (name, role, password) VALUES
('Angel',  'student', 'pass'),
('Petar',  'student', 'pass'),
('Marto',  'teacher', 'pass'),
('Maria',  'student', 'pass123');

INSERT INTO grades (student_id, course_name, value) VALUES
(1, 'Mathematics', 5.5),
(1, 'Japanese',    5.75),
(1, 'Biology',     6.0),
(1, 'Bulgarian',   4.3),
(2, 'Mathematics', 4.75),
(2, 'Physics',     5.2);