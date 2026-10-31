# JPA, Hibernate, and Spring Data JPA — Method Cheat Sheet

## 1. JPA — `EntityManager`

```text
ENTITY OPERATIONS
─────────────────
persist()
merge()
remove()
find()
getReference()

PERSISTENCE CONTEXT
───────────────────
contains()
detach()
clear()
refresh()
flush()

QUERY
─────
createQuery()
createNamedQuery()
createNativeQuery()
createStoredProcedureQuery()

CRITERIA
────────
getCriteriaBuilder()

TRANSACTION
───────────
getTransaction()
isJoinedToTransaction()
joinTransaction()

LOCK
────
lock()

CACHE
─────
getCacheRetrieveMode()
setCacheRetrieveMode()
getCacheStoreMode()
setCacheStoreMode()

LIFECYCLE
─────────
close()
isOpen()

OTHER
─────
unwrap()
getDelegate()
setProperty()
getProperties()
```

---

## 2. Hibernate — `Session`

Hibernate `Session` contains the JPA-style operations plus Hibernate-specific operations.

```text
ENTITY OPERATIONS
─────────────────
persist()
merge()
remove()
find()
getReference()

Hibernate-specific:
get()
load()              [deprecated/old]
save()              [deprecated/old]
update()            [deprecated/old]
saveOrUpdate()      [deprecated/old]

PERSISTENCE CONTEXT
───────────────────
contains()
detach()
clear()
refresh()
flush()

Hibernate-specific:
isDirty()
setReadOnly()
isReadOnly()
setDefaultReadOnly()
isDefaultReadOnly()

QUERY
─────
createQuery()
createNamedQuery()
createNativeQuery()

Hibernate-specific:
createSelectionQuery()
createMutationQuery()

ENTITY LOADING
──────────────
byId()
byMultipleIds()
byNaturalId()
bySimpleNaturalId()
byMultipleNaturalId()

TRANSACTION / SESSION
─────────────────────
beginTransaction()
getTransaction()
isJoinedToTransaction()
joinTransaction()

close()
isOpen()

LOCK
────
lock()
getCurrentLockMode()

CACHE
─────
getCacheMode()
setCacheMode()

getCacheRetrieveMode()
setCacheRetrieveMode()

getCacheStoreMode()
setCacheStoreMode()

FILTER
──────
enableFilter()
disableFilter()
getEnabledFilter()

FETCH PROFILE
─────────────
enableFetchProfile()
disableFetchProfile()
isFetchProfileEnabled()

JDBC
────
doWork()
doReturningWork()

OTHER
─────
getSessionFactory()
getIdentifier()
getEntityName()
getLobHelper()
cancelQuery()
```

### Important

These old Hibernate APIs:

```java
save()
update()
saveOrUpdate()
load()
```

should not be your priority for modern Hibernate. Prefer:

```java
persist()
merge()
getReference()
```

---

## 3. Spring Data JPA — `JpaRepository`

Typical usage:

```java
DepartmentRepository
        ↓
JpaRepository<Department, Long>
```

### CRUD

```text
CREATE / UPDATE
───────────────
save()
saveAll()

READ
────
findById()
findAll()
findAllById()
existsById()
count()

DELETE
──────
delete()
deleteById()
deleteAll()
deleteAll(entities)
deleteAllById()
```

### PAGINATION / SORTING

```text
PAGINATION
──────────
findAll(Pageable)

SORTING
───────
findAll(Sort)
```

### FLUSH

```text
FLUSH
─────
flush()
saveAndFlush()
saveAllAndFlush()
```

### BATCH DELETE

```text
BATCH DELETE
────────────
deleteAllInBatch()
deleteAllInBatch(entities)
deleteAllByIdInBatch()
```

### QUERY BY EXAMPLE

```text
QUERY BY EXAMPLE
────────────────
findOne(Example)
findAll(Example)
findAll(Example, Sort)
findAll(Example, Pageable)

exists(Example)
count(Example)
```

### CUSTOM QUERY METHODS

Spring Data generates queries from method names:

```text
findByName()
findByNameContaining()
findByDepartment()
findBySalaryGreaterThan()
findByNameAndDepartment()
existsByEmail()
countByDepartment()
deleteByDepartment()
```

Example:

```java
List<Employee> findByDepartment(String department);
```

### `@Query`

```text
CUSTOM JPQL / SQL
─────────────────
@Query(...)
```

Example:

```java
@Query("SELECT e FROM Employee e WHERE e.salary > :salary")
List<Employee> findHighSalary(
    @Param("salary") double salary
);
```

---

# Side-by-side

## JPA `EntityManager`

```text
persist()
merge()
remove()
find()
getReference()

contains()
detach()
clear()
refresh()
flush()

createQuery()
createNamedQuery()
createNativeQuery()
createStoredProcedureQuery()

getCriteriaBuilder()

lock()

close()
isOpen()

unwrap()
getDelegate()
setProperty()
getProperties()
```

## Hibernate `Session`

```text
persist()
merge()
remove()
find()
getReference()

get()
load()              [old]
save()              [old]
update()            [old]
saveOrUpdate()      [old]

contains()
detach()
clear()
refresh()
flush()

createQuery()
createNativeQuery()
createSelectionQuery()
createMutationQuery()

byId()
byMultipleIds()
byNaturalId()

isDirty()
setReadOnly()

enableFilter()
disableFilter()

doWork()
doReturningWork()

getSessionFactory()

close()
isOpen()
```

## Spring Data JPA `JpaRepository`

```text
save()
saveAll()

findById()
findAll()
findAllById()

existsById()
count()

delete()
deleteById()
deleteAll()

findAll(Pageable)
findAll(Sort)

flush()
saveAndFlush()
saveAllAndFlush()

deleteAllInBatch()
deleteAllByIdInBatch()

findOne(Example)
findAll(Example)
exists(Example)
count(Example)

+ your own:
findByName()
findByDepartment()
findByNameContaining()
...
```

# Final mental model

```text
                  YOUR CODE
                     │
                     ▼
        ┌────────────────────────┐
        │   Spring Data JPA      │
        │                        │
        │ JpaRepository          │
        │ save()                 │
        │ findById()             │
        │ findAll()              │
        │ delete()               │
        └───────────┬────────────┘
                    │
                    ▼
        ┌────────────────────────┐
        │          JPA           │
        │                        │
        │    EntityManager       │
        │ persist()              │
        │ merge()                │
        │ remove()               │
        │ find()                 │
        │ flush()                │
        │ detach()               │
        │ clear()                │
        │ refresh()              │
        └───────────┬────────────┘
                    │
                    ▼
        ┌────────────────────────┐
        │       Hibernate        │
        │                        │
        │       Session          │
        │                        │
        │ JPA methods +          │
        │ Hibernate-specific     │
        │ methods                │
        └───────────┬────────────┘
                    │
                    ▼
                  JDBC
                    │
                    ▼
                DATABASE
```

## Three APIs in one sentence

- **`JpaRepository`** → convenient application-level CRUD/query methods.
- **`EntityManager`** → standard JPA persistence-context/entity operations.
- **`Session`** → Hibernate's API, with JPA operations plus Hibernate-specific capabilities.

## Most important distinction

If IntelliJ shows:

```java
departmentRepository.save()
departmentRepository.findAll()
departmentRepository.findById()
```

that's normal.

You are looking at **Spring Data JPA's `JpaRepository` API**, not directly at JPA's `EntityManager` or Hibernate's `Session`.
