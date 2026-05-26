-- Create the table
CREATE TABLE IF NOT EXISTS employee (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    salary     DOUBLE
);

-- Drop proc if already exists, then recreate
-- MySQL does not support CREATE OR REPLACE PROCEDURE, so we drop first
DROP PROCEDURE IF EXISTS get_employees_by_dept;

-- MySQL needs DELIMITER change; in schema.sql use the full block as-is
-- Spring JDBC executes the whole file, so this works correctly
CREATE PROCEDURE get_employees_by_dept(
    IN p_department VARCHAR(100)
)
BEGIN
    SELECT id, name, department, salary
    FROM   employee
    WHERE  department = p_department;
END;
