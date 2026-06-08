package org.example.concepts.references;

import java.lang.ref.Cleaner;

/// Stage 1 — Strong Reference (default)
/// This is what you use every day — normal variable assignment
/// GC will NEVER collect the object as long as the variable holds it

public class Stage1_StrongReference {

    static final Cleaner CLEANER = Cleaner.create();

    static class HeavyObject {
        String name;

        HeavyObject(String name) {
            this.name = name;
            System.out.println("Created: " + name);
            // register cleanup action — runs AFTER GC collects this object
            CLEANER.register(this, () -> System.out.println("GC collected: " + name));
        }
    }

    public static void main(String[] args) throws Exception {

        // ── strong reference — GC will NOT collect this ──
        HeavyObject obj = new HeavyObject("StrongObject");
        System.out.println("obj is: " + obj.name);

        System.gc();
        Thread.sleep(100);
        System.out.println("After GC — obj still alive: " + obj.name); // still accessible

        // ── make it eligible for GC by removing the reference ──
        obj = null; // now no strong reference points to it
        System.gc();
        Thread.sleep(200);
        System.out.println("After null + GC — obj: " + obj); // null, cleaner action printed above

        // ── the classic memory leak pattern ──
        // static map holds strong references → objects NEVER get collected
        // java.util.HashMap<String, HeavyObject> cache = new HashMap<>();
        // cache.put("key", new HeavyObject("Leaked"));
        // even if you "forget" about it, GC can't touch it → memory leak
        // Solution: use WeakHashMap or WeakReference instead
    }
}
