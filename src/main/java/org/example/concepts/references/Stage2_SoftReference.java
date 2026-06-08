package org.example.concepts.references;

import java.lang.ref.Cleaner;
import java.lang.ref.SoftReference;

/// Stage 2 — SoftReference
/// JVM keeps the object as long as memory is available
/// Collected ONLY when JVM is about to throw OutOfMemoryError
/// Best for: in-memory caches (images, thumbnails, computed results)

public class Stage2_SoftReference {

    static final Cleaner CLEANER = Cleaner.create();

    static class CachedImage {
        String filename;
        byte[] data;                        // simulate image data

        CachedImage(String filename) {
            this.filename = filename;
            this.data     = new byte[1024]; // 1KB simulated image
            System.out.println("Loaded from disk: " + filename);
            CLEANER.register(this, () -> System.out.println("GC collected image: " + filename));
        }
    }

    // ── simple image cache using SoftReference ──
    static class ImageCache {

        // value is SoftReference — GC can collect it when memory is low
        private final java.util.Map<String, SoftReference<CachedImage>> cache
                = new java.util.HashMap<>();

        CachedImage get(String filename) {
            SoftReference<CachedImage> ref = cache.get(filename);

            // ref exists but object may have been GC'd — always check get()
            if (ref != null && ref.get() != null) {
                System.out.println("Cache HIT: " + filename);
                return ref.get();
            }

            // cache miss or GC'd — reload from disk
            System.out.println("Cache MISS: " + filename);
            CachedImage image = new CachedImage(filename);
            cache.put(filename, new SoftReference<>(image));
            return image;
        }
    }

    public static void main(String[] args) throws Exception {

        ImageCache cache = new ImageCache();

        // first call — loads from disk
        CachedImage img1 = cache.get("photo1.jpg");

        // second call — served from cache (soft ref still alive)
        CachedImage img2 = cache.get("photo1.jpg");

        System.out.println("\n── manually checking SoftReference ──");
        SoftReference<CachedImage> softRef = new SoftReference<>(new CachedImage("photo2.jpg"));

        System.out.println("Before GC: " + softRef.get());  // object available

        System.gc();
        Thread.sleep(100);

        // SoftReference survives normal GC — only dies under memory pressure
        System.out.println("After GC : " + softRef.get());  // likely still available

        // ── key point ──
        // If you used strong reference in the cache map:
        //   Map<String, CachedImage> → images NEVER get collected → OutOfMemoryError
        // With SoftReference:
        //   JVM will free cached images when it needs memory → safe cache
    }
}
