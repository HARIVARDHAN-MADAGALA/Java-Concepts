## ⚠️ First—what actually causes deadlock?

Deadlock isn’t random. It needs **all 4 conditions**:

1. Mutual exclusion (locks)
2. Hold and wait
3. No preemption
4. **Circular wait** ← THIS is what you’ll create

If you don’t create a circular dependency, no deadlock.
