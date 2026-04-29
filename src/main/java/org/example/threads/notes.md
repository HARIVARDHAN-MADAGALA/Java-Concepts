1) we can extend Thread and we can start that obj of class
2) we can implement the runnable and can pass to the thread constructor
3) we can pass the lambda fucntion of runnable inside the thread contructor
4) we can create the ExecutorService obj and can submit the runnable/Callable to it and can get it
5) if retrun type is there then it was callable or runnable
6) if any stuck (infinite loop / deadlock) got in callable call() function then return will not come thrn .get() will get blocked and the thread will wait foreever teh u can use **result**.**get**(**2**, **TimeUnit**.**SECONDS**); Doesn’t block forever,Fails fast
7) Callable is **never used alone**.It works with:ExecutorService,Future
8) Stop using `Future` style thinking . Use **CompletableFuture**
9) Future.get() is blocking. To avoid this, we use CompletableFuture which provides non-blocking, callback-based asynchronous programming and allows chaining of dependent tasks.
10)

* NEW
* RUNNABLE
* BLOCKED
* WAITING
* TIMED\_WAITING
* TERMINATED
* Synchronization
* Race conditions
* Volatile vs Atomic
* Locks
* Deadlocks
* BlockingQueue + Producer/Consumer

Synchronization:

* Fixes race condition ✅
* Slows down system ❌

👉 That’s why better tools exist

Even if threads don’t overlap operations, they may still see stale data.

boolean flag = false;

Thread t1 = new Thread(() -> {
while (!flag) {
// wait
}
System.out.println("Started");
});

Thread t2 = new Thread(() -> {
flag = true;
});

👉 You expect:

Started
👉 Reality:

t1 may never see flag = true

infinite loop 💀

🧠 Why this happens CPU + JVM optimization:

Each thread has its own cache

Changes are not immediately visible to others

Solution 1: `volatile`

## 🧠 What `volatile` guarantees

1. **Visibility** ✅
   → changes are seen by all threads immediately
2. **No caching issues** ✅

# 🔥 Solution 2: Atomic Classes

Use:

* **AtomicInteger**

<pre class="overflow-visible! px-0!" data-start="1350" data-end="1443"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="w-full overflow-x-hidden overflow-y-auto"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼt">AtomicInteger</span><span> </span><span class="ͼt">count</span><span> </span><span class="ͼn">=</span><span> </span><span class="ͼn">new</span><span> </span><span class="ͼt">AtomicInteger</span><span>(</span><span class="ͼq">0</span><span>);</span><br/><br/><span class="ͼt">count</span><span class="ͼn">.</span><span class="ͼt">incrementAndGet</span><span>();</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 🧠 What Atomic does

* Uses **CAS (Compare-And-Swap)**
* Ensures **atomic operation without locks**

---

# 🧠 Real-world analogy

### Volatile:

Notice board 📢
→ everyone can see updates immediately
→ but multiple people writing = chaos

### Use `volatile` when:

* simple flag
* no compound operations

### Atomic:

Token system 🎟️
→ one update at a time, safely

### Use Atomic when:

* counters
* shared updates

Race condition → needs atomicity
Visibility issue → needs volatile
Both → use synchronization / atomic

# 🚀 Next Step

👉 **Locks (Step 4)**

* Why `synchronized` is not enough
* ReentrantLock advantages

**control-level concurrency** — this is where you stop relying on JVM magic and start **controlling threads explicitly**.

## 💣 Problem with `synchronized`

You already know:

<pre class="overflow-visible! px-0!" data-start="264" data-end="322"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="w-full overflow-x-hidden overflow-y-auto"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼn">synchronized</span><span>(</span><span class="ͼq">this</span><span>) {</span><br/><span>    </span><span class="ͼl">// critical section</span><br/><span>}</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

Looks fine… until you need **control**.

### Limitations:

* ❌ No timeout
* ❌ No way to check if lock is available
* ❌ No fairness control
* ❌ Automatically blocks (you’re stuck)

👉 You get safety, but **zero flexibility**

# 🔥 Why Locks were introduced

Java added:

* **ReentrantLock**

Because real systems need:

👉 “Try if possible, otherwise move on”
👉 “Don’t wait forever”
👉 “Control which thread gets priority”

# ⚙️ Basic usage

<pre class="overflow-visible! px-0!" data-start="806" data-end="936"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="w-full overflow-x-hidden overflow-y-auto"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼt">ReentrantLock</span><span> </span><span class="ͼt">lock</span><span> </span><span class="ͼn">=</span><span> </span><span class="ͼn">new</span><span> </span><span class="ͼt">ReentrantLock</span><span>();</span><br/><br/><span class="ͼt">lock</span><span class="ͼn">.</span><span class="ͼt">lock</span><span>();</span><br/><span class="ͼn">try</span><span> {</span><br/><span>    </span><span class="ͼl">// critical section</span><br/><span>} </span><span class="ͼn">finally</span><span> {</span><br/><span>    </span><span class="ͼt">lock</span><span class="ͼn">.</span><span class="ͼt">unlock</span><span>();</span><br/><span>}</span></div></div></div></div></div></div></div></div></div></div></div></div></pre>

## ⚠️ Important

If you forget:

<pre class="overflow-visible! px-0!" data-start="976" data-end="1002"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute inset-x-4 top-12 bottom-4"><div class="pointer-events-none sticky z-40 shrink-0 z-1!"><div class="sticky bg-token-border-light"></div></div></div><div class="w-full overflow-x-hidden overflow-y-auto"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span class="ͼt">lock</span><span class="ͼn">.</span><span class="ͼt">unlock</span><span>();</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

👉 💀 Deadlock risk

Unlike `synchronized`, JVM won’t save you.

### synchronized:

Single door 🚪 with guard
→ you wait in line, no choice

---

### ReentrantLock:

Smart door 🚪
→ try entry, timeout, priority, skip

# ⚠️ Where `synchronized` fails in real systems

### Example: API calls

<pre class="overflow-visible! px-0!" data-start="2055" data-end="2143"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="w-full overflow-x-hidden overflow-y-auto pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>Thread A → waits forever  </span><br/><span>Thread B → waits forever  </span><br/><span>System → stuck</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

👉 With `tryLock()`:

* You can fail fast
* Return response
* Keep system alive

# ⚔️ synchronized vs ReentrantLock



| Feature      | synchronized | ReentrantLock |
| ------------ | ------------ | ------------- |
| Easy to use  | ✅           | ❌            |
| Auto release | ✅           | ❌            |
| tryLock      | ❌           | ✅            |
| Timeout      | ❌           | ✅            |
| Fairness     | ❌           | ✅            |

If you always use `synchronized`:

👉 You’re writing **safe but rigid code**

If you use `ReentrantLock` blindly:

👉 You’ll create **deadlocks**



# 🚀 Next Step

👉 **Deadlocks (Step 5)**

* How they actually happen in real code
* How to detect
* How to avoid


Deadlock



A **deadlock** happens when:

> Two or more threads are waiting on each other forever, and none can proceed.
