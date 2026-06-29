# Interpolation Search

In **DSA (Searching)**, **Interpolation Search** estimates where the target value is likely to be instead of always checking the middle element (like **Binary Search**).

## Position Formula

```text
pos = low + ((key - A[low]) * (high - low)) / (A[high] - A[low])
```

### Where

- `low` = Starting index
- `high` = Ending index
- `key` = Element to search
- `A[low]` = Value at `low`
- `A[high]` = Value at `high`
- `pos` = Estimated index where the key might be

---

## Example

```text
Array = [10, 20, 30, 40, 50, 60, 70]
Key = 50

low = 0
high = 6

pos = 0 + ((50 - 10) * (6 - 0)) / (70 - 10)
    = (40 * 6) / 60
    = 4
```

So, it directly checks **index 4**, where `A[4] = 50`, and the search finishes in **one step**.

---

## Time Complexity

| Case | Complexity |
|------|------------|
| Best Case | **O(1)** |
| Average Case | **O(log log n)** (when values are uniformly distributed) |
| Worst Case | **O(n)** (when values are unevenly distributed) |

---

## Conditions for Using Interpolation Search

- ✅ Array must be **sorted**.
- ✅ Values should be **uniformly distributed** for good performance.
- ❌ Not suitable for **highly skewed** or **unevenly distributed** data.