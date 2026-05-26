The stored procedure should be created from within the Spring app itself (using schema.sql / @PostConstruct / Flyway etc.)
And then called via Plain JDBC, JdbcTemplate, and Spring Data JPA

