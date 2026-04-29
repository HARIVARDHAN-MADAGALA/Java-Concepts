A checked exception is one that the compiler forces you to handle — either by:

putting it inside a try–catch block, or

declaring it with throws in the method signature.

They usually represent recoverable problems — issues your program can handle gracefully.

🧩 Common Checked Exceptions in Java
1️⃣ IOException

➡️ When something goes wrong during input/output — reading or writing files, network issues, stream problems.

2️⃣ SQLException

➡️ When something fails while interacting with a database — like wrong query, DB down, or constraint violation.

3️⃣ ClassNotFoundException

➡️ When Java tries to load a class at runtime (for example via reflection or Class.forName),
but the JVM can’t find that class in the classpath.
Basically, “I looked for this class file, but it’s missing.”

4️⃣ FileNotFoundException

➡️ A subclass of IOException.
Thrown when a program tries to open a file that doesn’t exist or can’t be accessed.

5️⃣ InterruptedException

➡️ When a thread that is sleeping or waiting gets interrupted by another thread.
In short: “I was resting or waiting, someone poked me awake.”

6️⃣ NoSuchMethodException

➡️ Happens during reflection, when you try to access a method that doesn’t exist in a class.

7️⃣ NoSuchFieldException

➡️ Similar to the above, but when a field (variable) is missing during reflection.

8️⃣ InstantiationException

➡️ When Java tries to create an object via reflection (using newInstance()),
but can’t — for example, if the class is abstract or has no default constructor.

9️⃣ IllegalAccessException

➡️ When code tries to access a method, field, or class that it doesn’t have permission to access (often during reflection).

🔟 CloneNotSupportedException

➡️ Thrown when you try to clone an object that doesn’t implement the Cloneable interface.
Basically, “This object doesn’t allow copying.”

1️⃣1️⃣ DataFormatException (from java.util.zip)

➡️ When compressed data (like ZIP) has a bad or corrupted format.

1️⃣2️⃣ URISyntaxException

➡️ When a malformed or invalid URI string is passed to a URI parser —
example: missing :// or wrong characters in a URL.

1️⃣3️⃣ TimeoutException (in concurrency APIs)

➡️ When a blocking operation (like waiting for a result or lock) times out before completing.

1️⃣4️⃣ ParseException

➡️ When parsing data like dates or numbers fails because the input format doesn’t match the expected format.
For example, trying to parse “12-13-2025” when expecting “yyyy/MM/dd”.

🧾 Summary Table
Exception	When It Happens
IOException	File or network I/O fails
SQLException	Database query or connection issue
ClassNotFoundException	JVM can’t find a class
FileNotFoundException	File doesn’t exist or inaccessible
InterruptedException	Thread interrupted while waiting/sleeping
NoSuchMethodException	Method not found via reflection
NoSuchFieldException	Field not found via reflection
InstantiationException	Cannot create object via reflection
IllegalAccessException	Accessing private or restricted member
CloneNotSupportedException	Object not cloneable
DataFormatException	Invalid compressed data
URISyntaxException	Malformed URI/URL syntax
TimeoutException	Operation didn’t finish in time
ParseException	Failed to parse text/date/number
🧠 Orally Summarized:

Checked exceptions are real-world, recoverable issues that Java wants you to handle —
like missing files, invalid data, bad network, or database errors.

Unchecked ones (RuntimeException types) are usually logic errors inside your code (like null pointer, divide by zero).
