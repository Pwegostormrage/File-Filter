package ru.shift.filter.stats;

public final class StringStats {
    private long count = 0;
    private int minLen = Integer.MAX_VALUE;
    private int maxLen = 0;

    public void accept(String s) {
        if (s == null) s = "";
        count++;
        int len = s.length();
        if (len < minLen) minLen = len;
        if (len > maxLen) maxLen = len;
    }

    public long count() {
        return count;
    }

    public String fullReport() {
        if (count == 0) {
            return "count=0";
        }
        return "count=" + count +
                "\nminLen=" + minLen +
                "\nmaxLen=" + maxLen;
    }
}
