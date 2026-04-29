package org.example.exceptions;

import java.util.Collections;

public class streams {


}

    /// 🚫 6️⃣ Checked Exception Example Inside Streams
    ///
    /// Streams don’t allow throwing checked exceptions directly:
    ///
    /// list.stream()
    ///     .map(s -> {
    ///         // ❌ Compiler error
    ///         throw new IOException("Error!");
    ///     })
    ///     .collect(Collectors.toList());
    ///
    ///
    /// Why?
    /// → Because map() uses a Function<T, R> which does not declare throws Exception.
    ///
    /// ✅ Solution: Wrap it
    ///
    /// list.stream()
    ///     .map(s -> {
    ///         try {
    ///             return riskyOperation(s);
    ///         } catch (IOException e) {
    ///             throw new RuntimeException(e); // wrap it
    ///         }
    ///     });

