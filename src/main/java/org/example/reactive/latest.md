# ⚡ Reactive Spring vs Spring MVC (Clean Interview Notes)

## 🧩 1. What is Reactive Programming?

Reactive Programming is a paradigm based on:

* **Asynchronous**
* **Non-blocking**
* **Data streams**

👉 Instead of waiting for data, the system **reacts when data arrives**.

---

## ⚙️ 2. Spring MVC (Blocking Model)

### 🔹 Key Characteristics

* Synchronous & blocking
* **One thread per request**
* Uses Servlet API (Tomcat, Jetty)

### 🔹 Behavior

* Thread waits until DB/API call completes
* Returns response **only after full processing**

### 🔹 Example

<pre class="overflow-visible! px-0!" data-start="735" data-end="853"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>@</span><span class="ͼt">GetMapping</span><span>(</span><span class="ͼr">"/users"</span><span>)</span><br/><span class="ͼn">public</span><span> </span><span class="ͼt">List</span><span><</span><span class="ͼt">String</span><span>> </span><span class="ͼt">getUsers</span><span>() {</span><br/><span>    </span><span class="ͼn">return</span><span> </span><span class="ͼt">userService</span><span class="ͼn">.</span><span class="ͼt">getAllUsers</span><span>(); </span><span class="ͼl">// blocking</span><br/><span>}</span></div></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## ⚙️ 3. Spring WebFlux (Reactive Model)

### 🔹 Key Characteristics

* Asynchronous & non-blocking
* **Event-loop model (few threads)**
* Built on Project Reactor
* Uses Netty (default)

### 🔹 Behavior

* Returns immediately
* Data is **emitted as it becomes available**

### 🔹 Example

<pre class="overflow-visible! px-0!" data-start="1147" data-end="1269"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>@</span><span class="ͼt">GetMapping</span><span>(</span><span class="ͼr">"/users"</span><span>)</span><br/><span class="ͼn">public</span><span> </span><span class="ͼt">Flux</span><span><</span><span class="ͼt">String</span><span>> </span><span class="ͼt">getUsers</span><span>() {</span><br/><span>    </span><span class="ͼn">return</span><span> </span><span class="ͼt">userService</span><span class="ͼn">.</span><span class="ͼt">getAllUsers</span><span>(); </span><span class="ͼl">// non-blocking</span><br/><span>}</span></div></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 🧠 4. Core Reactive Types


| Type     | Description     |
| -------- | --------------- |
| **Mono** | 0 or 1 element  |
| **Flux** | 0 to N elements |

👉 Think:

* Mono → Optional
* Flux → Stream/List

---

## 🧩 5. MVC vs WebFlux (Direct Comparison)


| Feature      | Spring MVC            | WebFlux      |
| ------------ | --------------------- | ------------ |
| I/O          | Blocking              | Non-blocking |
| Thread Model | 1 thread/request      | Event loop   |
| Response     | After full data ready | Streams data |
| Scalability  | Limited               | High         |
| Server       | Tomcat                | Netty        |

---

## ⚡ 6. Real Execution Difference

### 🔴 Spring MVC

1. Request comes
2. Thread assigned
3. Wait for DB
4. Build full response
5. Send response

👉 Client waits fully

---

### 🟢 WebFlux

1. Request comes
2. Returns immediately
3. Data emitted step-by-step
4. Client receives **streaming response**

👉 Client starts processing early

---

## 🧩 7. Service Layer Example

### Blocking

<pre class="overflow-visible! px-0!" data-start="2188" data-end="2277"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼn">public</span><span> </span><span class="ͼt">List</span><span><</span><span class="ͼt">User</span><span>> </span><span class="ͼt">getUsers</span><span>() {</span><br/><span>    </span><span class="ͼn">return</span><span> </span><span class="ͼt">repository</span><span class="ͼn">.</span><span class="ͼt">findAll</span><span>(); </span><span class="ͼl">// blocking</span><br/><span>}</span></div></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

### Reactive

<pre class="overflow-visible! px-0!" data-start="2292" data-end="2385"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼn">public</span><span> </span><span class="ͼt">Flux</span><span><</span><span class="ͼt">User</span><span>> </span><span class="ͼt">getUsers</span><span>() {</span><br/><span>    </span><span class="ͼn">return</span><span> </span><span class="ͼt">repository</span><span class="ͼn">.</span><span class="ͼt">findAll</span><span>(); </span><span class="ͼl">// non-blocking</span><br/><span>}</span></div></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## ⚙️ 8. Database Layer


| Tech             | Type            |
| ---------------- | --------------- |
| JPA / Hibernate  | ❌ Blocking     |
| JDBC             | ❌ Blocking     |
| R2DBC            | ✅ Non-blocking |
| Reactive MongoDB | ✅ Non-blocking |

👉 Using JPA with WebFlux = **wrong design**

---

## 🧩 9. Important Operators

* `map()` → transform
* `flatMap()` → async transform
* `filter()` → filter data
* `zip()` → combine streams
* `merge()` → merge streams
* `onErrorResume()` → fallback

---

## ⚠️ 10. Error Handling (Reactive)

<pre class="overflow-visible! px-0!" data-start="2874" data-end="2967"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="relative"><div class=""><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼt">userService</span><span class="ͼn">.</span><span class="ͼt">getUser</span><span>(</span><span class="ͼt">id</span><span>)</span><br/><span>    </span><span class="ͼn">.</span><span class="ͼt">onErrorResume</span><span>(</span><span class="ͼt">ex</span><span> -> </span><span class="ͼt">Mono</span><span class="ͼn">.</span><span class="ͼt">just</span><span>(</span><span class="ͼn">new</span><span> </span><span class="ͼt">User</span><span>(</span><span class="ͼr">"default"</span><span>)));</span></div></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

👉 No try-catch → use operators

---

## ⚡ 11. When to Use WebFlux

### ✅ Use when:

* High concurrency (1000+ users)
* Multiple API calls
* Streaming data
* I/O-heavy systems

### ❌ Avoid when:

* Simple CRUD app
* Using JPA/Hibernate
* Team not comfortable with reactive

---

## 🧾 12. Final Summary


| Spring MVC             | WebFlux         |
| ---------------------- | --------------- |
| Blocking               | Non-blocking    |
| Thread per request     | Event loop      |
| Wait for full response | Stream response |
| Simple apps            | High-scale apps |

---

## 🎯 One-line Interview Answer

> Spring MVC is blocking with one thread per request, while WebFlux is non-blocking using an event-loop model that streams data using Mono and Flux, enabling high scalability.
>
