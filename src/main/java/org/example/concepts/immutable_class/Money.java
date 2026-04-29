package org.example.concepts.immutable_class;

/// 🔬 Example 1 — A correct immutable class (simple fields)
public final class Money {

    private final String currency;
    private final long amountInPaise; // using long to avoid floating point issues

    public Money(String currency, long amountInPaise) {
        if (currency == null) throw new NullPointerException("currency");
        this.currency = currency;
        this.amountInPaise = amountInPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public long getAmountInPaise() {
        return amountInPaise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money other = (Money) o;
        return amountInPaise == other.amountInPaise && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return 31 * currency.hashCode() + Long.hashCode(amountInPaise);
    }

    @Override
    public String toString() {
        return currency + " " + (amountInPaise / 100.0);
    }
}

