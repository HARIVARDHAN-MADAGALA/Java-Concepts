# Java Date & DateTime — Classes, Interfaces, and Hierarchy

Java has **two generations** of date/time APIs:

| | Legacy API (pre-Java 8) | Modern API (Java 8+, `java.time`, JSR-310) |
|---|---|---|
| Package | `java.util`, `java.sql` | `java.time`, `java.time.temporal`, `java.time.format` |
| Mutability | **Mutable** (not thread-safe) | **Immutable** (thread-safe) |
| Design | Confusing, 0-indexed months, poor API | Clean, fluent, inspired by Joda-Time |
| Status | Legacy, avoid in new code | Recommended for all new code |

---

## 1. Full Hierarchy Diagram

```
java.lang.Object
│
├── java.util.Date   implements Serializable, Cloneable, Comparable<Date>
│      │
│      ├── java.sql.Date        (date only — yyyy-MM-dd)
│      ├── java.sql.Time        (time only — HH:mm:ss)
│      └── java.sql.Timestamp   (date + time + nanoseconds)
│
├── java.util.Calendar   [abstract] implements Serializable, Cloneable, Comparable<Calendar>
│      │
│      └── java.util.GregorianCalendar
│
└── java.time (modern API — classes don't extend each other, they implement shared interfaces)

   INTERFACES (java.time.temporal / java.time.chrono)
   ├── TemporalAccessor                       (read-only access to a point in time)
   │     └── Temporal  (extends TemporalAccessor)   (read + write/manipulate)
   ├── TemporalAdjuster                        (functional interface — "adjust this temporal")
   ├── TemporalAmount                          (an amount of time: Period, Duration)
   ├── TemporalQuery<R>                        (functional interface — extract custom info)
   ├── ChronoLocalDate        extends Temporal, Comparable<ChronoLocalDate>
   ├── ChronoLocalDateTime<D> extends Temporal, Comparable<ChronoLocalDateTime<?>>
   ├── ChronoZonedDateTime<D> extends Temporal, Comparable<ChronoZonedDateTime<?>>
   └── ChronoPeriod           extends TemporalAmount

   CLASSES (java.time) — all final, immutable
   ├── LocalDate       implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable
   ├── LocalTime       implements Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable
   ├── LocalDateTime   implements ChronoLocalDateTime<LocalDate>, Temporal, TemporalAdjuster, Serializable
   ├── ZonedDateTime   implements ChronoZonedDateTime<LocalDate>, Temporal, Serializable
   ├── OffsetDateTime  implements Temporal, TemporalAdjuster, Comparable<OffsetDateTime>, Serializable
   ├── OffsetTime      implements Temporal, TemporalAdjuster, Comparable<OffsetTime>, Serializable
   ├── Instant         implements Temporal, TemporalAdjuster, Comparable<Instant>, Serializable
   ├── Duration        implements TemporalAmount, Comparable<Duration>, Serializable   (time-based: H/M/S)
   ├── Period          implements ChronoPeriod, Serializable                            (date-based: Y/M/D)
   ├── Year / YearMonth / MonthDay   (partial dates)
   ├── ZoneId          [abstract]
   │      └── ZoneOffset (extends ZoneId)
   └── DateTimeFormatter   (utility — not a temporal type at all)
```

---

## 2. Legacy API (`java.util`)

### `java.util.Date`
Represents a single instant in time — internally just **milliseconds since the epoch** (Jan 1, 1970, 00:00:00 UTC).

- Mutable, not thread-safe.
- Most field-based methods (`getYear()`, `setMonth()`, etc.) are **deprecated** — use `Calendar` or `java.time` instead.

| Method | Purpose |
|---|---|
| `Date()` | current date/time |
| `Date(long millis)` | specific instant from epoch millis |
| `long getTime()` | millis since epoch |
| `void setTime(long millis)` | mutate the instant |
| `boolean before(Date d)` / `after(Date d)` | comparison |
| `int compareTo(Date d)` | from `Comparable` |
| `Instant toInstant()` | bridge to modern API (Java 8+) |

### `java.sql.Date`, `java.sql.Time`, `java.sql.Timestamp`
JDBC-specific subclasses of `java.util.Date`:
- **`java.sql.Date`** — date only (year/month/day), time portion zeroed out. Maps to SQL `DATE`.
- **`java.sql.Time`** — time only. Maps to SQL `TIME`.
- **`java.sql.Timestamp`** — date + time + nanosecond precision. Maps to SQL `TIMESTAMP`.

### `java.util.Calendar` (abstract)
A field-based, mutable calendar system. You don't instantiate it directly — use the factory method.

| Method | Purpose |
|---|---|
| `static Calendar getInstance()` | factory — returns a `GregorianCalendar` by default |
| `int get(int field)` | e.g. `cal.get(Calendar.YEAR)` |
| `void set(int field, int value)` | e.g. `cal.set(Calendar.MONTH, 5)` (months are **0-indexed**!) |
| `void add(int field, int amount)` | adds, rolling over to other fields (e.g. +1 month can change year) |
| `void roll(int field, boolean up)` | adds without affecting larger fields |
| `Date getTime()` / `void setTime(Date d)` | bridge to `Date` |
| `boolean before(Object cal)` / `after(Object cal)` | comparison |

Common fields: `Calendar.YEAR`, `MONTH`, `DAY_OF_MONTH`, `HOUR_OF_DAY`, `MINUTE`, `SECOND`, `DAY_OF_WEEK`.

### `java.util.GregorianCalendar`
The only concrete subclass normally used — implements the standard Gregorian calendar (leap years, month lengths, etc.). `Calendar.getInstance()` returns one of these.

---

## 3. Modern API (`java.time`, JSR-310, Java 8+)

### Core interfaces

#### `TemporalAccessor`
Read-only view of a point in time. Base interface for almost everything in `java.time`.

| Method | Purpose |
|---|---|
| `boolean isSupported(TemporalField field)` | does this object support this field? |
| `int get(TemporalField field)` | get value of a field |
| `long getLong(TemporalField field)` | get value as a `long` |
| `<R> R query(TemporalQuery<R> query)` | extract custom info (e.g. `TemporalQueries.zoneId()`) |

#### `Temporal` (extends `TemporalAccessor`)
Adds the ability to **manipulate** a point in time (not just read it).

| Method | Purpose |
|---|---|
| `Temporal with(TemporalField field, long value)` | return a copy with one field changed |
| `Temporal plus(long amount, TemporalUnit unit)` | add an amount |
| `Temporal minus(long amount, TemporalUnit unit)` | subtract |
| `long until(Temporal endExclusive, TemporalUnit unit)` | distance between two temporals |

#### `TemporalAdjuster` (functional interface)
Single method: `Temporal adjustInto(Temporal temporal)`. Used via `.with(...)`:
```java
LocalDate firstDayOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());
LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
```

#### `TemporalAmount`
Represents a quantity of time rather than a point in time. Implemented by `Period` and `Duration`.

| Method | Purpose |
|---|---|
| `long get(TemporalUnit unit)` | value for that unit |
| `List<TemporalUnit> getUnits()` | which units this amount uses |
| `Temporal addTo(Temporal t)` / `subtractFrom(Temporal t)` | apply the amount to a temporal |

#### `ChronoLocalDate` / `ChronoLocalDateTime<D>` / `ChronoZonedDateTime<D>`
Chronology-agnostic versions of date types (support non-ISO calendars like Japanese or Hijrah). In practice, `LocalDate`, `LocalDateTime`, and `ZonedDateTime` are the **ISO-chronology implementations** of these interfaces — you'll rarely use the interfaces directly unless writing calendar-system-agnostic code.

---

### Core classes

#### `LocalDate`
Date only — year, month, day. No time, no time zone.

| Method | Purpose |
|---|---|
| `static LocalDate now()` | today |
| `static LocalDate of(int y, int m, int d)` | specific date |
| `static LocalDate parse(CharSequence text)` | parse ISO string `"2026-06-24"` |
| `int getYear()`, `getMonthValue()`, `getDayOfMonth()` | accessors |
| `DayOfWeek getDayOfWeek()` | e.g. `WEDNESDAY` |
| `LocalDate plusDays(long n)` / `minusMonths(long n)` | arithmetic |
| `boolean isLeapYear()` | leap year check |
| `boolean isBefore(LocalDate o)` / `isAfter(LocalDate o)` | comparison |
| `LocalDateTime atTime(LocalTime t)` | combine with a time |
| `long toEpochDay()` | days since epoch |

#### `LocalTime`
Time only — hour, minute, second, nanosecond. No date, no zone.

| Method | Purpose |
|---|---|
| `static LocalTime now()` | current time |
| `static LocalTime of(int h, int m, int s)` | specific time |
| `int getHour()`, `getMinute()`, `getSecond()` | accessors |
| `LocalTime plusHours(long n)` | arithmetic |
| `int toSecondOfDay()` | seconds since midnight |

#### `LocalDateTime`
`LocalDate` + `LocalTime` together. **No time zone** — represents a "wall clock" date-time.

| Method | Purpose |
|---|---|
| `static LocalDateTime now()` | current date-time |
| `static LocalDateTime of(LocalDate d, LocalTime t)` | combine |
| `LocalDate toLocalDate()` / `LocalTime toLocalTime()` | split apart |
| `ZonedDateTime atZone(ZoneId zone)` | attach a time zone |
| `String format(DateTimeFormatter f)` | formatting |

#### `ZonedDateTime`
`LocalDateTime` + `ZoneId`. Fully zone-aware, handles **daylight saving time** rule changes for that region.

| Method | Purpose |
|---|---|
| `static ZonedDateTime now(ZoneId zone)` | current time in a zone |
| `static ZonedDateTime of(LocalDateTime ldt, ZoneId zone)` | combine |
| `ZonedDateTime withZoneSameInstant(ZoneId zone)` | convert to another zone (same instant) |
| `Instant toInstant()` | convert to UTC instant |
| `ZoneId getZone()` | get the zone |

#### `OffsetDateTime`
`LocalDateTime` + fixed `ZoneOffset` (e.g. `+05:30`) — **no DST rules**, just a fixed offset. Commonly required by APIs/standards like ISO-8601 and many database drivers, since it's unambiguous without needing a full time-zone database.

#### `OffsetTime`
`LocalTime` + fixed `ZoneOffset`. Rare; used when only the time-of-day with an offset matters.

#### `Instant`
A single point on the timeline — seconds + nanoseconds since the epoch (UTC). The closest modern equivalent of `Date.getTime()`; ideal for timestamps, logging, machine-to-machine communication.

| Method | Purpose |
|---|---|
| `static Instant now()` | current instant |
| `static Instant ofEpochSecond(long s)` | from epoch seconds |
| `long getEpochSecond()` / `long toEpochMilli()` | conversions |
| `Instant plusSeconds(long s)` | arithmetic |
| `boolean isBefore(Instant o)` | comparison |

#### `Duration`
A **time-based** amount (hours/minutes/seconds/nanos) — for measuring elapsed time.

| Method | Purpose |
|---|---|
| `static Duration between(Temporal start, Temporal end)` | elapsed time between two temporals |
| `static Duration ofMinutes(long m)` / `ofSeconds(long s)` | factory |
| `long toMinutes()` / `toHours()` | conversions |
| `Duration plus(Duration d)` | arithmetic |

```java
Duration d = Duration.between(start, end);
System.out.println(d.toMillis() + " ms elapsed");
```

#### `Period`
A **date-based** amount (years/months/days) — for measuring calendar differences.

| Method | Purpose |
|---|---|
| `static Period between(LocalDate start, LocalDate end)` | date difference |
| `static Period of(int y, int m, int d)` | factory |
| `int getYears()`, `getMonths()`, `getDays()` | accessors |

```java
Period p = Period.between(dob, LocalDate.now());
System.out.println(p.getYears() + " years old");
```

> **Duration vs Period:** `Duration` is precise, exact-second arithmetic (good for `Instant`/`LocalTime`). `Period` is calendar-aware (knows that February can have 28 or 29 days, that months have different lengths).

#### `Year`, `YearMonth`, `MonthDay`
Lightweight "partial date" classes — e.g. `YearMonth.of(2026, 6)` for "June 2026" without a specific day. Useful for things like credit-card expiry dates or monthly billing cycles.

#### `ZoneId` / `ZoneOffset`
- **`ZoneId`** — a named time-zone region with full DST rules, e.g. `ZoneId.of("Asia/Kolkata")`.
- **`ZoneOffset`** (extends `ZoneId`) — a fixed offset from UTC with no DST rules, e.g. `ZoneOffset.of("+05:30")`.

#### `DateTimeFormatter`
Not a temporal type — a utility for formatting and parsing.

| Method | Purpose |
|---|---|
| `static DateTimeFormatter ofPattern(String pattern)` | custom pattern, e.g. `"dd-MM-yyyy"` |
| `String format(TemporalAccessor t)` | format a date/time to String |
| `<T> T parse(CharSequence text, TemporalQuery<T> query)` | parse a String back to a temporal type |

```java
DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
String s = LocalDateTime.now().format(f);
LocalDateTime parsed = LocalDateTime.parse("24-06-2026 14:30:00", f);
```

---

## 4. Quick decision guide

| Need | Use |
|---|---|
| Just a date (birthday, due date) | `LocalDate` |
| Just a time (alarm, opening hours) | `LocalTime` |
| Date + time, no zone (timestamps in a single-region app) | `LocalDateTime` |
| Date + time, zone-aware (global app, scheduling across regions) | `ZonedDateTime` |
| Machine timestamp / logging / DB `TIMESTAMP WITH TIME ZONE` | `Instant` |
| Elapsed time measurement (e.g. API latency) | `Duration` |
| Calendar-based difference (age, subscription length) | `Period` |
| Legacy code interop (`java.sql.Timestamp`, old libraries) | Convert via `.toInstant()` / `Date.from(Instant)` |

```java
// Legacy <-> Modern bridge
Date legacyDate = new Date();
Instant instant = legacyDate.toInstant();
Date backToLegacy = Date.from(instant);

LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
```
