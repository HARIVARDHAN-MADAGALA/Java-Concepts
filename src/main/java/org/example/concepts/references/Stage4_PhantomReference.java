package org.example.concepts.references;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/// Stage 4 — PhantomReference
/// Object is already collected by GC — PhantomReference is enqueued AFTER collection
/// get() always returns null — you can NEVER access the object through it
/// Best for: cleanup of native/external resources after GC (files, sockets, off-heap memory)
///
/// This is how Java's Cleaner API (Java 9+) works internally

public class Stage4_PhantomReference {

    static class NativeResource {
        String name;

        NativeResource(String name) {
            this.name = name;
            System.out.println("Acquired native resource: " + name);
        }

        // imagine this opens a file handle or allocates native memory
        void releaseNative() {
            System.out.println("Native resource released: " + name);
        }
    }

    // ── a cleanup action tied to the phantom reference ──
    static class ResourceCleaner extends PhantomReference<NativeResource> {

        private final String resourceName; // store what we need to clean up
                                           // we can't use .get() — always null

        ResourceCleaner(NativeResource resource, ReferenceQueue<NativeResource> queue) {
            super(resource, queue);
            this.resourceName = resource.name; // capture before GC takes it
        }

        void cleanup() {
            System.out.println("Phantom cleanup triggered for: " + resourceName);
            // do actual native cleanup here — close file, free memory, etc.
        }
    }

    public static void main(String[] args) throws Exception {

        // ── ReferenceQueue — GC puts phantom refs here after collecting the object ──
        ReferenceQueue<NativeResource> queue = new ReferenceQueue<>();

        // create object + wrap in phantom reference
        NativeResource resource = new NativeResource("FileHandle_001");
        ResourceCleaner phantom  = new ResourceCleaner(resource, queue);

        System.out.println("\n── phantom.get() always returns null ──");
        System.out.println("phantom.get() = " + phantom.get()); // always null

        System.out.println("\n── removing strong reference ──");
        resource = null; // object now eligible for GC

        System.gc();
        Thread.sleep(200);

        // ── poll the queue to check if GC has collected the object ──
        System.out.println("\n── polling ReferenceQueue ──");
        ResourceCleaner collected = (ResourceCleaner) queue.poll();

        if (collected != null) {
            collected.cleanup(); // safe to clean up now — object is already gone
        } else {
            System.out.println("Not yet collected — try again later");
        }

        // ── key difference from finalize() ──
        // finalize() runs BEFORE GC collects → object can be resurrected (bad)
        // PhantomReference runs AFTER GC collects → object is definitely gone (safe)
        // This is why PhantomReference replaced finalize() for resource cleanup

        // ── Java 9+ Cleaner API uses this pattern internally ──
        // java.lang.ref.Cleaner is the modern, safe replacement for finalize()
        System.out.println("\n── Java 9+ Cleaner (modern way) ──");
        var cleaner = java.lang.ref.Cleaner.create();
        Object obj  = new Object();
        cleaner.register(obj, () -> System.out.println("Cleaner action ran — object collected"));
        obj = null;
        System.gc();
        Thread.sleep(200);
    }
}
