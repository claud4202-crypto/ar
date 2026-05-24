package me.arisdonate.util;

/** "1h", "30m", "1d2h" → миллисекунды. */
public final class TimeUtil {

    private TimeUtil() {}

    public static long parseDuration(String s) {
        if (s == null) return -1;
        long total = 0;
        StringBuilder num = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) { num.append(c); continue; }
            if (num.length() == 0) return -1;
            long val = Long.parseLong(num.toString());
            num.setLength(0);
            switch (Character.toLowerCase(c)) {
                case 's': total += val * 1000L; break;
                case 'm': total += val * 60_000L; break;
                case 'h': total += val * 3_600_000L; break;
                case 'd': total += val * 86_400_000L; break;
                case 'w': total += val * 604_800_000L; break;
                default: return -1;
            }
        }
        if (num.length() > 0) total += Long.parseLong(num.toString()) * 1000L;
        return total;
    }

    public static String fmt(long ms) {
        long s = ms / 1000;
        long h = s / 3600; s %= 3600;
        long m = s / 60;   s %= 60;
        if (h > 0) return h + "ч " + m + "м " + s + "с";
        if (m > 0) return m + "м " + s + "с";
        return s + "с";
    }
}
