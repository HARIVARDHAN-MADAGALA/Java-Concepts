# ClassLoader

## The 4 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_WhatIsClassLoader | ClassLoader hierarchy, parent delegation, bootstrap/platform/app loaders |
| 2 | Stage2_ClassLoadingLifecycle | Loading → Linking → Initialization, static blocks, forName vs loadClass |
| 3 | Stage3_CustomClassLoader | Writing a custom ClassLoader, defineClass, class identity |
| 4 | Stage4_HotReloadSimulation | Class isolation, hot-reload pattern, classloader-scoped unloading |

---

## ClassLoader Hierarchy

```
Bootstrap ClassLoader      ← java.*, javax.* from rt.jar (null in Java)
      ↑ parent
Platform ClassLoader       ← extensions (Java 9+)
      ↑ parent
Application ClassLoader    ← your classpath classes
      ↑ parent
Custom ClassLoader         ← plugins, hot-reload, isolation
```

## Parent Delegation Model
1. ClassLoader asks parent to load first
2. Only if parent fails → child tries itself
3. Protects java.lang.String etc. from being shadowed

## Class Loading Lifecycle
```
Loading → Verification → Preparation → Resolution → Initialization
```
- **Loading**: bytecode → `Class` object in heap
- **Preparation**: static fields get default values (0, null, false)
- **Initialization**: static blocks + static field assignments run (once, lazily)

## Class Identity Rule
> Two `Class` objects are the same type **only if** they have the same binary name **and** were loaded by the **same ClassLoader** instance.

```java
ClassLoader l1 = new MyLoader();
ClassLoader l2 = new MyLoader();
Class<?> c1 = l1.loadClass("com.example.Foo");
Class<?> c2 = l2.loadClass("com.example.Foo");
c1 == c2  // false — two distinct types
```

## Real-World Uses
| Use Case | How ClassLoader is used |
|----------|------------------------|
| Tomcat | Each webapp gets its own ClassLoader for isolation |
| Spring DevTools | Reloads app ClassLoader on file change |
| OSGi / IntelliJ plugins | Each plugin has isolated ClassLoader |
| Java agents / bytecode instrumentation | Custom loader transforms bytecode before defineClass |
