// package org.example.JDBC.storedprocedure;

// public class JPA {

// // Step A — annotate the Entity
// @Entity
// @NamedStoredProcedureQuery(name = "Employee.findByDept", procedureName =
// "get_employees_by_dept", resultClasses = Employee.class, parameters = {
// @StoredProcedureParameter(name = "p_department", type = String.class, mode =
// ParameterMode.IN)
// })
// public class Employee {
// /* fields, getters, setters */ }

// // Step B — one line in the Repository interface
// @Repository
// public interface EmployeeRepository
// extends JpaRepository<Employee, Long> {

// @Procedure(name = "Employee.findByDept")
// List<Employee> findByDepartment(
// @Param("p_department") String dept);
// }

// // Step C — call it from Service, exactly like any other method
// List<Employee> result = repo.findByDepartment("Engineering");

// }
