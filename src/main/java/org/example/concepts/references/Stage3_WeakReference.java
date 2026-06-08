package org.example.concepts.references;

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/// Stage 3 — WeakReference
/// Object is collected at the NEXT GC run — regardless of memory availability
/// Best for: avoiding memory leaks in listeners, metadata maps, WeakHashMap

public class Stage3_WeakReference {

    static final Cleaner CLEANER = Cleaner.create();

    static class Listener {
        String name;

        Listener(String name) {
            this.name = name;
            System.out.println("Registered listener: " + name);
            CLEANER.register(this, () -> System.out.println("GC collected listener: " + name));
        }
    }

    public static void main(String[] args) throws Exception {

        // ── basic WeakReference ──
        System.out.println("── Basic WeakReference ──");
        WeakReference<Listener> weakRef = new WeakReference<>(new Listener("ButtonClickListener"));

        System.out.println("Before GC: " + weakRef.get()); // object available

        System.gc();
        Thread.sleep(200);

        // WeakReference object has no strong reference pointing to it
        // so GC collects it on the very next run
        System.out.println("After GC : " + weakRef.get()); // null — collected

        // ── WeakReference with a strong reference still alive ──
        System.out.println("\n── WeakRef + strong ref together ──");
        Listener strong = new Listener("StrongListener");
        WeakReference<Listener> weak = new WeakReference<>(strong);

        System.gc();
        Thread.sleep(200);
        System.out.println("After GC (strong alive): " + weak.get()); // NOT null — strong ref protects it

        strong = null; // remove strong reference
        System.gc();
        Thread.sleep(200);
        System.out.println("After GC (strong removed): " + weak.get()); // null — now collected

        // ── WeakHashMap — the most practical use of WeakReference ──
        System.out.println("\n── WeakHashMap ──");

        // keys in WeakHashMap are WeakReferences
        // when the key object has no other strong reference → entry is auto-removed
        WeakHashMap<Listener, String> map = new WeakHashMap<>();

        Listener key1 = new Listener("key1");
        Listener key2 = new Listener("key2");

        map.put(key1, "data1");
        map.put(key2, "data2");
        System.out.println("Map size before GC: " + map.size()); // 2

        key1 = null; // remove strong reference to key1
        System.gc();
        Thread.sleep(200);

        // key1 entry is automatically removed from map — no memory leak
        System.out.println("Map size after GC : " + map.size()); // 1 — key1 entry gone

        // ── why this matters ──
        // Normal HashMap with Listener keys:
        //   map.put(listener, data) → listener object can NEVER be GC'd → memory leak
        // WeakHashMap:
        //   when listener goes out of scope → GC removes it + map entry automatically
    }
}
