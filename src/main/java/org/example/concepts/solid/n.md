| SOLID Principle | How Spring Implements It                                            |
| --------------- | ------------------------------------------------------------------- |
| **S**           | Services, Controllers, Repositories each have one clear job         |
| **O**           | You can extend beans / strategies without changing core logic       |
| **L**           | Beans and interfaces are substitutable (e.g., multiple DataSources) |
| **I**           | Many small interfaces (e.g., `CrudRepository`, `JpaRepository`)     |
| **D**           | IoC container injects dependencies via abstractions (DI)            |
