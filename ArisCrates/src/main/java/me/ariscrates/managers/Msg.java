package me.ariscrates.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Msg {
    private static final Pattern GRAD_PATTERN = Pattern.compile("<grad:#([0-9A-Fa-f]{6}):#([0-9A-Fa-f]{6})>(.*?)</grad>");

    private Msg() {}

    public static Component parse(String input) {
        if (input == null) return Component.empty();
        Component result = Component.empty();
        Matcher gm = GRAD_PATTERN.matcher(input);
        int last = 0;
        while (gm.find()) {
            if (gm.start() > last) result = result.append(parseColorCodes(input.substring(last, gm.start())));
            int c1 = Integer.parseInt(gm.group(1), 16);
            int c2 = Integer.parseInt(gm.group(2), 16);
            result = result.append(gradient(gm.group(3), c1, c2));
            last = gm.end();
        }
        if (last < input.length()) result = result.append(parseColorCodes(input.substring(last)));
        return result.decoration(TextDecoration.ITALIC, false);
    }

    public static Component gradient(String text, int startRgb, int endRgb) {
        if (text == null || text.isEmpty()) return Component.empty();
        Component out = Component.empty();
        int len = text.length();
        int sr = (startRgb >> 16) & 0xFF, sg = (startRgb >> 8) & 0xFF, sb = startRgb & 0xFF;
        int er = (endRgb >> 16) & 0xFF, eg = (endRgb >> 8) & 0xFF, eb = endRgb & 0xFF;
        for (int i = 0; i < len; i++) {
            double t = len == 1 ? 0.0 : (double) i / (len - 1);
            int r = (int) Math.round(sr + (er - sr) * t);
            int g = (int) Math.round(sg + (eg - sg) * t);
            int b = (int) Math.round(sb + (eb - sb) * t);
            out = out.append(Component.text(String.valueOf(text.charAt(i))).color(TextColor.color(r, g, b)));
        }
        return out.decoration(TextDecoration.ITALIC, false);
    }

    private static Component parseColorCodes(String s) {
        Component result = Component.empty();
        TextColor current = null;
        boolean bold = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c == '&' || c == '\u00A7') && i + 1 < s.length()) {
                char next = Character.toLowerCase(s.charAt(i + 1));
                TextColor mapped = mapColor(next);
                if (mapped != null) { current = mapped; bold = false; i++; continue; }
                if (next == 'l') { bold = true; i++; continue; }
                if (next == 'r') { current = null; bold = false; i++; continue; }
            }
            Component ch = Component.text(String.valueOf(c));
            if (current != null) ch = ch.color(current);
            if (bold) ch = ch.decoration(TextDecoration.BOLD, true);
            result = result.append(ch);
        }
        return result;
    }

    private static TextColor mapColor(char c) {
        return switch (c) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> null;
        };
    }
}
