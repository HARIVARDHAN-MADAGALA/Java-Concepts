# ⚙️ 1️⃣ Why Exception Handling Is Tricky in Streams

Normally, in imperative Java code, you can easily use `try-catch`:

<pre class="overflow-visible!" data-start="454" data-end="586"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>for</span><span> (String s : list) {
    </span><span>try</span><span> {
        process(s);
    } </span><span>catch</span><span> (IOException e) {
        e.printStackTrace();
    }
}
</span></span></code></div></div></pre>

✅ Works fine.

But in **Streams**, you write something like:

<pre class="overflow-visible!" data-start="650" data-end="771"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>list.stream()
    .map(s -> process(s)) </span><span>// process() may throw IOException</span><span>
    .collect(Collectors.toList());
</span></span></code></div></div></pre>

⚠️ Problem:

> Lambdas **cannot throw checked exceptions** unless the functional interface allows it.

For example:

* `Function<T, R>` does **not** declare `throws Exception`.
* So, you cannot write:

  <pre class="overflow-visible!" data-start="974" data-end="1029"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>.map(s -> { </span><span>throw</span><span> </span><span>new</span><span> </span><span>IOException</span><span>(); })
  </span></span></code></div></div></pre>

  → ❌ Compiler error: *“Unhandled exception: IOException”*

---

# ⚠️ 2️⃣ Types of Exceptions in Streams


| Type                   | Examples                                     | Can directly handle?                |
| ---------------------- | -------------------------------------------- | ----------------------------------- |
| ✅ Unchecked (Runtime) | `NullPointerException`,`ArithmeticException` | Yes (they can propagate freely)     |
| ❌ Checked             | `IOException`,`SQLException`                 | No (compiler forbids inside lambda) |

---

# 🧩 3️⃣ Ways to Handle Exceptions in Streams

Let’s explore **every safe and clean approach** 👇

---

## ✅ **A. Use try-catch inside the lambda**

Simplest and most direct method:

<pre class="overflow-visible!" data-start="1604" data-end="1924"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>List<String> result = list.stream()
    .map(s -> {
        </span><span>try</span><span> {
            </span><span>return</span><span> riskyOperation(s); </span><span>// may throw IOException</span><span>
        } </span><span>catch</span><span> (IOException e) {
            System.out.println(</span><span>"Error with: "</span><span> + s);
            </span><span>return</span><span> </span><span>"default"</span><span>; </span><span>// fallback</span><span>
        }
    })
    .collect(Collectors.toList());
</span></span></code></div></div></pre>

📘 Works fine when:

* You just want to log or handle per element.
* You don’t need to propagate the exception.

---

## ✅ **B. Wrap checked exceptions into RuntimeException**

If you want to stop stream execution when exception occurs:

<pre class="overflow-visible!" data-start="2163" data-end="2385"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>list.stream()
    .map(s -> {
        </span><span>try</span><span> {
            </span><span>return</span><span> riskyOperation(s);
        } </span><span>catch</span><span> (IOException e) {
            </span><span>throw</span><span> </span><span>new</span><span> </span><span>RuntimeException</span><span>(e);
        }
    })
    .forEach(System.out::println);
</span></span></code></div></div></pre>

This rethrows as **unchecked**, allowing it to bubble up normally.

⚠️ Be sure to unwrap `RuntimeException.getCause()` when catching later.

---

## ✅ **C. Create a reusable wrapper method**

This is a **clean and reusable** functional approach 👇

### Step 1: Write a functional interface that can throw checked exceptions

<pre class="overflow-visible!" data-start="2712" data-end="2815"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>@FunctionalInterface</span><span>
</span><span>interface</span><span> </span><span>CheckedFunction</span><span><T, R> {
    R </span><span>apply</span><span>(T t)</span><span> </span><span>throws</span><span> Exception;
}
</span></span></code></div></div></pre>

### Step 2: Wrap it inside a helper

<pre class="overflow-visible!" data-start="2854" data-end="3107"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>public</span><span> </span><span>static</span><span> <T, R> Function<T, R> </span><span>handleException</span><span>(CheckedFunction<T, R> func)</span><span> {
    </span><span>return</span><span> t -> {
        </span><span>try</span><span> {
            </span><span>return</span><span> func.apply(t);
        } </span><span>catch</span><span> (Exception e) {
            </span><span>throw</span><span> </span><span>new</span><span> </span><span>RuntimeException</span><span>(e);
        }
    };
}
</span></span></code></div></div></pre>

### Step 3: Use it in stream

<pre class="overflow-visible!" data-start="3139" data-end="3250"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>list.stream()
    .map(handleException(MyClass::riskyOperation))
    .forEach(System.out::println);
</span></span></code></div></div></pre>

✅ Elegant
✅ Reusable
✅ Works with method references

---

## ✅ **D. Collect both successful and failed results**

Sometimes you want to **continue stream** and keep track of failures:

<pre class="overflow-visible!" data-start="3437" data-end="3754"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>record</span><span> </span><span>Result</span><span><T>(T value, Exception error) {}

List<Result<String>> results = list.stream()
    .map(s -> {
        </span><span>try</span><span> {
            </span><span>return</span><span> </span><span>new</span><span> </span><span>Result</span><span><>(riskyOperation(s), </span><span>null</span><span>);
        } </span><span>catch</span><span> (Exception e) {
            </span><span>return</span><span> </span><span>new</span><span> </span><span>Result</span><span><>(</span><span>null</span><span>, e);
        }
    })
    .collect(Collectors.toList());
</span></span></code></div></div></pre>

Now you can easily separate:

<pre class="overflow-visible!" data-start="3785" data-end="4030"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>results.stream()
    .filter(r -> r.error() == </span><span>null</span><span>)
    .forEach(r -> System.out.println(</span><span>"Success: "</span><span> + r.value()));

results.stream()
    .filter(r -> r.error() != </span><span>null</span><span>)
    .forEach(r -> System.out.println(</span><span>"Failed: "</span><span> + r.error()));
</span></span></code></div></div></pre>

✅ Stream doesn’t break
✅ All results preserved

---

## ✅ **E. Use external libraries (Optional)**

Libraries like **Vavr** or **jOOL** simplify this:

Example (using Vavr):

<pre class="overflow-visible!" data-start="4208" data-end="4453"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>import</span><span> io.vavr.control.Try;

list.stream()
    .map(s -> Try.of(() -> riskyOperation(s)))
    .forEach(t -> {
        </span><span>if</span><span> (t.isSuccess()) System.out.println(t.get());
        </span><span>else</span><span> System.out.println(</span><span>"Failed: "</span><span> + t.getCause());
    });
</span></span></code></div></div></pre>

✅ Very clean
✅ No custom interfaces needed
❌ Requires dependency

---

# ⚠️ 4️⃣ Important Notes


| Concept                   | Explanation                                                                    |
| ------------------------- | ------------------------------------------------------------------------------ |
| 💥 Checked exceptions     | Must be caught or converted to unchecked                                       |
| 🧱 Stream breaks          | If an unchecked exception is thrown, the stream pipeline stops immediately     |
| 🚫 No recovery mid-stream | You can’t resume a broken stream — you can only skip or handle inside lambda |
| 💡 Parallel streams       | Handle exceptions carefully, because threads may throw asynchronously          |

---

# 🧠 5️⃣ Real Example — Reading files safely

<pre class="overflow-visible!" data-start="5031" data-end="5382"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-java"><span><span>List<String> lines = files.stream()
    .flatMap(file -> {
        </span><span>try</span><span> (Stream<String> stream = Files.lines(file)) {
            </span><span>return</span><span> stream;
        } </span><span>catch</span><span> (IOException e) {
            System.err.println(</span><span>"Error reading "</span><span> + file);
            </span><span>return</span><span> Stream.empty(); </span><span>// skip this file</span><span>
        }
    })
    .collect(Collectors.toList());
</span></span></code></div></div></pre>

✅ Safe
✅ Doesn’t break if one file fails
✅ Continues with remaining files

---

# ⚡ 6️⃣ Summary Table


| Approach                  | Description                 | Use When                 |
| ------------------------- | --------------------------- | ------------------------ |
| `try-catch`inside lambda  | Simple inline handling      | Small logic              |
| Wrap to`RuntimeException` | Propagate unchecked         | Stop pipeline on failure |
| Custom wrapper function   | Clean reusable approach     | Large projects           |
| Record`Result<T>`         | Keep both success & failure | Data analysis            |
| `Try`(Vavr)               | Functional style            | Libraries allowed        |
