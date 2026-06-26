# DELETE vs TRUNCATE vs DROP

  ------------------------------------------------------------------------
  Feature             DELETE            TRUNCATE              DROP
  ------------------- ----------------- --------------------- ------------
  **Command Type**    DML (Data         DDL (Data Definition  DDL (Data
                      Manipulation      Language)             Definition
                      Language)                               Language)

  **Purpose**         Deletes selected  Removes all rows from Removes the
                      or all rows       a table               entire table

  **WHERE Clause**    ✅ Yes            ❌ No                 ❌ No

  **Deletes Specific  ✅ Yes            ❌ No                 ❌ No
  Rows**                                                      

  **Deletes All       ✅ Yes            ✅ Yes                ✅ Yes (with
  Rows**                                                      the table)

  **Table Structure** ✅ Preserved      ✅ Preserved          ❌ Deleted

  **Indexes**         ✅ Preserved      ✅ Preserved          ❌ Deleted

  **Constraints**     ✅ Preserved      ✅ Preserved          ❌ Deleted

  **Triggers Fired**  ✅ Yes            ❌ No                 ❌ No

  **Auto Increment    ❌ No             ✅ Yes (MySQL)        N/A
  Reset**                                                     

  **Rollback**        ✅ Yes (within    ❌ Usually No         ❌ Usually
                      transaction)                            No

  **Logging**         Row-by-row        Minimal               Minimal

  **Performance**     Slow              Fast                  Fastest
  ------------------------------------------------------------------------

------------------------------------------------------------------------

## Syntax

### DELETE

``` sql
DELETE FROM Employee WHERE id = 101;

DELETE FROM Employee;
```

### TRUNCATE

``` sql
TRUNCATE TABLE Employee;
```

### DROP

``` sql
DROP TABLE Employee;
```

------------------------------------------------------------------------

## Quick Memory Trick

  Command        Meaning
  -------------- -----------------------------
  **DELETE**     Remove rows
  **TRUNCATE**   Empty the table
  **DROP**       Remove the table completely
