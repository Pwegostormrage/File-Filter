package ru.shift.filter.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberStats {
    private long count = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = BigDecimal.ZERO;

    public void accept(BigDecimal x) {
        if (x == null) return;
        count++;

        if (min == null || x.compareTo(min) < 0) min = x;
        if (max == null || x.compareTo(max) > 0) max = x;

        sum = sum.add(x);
    }

    public long count() {
        return count;
    }

    public String fullReport() {
        if (count == 0) {
            return "count=0";
        }

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 10, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        return "count=" + count +
                "\nmin=" + min.toPlainString() +
                "\nmax=" + max.toPlainString() +
                "\nsum=" + sum.toPlainString() +
                "\navg=" + avg.toPlainString();
    }
}
