package org.example.JDBC.storedprocedure;

@Component
public class StoredProcedureInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct // runs automatically when Spring starts this bean
    public void createStoredProcedure() {

        // Step 1: Drop if already exists (so app restart doesn't fail)
        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS get_employees_by_dept");

        // Step 2: Create the stored procedure as a Java String
        String createProc = """
                CREATE PROCEDURE get_employees_by_dept(
                    IN p_department VARCHAR(100)
                )
                BEGIN
                    SELECT id, name, department, salary
                    FROM   employee
                    WHERE  department = p_department;
                END
                """;

        jdbcTemplate.execute(createProc);

        System.out.println("Stored procedure created successfully!");
    }
}
