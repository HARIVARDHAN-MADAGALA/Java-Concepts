# Kafka Interview Questions & Answers — 65 Practical + Textbook Questions

> Companion to `kafka-deep-dive.md`. Organized by topic, ordered roughly easy → hard within each section. Practical/scenario questions are marked **[Scenario]**. Use this as a mock-interview drill: cover the answer, ask yourself the question out loud, then check.

---

## Section A — Core Concepts & Architecture

**1. What is Kafka, and how is it different from a traditional message queue like RabbitMQ?**
Kafka is a distributed, partitioned, replicated commit log. The biggest difference from a classic queue: Kafka **retains** messages for a configured period (or forever, with compaction) regardless of whether they've been consumed, so multiple independent consumers/consumer groups can read the same data at their own pace, and you can replay history. A traditional queue typically removes a message once it's acknowledged by a consumer, and it's usually one-message-one-consumer by design, not built for replay or multiple independent readers of the same stream.

**2. What is a topic, and what is a partition? How are they related?**
A topic is the logical name of a stream (e.g., `orders`). A partition is the physical, ordered, append-only unit that a topic is split into. A topic with 6 partitions is really 6 independent, ordered logs that together make up the topic. Ordering is guaranteed only within a single partition, never across partitions of the same topic.

**3. What is an offset?**
A sequential, immutable, monotonically increasing ID assigned to each record within a partition (0, 1, 2, …). It's how both replication (Part 6 of the deep-dive: LEO/HW) and consumer progress tracking are expressed — everything in Kafka's internal bookkeeping is "offset X in partition Y."

**4. Why does Kafka use partitions at all instead of one big log per topic?**
Two reasons: (1) **parallelism** — multiple producers can write to different partitions concurrently, and multiple consumers in a group can each own a different partition and process in parallel; (2) **scalability** — partitions (and their replicas) are distributed across brokers, so a topic's total throughput and storage isn't capped by a single machine's disk/network.

**5. What determines which partition a message goes to?**
If the producer record has a key, Kafka hashes the key (murmur2 by default) and mods by partition count — same key always lands on the same partition, every time, as long as partition count doesn't change. If there's no key, the default `UniformStickyPartitioner` sticks to one partition per batch and rotates, rather than strict per-record round robin (improves batching efficiency).

**6. Can two partitions of the same topic ever live on the same broker?**
Yes, easily — partition-to-broker assignment is independent per partition. A 3-broker cluster with a 6-partition topic commonly has 2 partition leaders per broker.

**7. What is replication factor, and what does it actually protect against?**
The number of copies of each partition kept on different brokers (1 leader + N-1 followers). It protects against **broker failure** — if the leader's broker dies, a follower with the data can take over as leader with no data loss (provided it was in the ISR). It does not, by itself, protect you against application bugs, accidental topic deletion, or bad data — only against infrastructure failure.

**8. What is the role of a broker?**
A single Kafka server process. It hosts some partitions as leader, some as follower, handles all produce/fetch requests for the partitions it leads, and participates in cluster metadata propagation. A "cluster" is just a set of brokers working together, coordinated via the Controller (ZooKeeper-based) or the KRaft quorum.

**9. What is ZooKeeper's role in Kafka, and why is it being removed?**
Historically, ZooKeeper stored cluster metadata (broker list, topic configs, partition assignments) and was where the Controller broker was elected. It's being replaced by **KRaft**, Kafka's own built-in Raft-based metadata quorum, because running and version-matching a second distributed system added operational overhead, and ZooKeeper-based metadata propagation became a throughput bottleneck on very large clusters (hundreds of thousands of partitions).

**10. [Scenario] You create a topic with 3 partitions and replication factor 3 on a 3-broker cluster. How many total log directories get created across the cluster, and how are leaders typically distributed?**
3 partitions × 3 replicas = 9 total partition-replica directories across the cluster (3 per broker on average). Kafka's default assignment algorithm spreads leadership so each of the 3 brokers leads roughly one partition — this even spread is exactly what "preferred leader election" (Part 7.2) is designed to restore if failovers later cause it to drift.

---

## Section B — Producer Internals

**11. Explain `acks=0`, `acks=1`, and `acks=all`. When would you use each?**
`acks=0`: fire-and-forget, no wait for any acknowledgment — fastest, can silently lose data (e.g., for high-volume metrics/logs where occasional loss is acceptable). `acks=1`: wait for the leader to write to its own local log only — default historically, can lose data if the leader crashes before followers replicate. `acks=all`: wait until every ISR member has the record (HW would cover it) — use for anything where data loss is unacceptable (orders, payments, financial events), paired with `min.insync.replicas`.

**12. What is the difference between `retries` and `delivery.timeout.ms`?**
`retries` is effectively unbounded by default once idempotence is on; the real ceiling on "how long will the producer keep trying before giving up" is `delivery.timeout.ms` — the total time budget from `send()` to final success/failure, covering all retry attempts and backoffs.

**13. What problem does the idempotent producer solve, and how, mechanically?**
It solves duplicate writes and out-of-order writes caused by retries. Mechanism: the producer gets a unique Producer ID (PID) from the broker, and tags every batch per-partition with a strictly increasing sequence number. The broker tracks the last sequence number it accepted per (PID, partition); a retried batch with an already-seen sequence number is recognized and not re-written (no duplicate), and a batch arriving with a sequence number gap is rejected (no silent reordering).

**14. [Scenario] Your producer has `acks=all`, `min.insync.replicas=2`, replication factor 3, and one follower goes offline. What happens to produce requests?**
Nothing breaks yet — ISR is now {leader, 1 follower} = size 2, which still satisfies `min.insync.replicas=2`, so writes continue succeeding, just with reduced redundancy margin. If a *second* replica also drops (ISR size 1), produce requests would start failing with `NotEnoughReplicasException` rather than silently degrading to single-copy durability.

**15. Why does `linger.ms` exist, and why would you deliberately add latency to a producer?**
It lets the producer wait briefly to accumulate a fuller batch before sending, even if `batch.size` hasn't been reached. Trade-off: a few extra milliseconds of per-record latency buys significantly higher throughput and better compression ratios (since compression operates per-batch), because fewer, larger network requests are far more efficient than many tiny ones.

**16. What's the difference between `max.in.flight.requests.per.connection=1` and the default of 5, in terms of risk?**
With 5 in-flight requests and no idempotence, a retry of an earlier failed batch could land *after* a later batch already succeeded, producing out-of-order data on disk. With idempotence enabled (the modern default alongside `acks=all`), the broker's sequence-number checking makes 5 in-flight requests safe without sacrificing ordering — which is exactly why you rarely need to manually drop this to 1 anymore.

**17. [Scenario] You need strict, global ordering across an entire topic, not just per key. Is this possible in Kafka, and what's the trade-off?**
Only by using a single partition for that topic — global ordering and partition-level parallelism are fundamentally in tension; one partition means one leader handles all reads/writes for that topic, capping throughput and consumer parallelism to "one consumer can usefully process it at a time." This is rarely the right trade for high-volume topics; usually the better fix is to redesign for per-key ordering (sufficient for most real use cases) instead.

**18. What does the `Partitioner` interface let you customize, and when would you write your own?**
It lets you control exactly which partition a record lands in, overriding default key-hash or sticky-round-robin behavior. Common reason: a hot-key problem (Section I covers this as a scenario) where you want to "salt" one overloaded key across several partitions deliberately, accepting a narrower ordering guarantee in exchange for better load distribution.

---

## Section C — Consumer & Consumer Group Internals

**19. What is a consumer group, and what guarantee does it give you?**
A named set of consumer instances sharing the work of consuming a topic's partitions, where each partition is assigned to exactly one group member at a time. The guarantee: no two members of the same group will process the same partition simultaneously — but it does **not** guarantee message-level exactly-once processing by itself (that requires the patterns in Section E).

**20. If a topic has 4 partitions and you start a consumer group with 6 consumer instances, what happens to the extra 2?**
They sit idle, assigned zero partitions. Kafka caps useful parallelism within a group at the partition count — partitions are never split between two consumers in the same group.

**21. What's the difference between `enable.auto.commit=true` and manual commits, in terms of delivery guarantees?**
Auto-commit fires on a timer (`auto.commit.interval.ms`) independent of whether you've actually finished processing — if your app crashes between auto-committing and finishing the side effect, you can silently skip a record (closer to at-most-once in that narrow window) or, more commonly in the opposite timing, reprocess a record (at-least-once). Manual commit, called *after* you've durably handled the record (e.g., after the DB write succeeds), is the standard way to deliberately get a correct at-least-once guarantee rather than an accidental, timing-dependent one.

**22. What is the difference between `session.timeout.ms` and `max.poll.interval.ms`? Why does Kafka need both?**
`session.timeout.ms` detects "this process is unreachable" via missed heartbeats (sent on a background thread). `max.poll.interval.ms` detects "this process is alive but stuck processing" — if your app thread doesn't call `poll()` again within this window, the consumer proactively leaves the group even though heartbeats were fine. Kafka needs both because a process can be network-healthy but application-stuck, and treating that the same as "crashed" (or not treating it as a problem at all) both cause real production issues.

**23. [Scenario] During a rolling deployment, your consumer group's throughput drops to near zero repeatedly, recovering between each pod restart. What's likely happening, and what's the fix?**
Classic eager-rebalance stop-the-world behavior — every pod restart triggers a full-group rebalance where every member (not just the restarting one) revokes all its partitions before reassignment. Fix: switch to `CooperativeStickyAssignor` (minimal-diff rebalancing) and set a stable `group.instance.id` per instance for static membership, so brief restarts within `session.timeout.ms` don't trigger a rebalance at all.

**24. What is `auto.offset.reset` for, and what's the danger of `earliest` vs `latest`?**
It controls what a consumer does when it has no previously committed offset for a partition (first run, or its old offset has aged out of retention). `latest` starts from the newest record — safe for "I only care about new data going forward" use cases but means a brand-new consumer group silently skips all history. `earliest` replays the entire retained history — correct for "I need to rebuild full state" use cases, but can be a shock (huge backlog, long catch-up time) if applied accidentally to a high-volume, long-retention topic.

**25. [Scenario] A consumer keeps getting kicked out of its group and reprocessing the same record over and over. What's your first hypothesis and how do you confirm it?**
First hypothesis: one record's processing logic is taking longer than `max.poll.interval.ms` (a poison-pill or a slow downstream call), causing the consumer to leave the group repeatedly before it can commit. Confirm by checking consumer logs for rebalance/leave-group events correlated with processing time logs, and checking if it's always the same offset getting stuck. Fix: implement retry-then-DLQ (Section I) instead of retrying indefinitely inline, and/or raise `max.poll.interval.ms` if the work is legitimately just slow but eventually succeeds.

**26. What is `__consumer_offsets`, and why is it a compacted topic rather than a normal retention-based one?**
It's the internal topic where every committed offset, keyed by `(group.id, topic, partition)`, is stored. It's compacted because only the *latest* committed offset per key actually matters — there's no value in keeping historical commit records around, so `cleanup.policy=compact` naturally keeps just the latest value per key, the same mechanism used for "current state per key" topics generally.

---

## Section D — Replication & Durability

**27. Define Log End Offset (LEO) and High Watermark (HW). Why do we need both concepts?**
LEO is "the offset of the next record this specific replica will write" — every replica (leader and follower) tracks its own LEO. HW is "the highest offset confirmed written by every member of the ISR," tracked by the leader, and it's the visibility boundary — consumers only ever see records ≤ HW. We need both because LEO tells you what a replica *has*, while HW tells you what's actually *safe to expose* — without HW, a consumer could read a record that only existed on the leader, which could vanish forever if the leader crashed before any follower replicated it.

**28. [Scenario] The leader's LEO is 1000, Follower-A's LEO is 998, Follower-B's LEO is 1000, and all three are in the ISR. What is the HW, and can a consumer read offset 999?**
HW = min(1000, 998, 1000) = 998. A consumer cannot read offset 999 yet — it exists physically on the leader (and Follower-B) but isn't confirmed on every ISR member, so it's not yet committed/visible.

**29. What is the In-Sync Replica (ISR) set, and what removes a replica from it?**
The set of replicas (including the leader) considered "caught up enough" to count toward durability. A follower is removed if it hasn't made a successful fetch request within `replica.lag.time.max.ms` (default 30s) — a time-based liveness check, not a strict offset-distance check, so a follower that's briefly behind during a load spike but still actively fetching isn't unfairly evicted.

**30. Explain exactly what `min.insync.replicas=2` does, and why `acks=all` alone isn't enough for a real durability guarantee.**
`acks=all` only promises "wait for whoever is currently in the ISR" — if the ISR has shrunk to just the leader (size 1), `acks=all` silently degrades to single-copy durability with no error. `min.insync.replicas=2` closes that gap: if the ISR size would be below this number, the broker **rejects the write outright** (`NotEnoughReplicasException`) instead of accepting a falsely "safe" write — so durability is either guaranteed or the write fails loudly, never silently weakened.

**31. What is `unclean.leader.election.enable`, and what's the real-world trade-off it represents?**
If every ISR member for a partition is down simultaneously, the controller must choose: stay unavailable until an ISR member returns (consistency-favoring, the `false`/default behavior — no data loss, but no writes/reads for that partition meanwhile), or elect an out-of-sync replica anyway to restore availability immediately at the cost of permanently losing whatever committed records that replica was missing (`true`). It's literally the CAP theorem made into a single boolean flag.

**32. What is a leader epoch, and what specific bug does it prevent?**
A strictly increasing number bumped every time a partition gets a new leader, stamped into the record batch header and tracked in a checkpoint file. It prevents a subtle correctness issue where a follower, reconciling with a newly elected leader after a failover, could truncate its log based on stale/ambiguous offset information alone — the epoch gives followers an unambiguous reference point for exactly where their log diverges from the new leader's true history.

**33. [Scenario] You need to tolerate two simultaneous broker failures with zero downtime and zero data loss for a critical topic. What replication factor and `min.insync.replicas` do you need, and why?**
Replication factor 5, `min.insync.replicas=3`. With RF=5, losing 2 brokers still leaves 3 replicas — meeting `min.insync.replicas=3` so writes keep succeeding with no loss. (RF=3/`min.insync.replicas=2`, the common default recipe, only safely tolerates **one** simultaneous failure before writes would be rejected — that's the trade-off of the more common, cheaper setup.)

**34. Why doesn't Kafka rely on `fsync`-per-message for durability the way some databases do?**
Because fsync only protects against a single machine's crash, and a real outage (power loss, disk failure, network partition) can take out a whole machine regardless of fsync. Kafka's durability strategy is **replication across independent machines** (`acks=all` + `min.insync.replicas`) instead — which protects against the same single-machine failure class *and* against issues an fsync alone wouldn't catch, while avoiding the latency cost of fsync-ing every write. The OS's own page-cache flush behavior handles eventual physical persistence.

---

## Section E — Exactly-Once Semantics & Transactions

**35. What are the three Kafka delivery semantics, and which is the practical default in most production systems?**
At-most-once (may lose, never duplicates), at-least-once (never loses, may duplicate), exactly-once (neither loses nor duplicates, hardest to achieve). At-least-once + idempotent consumer-side handling is the practical default for the large majority of production systems — full exactly-once is reserved for cases where atomicity across multiple topics/partitions genuinely matters.

**36. Does Kafka's idempotent producer alone give you exactly-once? What's missing?**
It gives you exactly-once **writes to a single partition** (no duplicates from producer retries). It does **not** give you atomicity across multiple partitions/topics — e.g., "commit this output record AND this consumer offset together, or neither" requires the full Transactions API (`transactional.id`, `beginTransaction()`/`commitTransaction()`).

**37. [Scenario] Your exactly-once consume-process-produce pipeline reads from topic A, writes to topic B, and a consumer downstream of B uses `read_uncommitted`. What can go wrong?**
The downstream consumer can see records from transactions that later get aborted, or interleaved/partial views of a multi-record transaction before it's fully committed — defeating the purpose of using transactions in the first place. The consumer must use `isolation.level=read_committed` to have the broker filter out uncommitted/aborted-transaction records using the commit/abort control messages.

**38. How does the Transactions API prevent a "zombie" producer instance from corrupting data?**
Every `transactional.id` is bound to a producer epoch. Calling `initTransactions()` on startup bumps that epoch; any writes still arriving tagged with an older epoch (from a zombie instance that lost connectivity but didn't actually die) are rejected by the broker — the new instance effectively fences the old one out.

**39. Why can a long-running, stuck transaction block `read_committed` consumers from seeing records that come *after* it on the same partitions?**
Because a `read_committed` consumer can't yet know whether those later records should be presented "before" or interleaved with the eventual outcome (commit or abort) of the still-open transaction — until it resolves, the consumer has to hold back to avoid presenting an inconsistent view. `transaction.timeout.ms` exists specifically to bound how long a transaction can stay open and limit this blast radius.

---

## Section F — Storage, Compaction & Retention

**40. Why does Kafka split a partition into multiple segment files instead of one giant file?**
Retention and compaction operate on whole segment files, not individual records — deleting "everything older than 7 days" means deleting a couple of old segment files (cheap, O(number of segments)), not scanning/rewriting a potentially multi-terabyte single file. It also keeps index files (which are built per-segment) small and fast to binary-search.

**41. How does Kafka find a specific offset within a segment without scanning the whole file?**
Via the `.index` file, a sparse offset → byte-position mapping built roughly every `log.index.interval.bytes` (default 4KB). The broker binary-searches the (small, usually page-cache-resident) index for the nearest indexed offset at or before the target, seeks directly to that byte position in the `.log` file, then sequentially scans forward a few records at most.

**42. What's the difference between `cleanup.policy=delete` and `cleanup.policy=compact`, and when would you use each?**
`delete` removes whole segments once every record in them is older than `retention.ms` (or the partition exceeds `retention.bytes`) — appropriate for event streams where you want a rolling window of history (orders, clicks, logs). `compact` keeps only the latest record per key, regardless of age — appropriate for "current state per key" topics (a changelog of latest customer address, or Kafka's own internal `__consumer_offsets`/`__transaction_state` topics).

**43. What is a tombstone, and why is there a delay (`delete.retention.ms`) before it's fully removed?**
A tombstone is a record with the target key and a **null** value, signaling "delete this key" on a compacted topic. The delay exists so that lagging consumers, or any consumer doing a full bootstrap re-read from the start of the topic, get a real chance to actually observe the tombstone and process "this key was deleted" before it's purged — if it disappeared immediately, a slow consumer might never learn the key was deleted at all.

**44. [Scenario] You set `retention.ms=86400000` (1 day) on a high-volume topic, but disk usage on the brokers keeps climbing well past what 1 day of data should be. What would you check?**
Check actual segment sizes/ages on disk (`kafka-dump-log.sh` or just directory listing) — a common cause is `log.segment.bytes` set very large relative to actual topic throughput, meaning the *active* segment (which can never be deleted, by definition, since it's still being appended to) is holding far more than a day's worth of data before it even rolls over and becomes eligible for retention cleanup. Also check whether `retention.bytes` is set and conflicting, and confirm the log cleaner/retention thread isn't stuck or under-resourced.

**45. Why does Kafka rely on the OS page cache instead of maintaining its own application-level cache for hot reads?**
Because recently-written data is, by definition, already sitting in the OS page cache the moment it's written — reads of recent data are served at RAM speed for free, with no duplicate caching layer to keep in sync. It also means brokers can run with deliberately modest JVM heaps (avoiding GC pause problems) while leaving the rest of system RAM available to the OS for page caching, and a broker restart doesn't "cold start" this cache the way an in-process cache would.

---

## Section G — Controller, KRaft & Cluster Operations

**46. What does the Controller actually do, day to day?**
Watches for broker failures and triggers leader election for affected partitions, propagates metadata changes (new leaders, ISR changes, new/deleted topics) to every broker so their local metadata caches stay current, and manages topic creation/deletion and partition reassignment workflows.

**47. What is KRaft, and what specific operational problems does it solve compared to the ZooKeeper-based controller?**
KRaft replaces ZooKeeper with Kafka's own built-in Raft-based metadata quorum among designated controller nodes — metadata (what ZooKeeper used to hold) is itself stored as a replicated event log (`__cluster_metadata`). It removes the need to operate and version-match a separate distributed system, and it's measurably faster for controller failover and metadata propagation at very large partition counts, since changes are just Raft log entries replicated directly between controller nodes rather than round-tripping through ZooKeeper and then broadcasting to every broker.

**48. [Scenario] You need to perform a rolling restart of a 5-broker cluster with `min.insync.replicas=2` topics. What's the safe procedure, and what could go wrong if you restart brokers too quickly?**
Restart one broker at a time, and **wait for it to fully rejoin every partition's ISR** before moving to the next. If you restart a second broker before the first has fully caught back up, you risk an ISR briefly dropping below `min.insync.replicas` for some partitions — at best causing rejected writes, at worst (if you've also enabled unclean leader election) forcing a lossy failover, purely because of restart sequencing rather than any real infrastructure failure.

**49. What is preferred leader election, and why would leadership become unevenly distributed in the first place?**
Each partition has a "preferred leader" — conventionally the first broker in its replica assignment, chosen to spread leadership evenly across the cluster at topic-creation time. Over time, broker failures and failovers can leave leadership skewed (one broker ends up leading far more partitions than its fair share, after a string of incidents moved leadership away from it and it never moved back). Preferred leader election (manual, or automatic via `auto.leader.rebalance.enable=true`) restores the original even distribution once the preferred replica is healthy again.

**50. How would you safely increase the partition count of an existing, heavily-used topic, and what's the danger?**
You can add partitions (`kafka-topics.sh --alter --partitions N`), but you can never reduce them, and increasing partition count **changes the key→partition hash mapping for every existing key** going forward — meaning a key that used to consistently land on partition 2 might now land on partition 5, breaking the "same key always same partition" history that downstream consumers may have been relying on for ordering or for partition-local state (e.g., Kafka Streams state stores). Plan for this at topic-design time rather than reactively; if you must do it, downstream systems doing per-key stateful processing need to be aware their partition-to-key mapping just changed.

---

## Section H — Performance & Tuning

**51. [Scenario] Producer throughput is far below expectations even though the network and disks aren't saturated. What configs would you check first?**
`linger.ms` (likely 0, sending tiny under-full batches), `batch.size` (too small for your message size/volume), `compression.type` (none, wasting network bandwidth), and `acks` (if `all` with a slow follower dragging out every request, throughput suffers even with idle disks/network — check ISR health, not just the producer side).

**52. Why might increasing `num.io.threads` not help broker throughput if the bottleneck is actually disk I/O?**
More I/O threads only helps if the bottleneck is *thread availability* (requests queueing waiting for a free handler thread) — if the underlying disk itself is saturated, adding more threads just means more concurrent requests competing for the same disk bandwidth, with no net throughput gain and potentially worse latency from contention. Diagnose with actual disk I/O metrics before assuming it's a thread-pool sizing problem.

**53. What's the throughput/latency trade-off of `compression.type=gzip` vs `lz4` vs `zstd`?**
`gzip` gives the best compression ratio but is the slowest and most CPU-hungry — costs you both producer-side and consumer-side CPU, and at very high volume can become the actual bottleneck. `lz4` is fast with a decent ratio — a common high-throughput default. `zstd` generally gives the best balance of ratio and speed in modern benchmarks and is increasingly the recommended default for new pipelines.

**54. [Scenario] Consumer lag is growing steadily on one specific partition while others in the same topic are fine. What would you investigate?**
Likely a hot-key/skewed-partition problem (Section I) — check whether one key dominates that partition's traffic, or whether that partition's leader broker is under unrelated load (CPU/disk/network) compared to siblings. Also check if that specific consumer instance (the one assigned this partition) has a slower downstream dependency call pattern than its peers, since lag growth is specific to one partition's assigned consumer, not the whole group.

**55. What quota mechanisms does Kafka provide, and how do they actually throttle a misbehaving client without rejecting requests outright?**
Produce-byte-rate, fetch-byte-rate, and request-percentage (CPU-time) quotas, set per user/client-id. When a client exceeds its quota, the broker computes a delay and **returns the response late** rather than erroring — the client just experiences reduced effective throughput, visible as growing `produce-throttle-time-avg`/`fetch-throttle-time-avg` client metrics, rather than exceptions.

---

## Section I — Real-World Debugging Scenarios

**56. [Scenario] A consumer group has been stable for months. After a new feature deploy, the consuming service starts throwing exceptions on every poll, and the consumer never advances. What's your diagnosis path, and what's the long-term fix?**
Diagnosis: check if a producer team shipped a schema change (renamed/removed/retyped a field) without coordination — deserialization or downstream field access is failing on every record from that point forward, and since offset commits typically happen after successful processing, the consumer is stuck reprocessing the same poisoned record. Immediate fix: identify and either patch the consumer to handle the new shape, or route failing records to a DLQ so the partition can advance while you fix it properly. Long-term fix: mandate Schema Registry with `BACKWARD` or `FULL` compatibility enforcement so this class of change is rejected at publish time, not discovered in production.

**57. [Scenario] You notice `UnderReplicatedPartitions > 0` on one broker, but no consumer-facing errors yet. Do you treat this as urgent, and why?**
Yes, treat it as urgent even with zero visible symptoms — it means at least one partition's ISR has shrunk below its replication factor, so the durability margin has silently eroded. If a *second* failure hits while this window is open, you either lose data (unclean election enabled) or start rejecting writes (`min.insync.replicas` enforcing safety) — both are much worse outcomes than investigating now while the system still looks "fine."

**58. [Scenario] A single key (one VIP customer's user ID) is generating 40% of a topic's traffic, and the partition it lands on is consistently the slowest in the group. What are your options, in order of preference?**
First, confirm via per-partition throughput metrics that this really is the cause (not a broker-level issue affecting that partition's leader for unrelated reasons). Then: (1) if the use case tolerates it, "salt" that specific key with a rotating suffix via a custom partitioner so its traffic fans out across a few partitions, accepting ordering only within each salted sub-stream; (2) if true global ordering for that key is non-negotiable, consider whether that customer's events genuinely need dedicated handling/capacity rather than uniform per-key treatment; (3) increasing overall partition count helps baseline distribution for *other* keys but doesn't fix the one hot key's traffic share by itself.

**59. [Scenario] During a multi-broker network event, your cluster briefly loses every ISR member for a handful of partitions simultaneously. Writes to those partitions stop entirely rather than failing over. Is this expected, and what governs it?**
Yes, this is expected behavior with `unclean.leader.election.enable=false` (the recommended default) — with zero ISR members available, there is no replica that's provably caught up, so Kafka chooses unavailability over silently losing committed data. The real fix is prevention: replication factor ≥ 3 with rack/AZ awareness (`broker.rack`) so a single network event is far less likely to take out every replica of a partition simultaneously.

**60. [Scenario] You suspect duplicate records are reaching your downstream database despite `enable.idempotence=true` on the producer. Where do you look?**
Idempotence only deduplicates producer retries writing *to Kafka itself* — it says nothing about the consumer side. Check whether the consumer's offset commit is happening *before* the side effect completes (classic at-least-once timing bug: process → crash before commit → reprocess on restart → duplicate DB write), and whether the downstream write is itself idempotent (upsert/`ON CONFLICT`, or a dedup check keyed by `(topic, partition, offset)` or a business key) — if not, that's the actual gap, not the producer.

**61. [Scenario] A producer using `transactional.id="order-service-1"` crashes and restarts under the exact same ID. What happens to any transaction that was in-flight when it crashed?**
On restart, calling `initTransactions()` bumps the producer epoch for that `transactional.id`, fencing off any zombie continuation of the old instance. The Transaction Coordinator, using its durably-persisted state in `__transaction_state`, resolves the abandoned in-flight transaction (typically aborting it, since it never reached a commit decision), so no half-committed data leaks through, and the new instance starts clean.

**62. [Scenario] Your team wants to "just delete the bad messages" from a topic after a buggy producer wrote malformed records. What are your actual options, given Kafka's append-only model?**
You cannot delete or edit individual records in place. Options: (a) for a compacted topic, produce a tombstone for the affected key(s) and wait out `delete.retention.ms`; (b) for a non-compacted topic, you generally cannot remove specific records short of deleting/truncating whole segments (`kafka-delete-records.sh` can set a new low-water-mark per partition, effectively discarding everything before a given offset, but that's all-or-nothing up to that point, not selective); (c) most practically, leave the bad records in place and ensure consumers are robust to skipping/handling them, fixing forward rather than trying to surgically edit history.

**63. [Scenario] A Kafka Streams application crashes and is replaced by a new instance on a different machine. Its local RocksDB state store for a windowed aggregation is gone. What happens?**
The new instance rebuilds the state store from scratch by replaying its backing **changelog topic** (every state store is backed by one) from the beginning — exactly the same "replay a compacted-style topic to reconstruct current state per key" pattern used generally for compacted topics. This is slower than having a warm cache but guarantees correctness; for very large state stores, this rebuild time is a real operational consideration when planning for instance failure/replacement.

**64. [Scenario] You're asked to reduce a topic's storage footprint without losing the ability to replay the last week of data for new consumers. What do you change?**
Nothing about replay capability needs to change if `retention.ms` is already 7 days and that's your real requirement — check instead whether `compression.type` is set (uncompressed topics waste significant disk for no benefit if not already compressed), whether `log.segment.bytes` is oversized relative to throughput (delaying retention cleanup eligibility, Section F Q44), and whether you actually need `retention.bytes` as an additional hard cap in case volume spikes unpredictably.

**65. [Scenario] Leadership for a topic's partitions is heavily skewed toward two of your five brokers after a series of incidents over the past month, even though all five brokers are now healthy. What's the fix, and is it disruptive?**
This is leadership imbalance, not a replication or durability problem — run preferred leader election (`kafka-leader-election.sh`, or enable `auto.leader.rebalance.enable=true` for it to happen automatically going forward) to move leadership back to each partition's preferred replica. It's a lightweight operation (the data doesn't move, only which replica is "active" for reads/writes changes) and is far less disruptive than a full partition reassignment, which would actually move replica data between brokers.

---

## Section J — Rapid-Fire Definitions (closing round)

**66. What's the difference between a consumer and a consumer group?** A consumer is one client instance; a consumer group is a named set of consumers cooperatively sharing a topic's partitions, with each partition owned by exactly one group member at a time.

**67. What's the difference between `CreateTime` and `LogAppendTime`?** `CreateTime` is the timestamp the producer set when the record was created; `LogAppendTime` is overwritten by the broker at the moment of append — used when you don't trust client clocks and want broker-authoritative, consistent time-based behavior (e.g., for predictable retention regardless of producer clock skew).

**68. What's the difference between a source connector and a sink connector in Kafka Connect?** A source connector moves data *into* Kafka from an external system (e.g., database CDC via Debezium); a sink connector moves data *out of* Kafka into an external system (e.g., S3, Elasticsearch).

**69. What does MirrorMaker do, and what guarantee does it not give you that in-cluster replication does?** It replicates data between separate Kafka clusters (for DR, migration, or aggregation). Unlike in-cluster ISR-based replication, cross-cluster replication is inherently asynchronous, so there's always some lag — a failover to the secondary cluster can lose the most recent, not-yet-replicated window of data.

**70. What's the difference between SASL/PLAIN and SASL/SCRAM?** `PLAIN` is simple username/password sent essentially as-is (safe only layered under TLS); `SCRAM` is a salted challenge-response mechanism that's stronger without depending on an external auth system.

**71. What does an ACL actually control in Kafka?** Fine-grained permission per principal (user/service), per resource (a specific topic, consumer group, or cluster-wide operation), per operation type (Read, Write, Create, Describe, etc.) — enforced by the broker's Authorizer on every request.

**72. Name two JMX metrics you'd put on a critical alert for a Kafka cluster, and why those two.** Under-replicated partitions (durability margin eroding, leading indicator of many failure modes) and consumer lag growth rate (processing falling behind, leading indicator of downstream/capacity problems) — together they predict most other incidents before they become full outages.

**73. What's the practical difference between `kafka-reassign-partitions.sh` and preferred leader election?** Reassignment actually **moves replica data** between brokers (e.g., to rebalance disk usage or decommission a broker) — a heavier, I/O-intensive operation. Preferred leader election only changes **which existing replica is active as leader** — the data doesn't move, it's lightweight.

**74. What is `replica.fetch.max.bytes` and what's it bounding?** The maximum amount of data the leader will return to a single follower in one fetch response for a given partition — bounds how much a single replication fetch can pull at once, balancing replication throughput against memory/network burst size.

**75. In one sentence, what's the single biggest mental-model shift from "queue" thinking to "Kafka" thinking?** Stop thinking "messages get removed once consumed" and start thinking "messages are a durable, replayable log that multiple independent readers can each traverse at their own pace and position."
