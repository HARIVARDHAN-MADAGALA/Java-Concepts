# Java Garbage Collection — End-to-End Guide

This guide explains how Java Garbage Collection (GC) works from application startup to shutdown: JVM memory layout, GC roots and reachability, GC algorithms, collector implementations (Serial, Parallel, CMS, G1, ZGC, Shenandoah), GC lifecycle and phases, triggers and frequency, tuning knobs, monitoring tools, and practical best practices.

Who this is for
- Java developers wanting to understand memory lifecycle
- SREs and platform engineers tuning JVMs in production
- Architects designing systems with predictable memory behavior

Table of contents
- High-level role of GC
- JVM memory model
- Reachability and GC roots
- Generational hypothesis and heap layout
- GC algorithms (mark-sweep, copying, mark-compact)
- Major collectors and evolution (Serial → Parallel → CMS → G1 → ZGC/Shenandoah)
- GC phases and stop-the-world vs concurrent work
- When GC runs and how often (triggers & heuristics)
- Reference types, finalization and cleaners
- GC logging, monitoring and tools
- Tuning checklist and common flags
- Troubleshooting and best practices

---

High-level role of GC
---------------------
- Garbage Collector reclaims memory occupied by objects that are no longer reachable from the program (so apps don't run out of heap manually).
- At process start: JVM allocates heap and non-heap areas. GC is responsible for managing the heap.
- GC is automatic: application code allocates memory; GC frees unreachable objects.
- Goals: correctness (no use-after-free), low pause times, high throughput and predictable latency depending on requirements.

Key responsibilities
- Detect unreachable objects
- Reclaim memory and compact if needed
- Manage generations and promote long-lived objects
- Provide hooks to observe behavior (logs, JMX)

---

JVM memory model (brief)
------------------------
Major runtime memory areas relevant to GC:
- Heap (managed by GC)
  - Young generation (Eden + Survivor spaces)
  - Old (Tenured) generation
- Metaspace (class metadata) — not part of heap, managed separately
- Code cache, thread stacks, native memory — not garbage-collected by Java GC

ASCII layout

  [Java Process]
  ├─ Heap
  │  ├─ Young (Eden | S0 | S1)
  │  └─ Old (Tenured)
  ├─ Metaspace
  └─ Stacks / Native

Why generations?
- Generational hypothesis: most objects die young; few survive long.
- Young gen collections are frequent and fast (copying collectors). Old gen collections are less frequent.

---

Reachability & GC roots
------------------------
GC decides "live" objects by tracing from GC roots. If an object can't be reached from any root, it's garbage.

Common GC roots
- Local variables and parameters on Java stacks (active threads)
- Active JNI references
- Static fields (class variables)
- References from certain VM internal structures (e.g., interned strings)

Reachability types
- Strong reference: prevents collection
- SoftReference: collected only under memory pressure
- WeakReference: collected at next GC cycle if unreachable
- PhantomReference: queued after finalization for cleanup actions

---

GC algorithms (core ideas)
---------------------------
1. Mark-Sweep (2-phase)
  - Mark reachable objects starting from roots.
  - Sweep unmarked objects and reclaim memory.
  - Pros: simple, works for any memory arrangement.
  - Cons: fragmentation, long pauses during sweep.

2. Mark-Compact
  - Mark reachable objects, then compact live objects to reduce fragmentation.
  - Reduces fragmentation but requires moving objects and updating references.

3. Copying (semi-space)
  - Divide space into two halves. Copy live objects from from-space to to-space.
  - Very fast, no fragmentation, but uses extra space (half of region).

4. Generational GC
  - Combine above strategies: copying in young gen, mark-sweep/compact in old gen.
  - Promote survivors after N collections.

---

Major collectors and evolution
------------------------------
Java has evolved many collectors. Below are practical summaries.

1) Serial GC (–XX:+UseSerialGC)
- Single-threaded, stop-the-world collector.
- Good for small heaps and single-threaded environments (tools, small apps).

2) Parallel (Throughput) GC (–XX:+UseParallelGC)
- Parallel young generation collection using multiple threads.
- Focus on throughput; stop-the-world pauses but shorter due to parallelism.

3) CMS — Concurrent Mark-Sweep (deprecated/removed in newer JDKs)
- Concurrent marking and sweeping to reduce long pauses for old gen.
- Had fragmentation issues and complexity (promotion failures), deprecated in JDK 10+ in favor of G1.

4) G1 (Garbage-First) (–XX:+UseG1GC)
- Region-based heap (many fixed-size regions) mixing young and old regions.
- Aims at predictable pause times (target pause goal via `-XX:MaxGCPauseMillis`).
- Concurrent marking phases, mixed collections reclaim both young and old regions.
- Default collector in modern Oracle/OpenJDK versions (since JDK 9/10+).

5) ZGC (–XX:+UseZGC)
- Low-latency collector (sub-millisecond pause goals) using colored pointers and concurrent relocation.
- Scales to very large heaps (multi-terabyte). Available and production-ready since JDK 11+ (stabilized around JDK 15+).

6) Shenandoah (–XX:+UseShenandoahGC)
- Low-pause concurrent collector from Red Hat/Azure; reduces pause times with concurrent compaction.

Choosing a collector
- Small apps / dev: Serial or Parallel may suffice.
- Throughput-sensitive batch jobs: Parallel GC.
- Latency-sensitive services: G1 (default), ZGC or Shenandoah for very low pause requirements.

Evolution timeline (short)
- Early JVMs: Serial and simple mark-sweep/compact
- Parallel improvements for multicore
- CMS added concurrency
- G1 designed for predictable pauses and replaced CMS
- ZGC / Shenandoah added for low-latency and huge heaps

---

GC phases and stop-the-world vs concurrent work
-----------------------------------------------
Common phases (conceptual):
1. Young (Minor) GC phases: stop-the-world copying of live objects from Eden to Survivor(s)/Old.
2. Old (Major) GC phases: mark roots, mark reachable in old, sweep/compact or relocate.
3. Concurrent marking: collector threads trace graph while app threads run (minimizing pauses).
4. Remark phase: stop-the-world to finalize marking for any changes during concurrent marking.
5. Cleanup/compact/relocate: reclaim or move objects (may be concurrent or stop-the-world depending on collector).

Stop-the-world (STW)
- GC pauses the application threads to perform a critical phase (e.g., marking or final remark).
- Impact: latency spikes.
- Many collectors aim to minimize STW duration.

Concurrent work
- Collector threads run concurrently with application threads to reduce pause times (e.g., marking, relocation preparation).
- Still may require short STW "safepoints".

Safepoints
- JVM uses safepoints where all threads reach a safe state; certain GC phases require all threads at a safepoint.
- Excessive safepoint time can cause long application stalls.

---

When GC runs and how often
-------------------------
GC is triggered by several heuristics and explicit calls:
- Allocation pressure: when Eden fills, a minor GC occurs.
- Promotion pressure: when survivors cannot hold survivors and promotion causes old gen fill, major GC runs.
- Explicit `System.gc()` may request a full GC (generally discouraged).
- Concurrent collectors can run background marking triggered by heuristics (e.g., occupancy thresholds).

Frequency depends on:
- Allocation rate (throughput of app)
- Heap sizing: small heaps cause more frequent GC
- Collector algorithm: parallel young GCs are fast and frequent; old gen GCs less frequent but longer.

Practical insight
- Aim for few long but infrequent pauses for throughput workloads, or many short pauses for latency-sensitive apps.
- Monitor allocation rate and survivor promotion rate to tune sizes and tenuring.

---

Reference types, finalization and cleaners
------------------------------------------
- `java.lang.ref` includes SoftReference, WeakReference, PhantomReference (used with ReferenceQueue).
- Finalizers (`finalize()`) are deprecated and unpredictable — use `java.lang.ref.Cleaner` or try-with-resources for deterministic cleanup.
- PhantomReferences + ReferenceQueue allow post-mortem cleanup but require careful engineering.

---

GC logging, monitoring and tools
--------------------------------
Useful JVM flags (JDK 8+ and 11+ variations):
- Print GC logs (JDK8): `-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/path/gc.log`
- Unified logging (JDK9+): `-Xlog:gc*,gc+heap=debug:file=/path/gc.log:time,uptime,level` or simpler `-Xlog:gc`.

Monitoring tools
- `jstat` — GC stats (S0S1 capacity, YGC count/time, FGC count/time)
- `jcmd` — request GC, print heap info, flags
- `jmap` — heap dump
- `jstack` — thread dumps
- VisualVM, JMC (Java Mission Control) — visual analysis
- Prometheus exporters and Grafana dashboards for GC metrics (e.g., G1 Pause Time, GC Count)

Interpreting logs
- Track Young GC frequency & pause time, Old GC frequency & pause time, promotion failures, fragmentation.
- Watch for increasing Old gen occupancy and "Full GC" occurrences — indicates tuning required.

---

Tuning checklist & common flags
-------------------------------
1. Heap sizing
  - `-Xms` and `-Xmx` set min/max heap. Avoid frequent resizing; set `-Xms = -Xmx` for predictable behavior in some systems.
2. Choose collector
  - `-XX:+UseG1GC` (default modern), `-XX:+UseZGC`, `-XX:+UseShenandoahGC` as needed.
3. G1 specific
  - `-XX:MaxGCPauseMillis=200` (target), `-XX:InitiatingHeapOccupancyPercent=45` (trigger concurrent marking)
4. Parallel
  - `-XX:ParallelGCThreads` and `-XX:ConcGCThreads`
5. Tuning tenuring
  - `-XX:MaxTenuringThreshold` controls how many young GCs an object survives before promotion.
6. Avoid `System.gc()` or use `-XX:+DisableExplicitGC` to ignore explicit GC calls in third-party libs.

Example JVM startup for G1 tuned for low pauses

```
java -Xms8g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=35 -XX:+ParallelRefProcEnabled -jar app.jar
```

---

Troubleshooting common symptoms
-------------------------------
- Frequent short young GCs: high allocation rate or too small young generation.
  - Increase `-Xmx` or the young generation size (`-XX:NewSize`, `-XX:MaxNewSize` or `-XX:NewRatio`).

- Long full GCs: old gen fragmentation or promotion failures.
  - Consider G1/ZGC/Shenandoah; tune tenuring; increase heap.

- Promotion failure / Concurrent Mode Failure (CMS): caused by inability to promote due to old gen full during concurrent GC.
  - Increase old gen size, reduce allocation rate, move to G1.

- High pause times despite G1: tweak `MaxGCPauseMillis`, check allocation rate and GC thread counts.

---

Best practices
---------------
- Prefer newer collectors (G1 for general use, ZGC/Shenandoah for ultra-low pause needs).
- Right-size your heap; avoid extremely small heaps for busy services.
- Use application-level pooling and avoid excessive object churn where possible.
- Prefer `try-with-resources` and explicit cleanup over finalizers.
- Monitor GC metrics in production and set alert thresholds for GC pause time and frequency.
- Test changes with realistic load and measure pause distribution, throughput and memory usage.

---

Further reading and references
- Oracle/OpenJDK GC documentation for each collector (G1, ZGC, Shenandoah)
- "Java Performance" and "Java Concurrency in Practice" chapters on memory and GC
- JEPs: ZGC, G1 improvements

---

File created: `src/main/java/org/example/concepts/GarbageCollection_Guide.md`

