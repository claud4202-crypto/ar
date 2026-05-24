package me.arisdonate.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилиты для форматирования сообщений: градиенты, цвета, HEX.
 * Используем нативный Adventure API (Paper встроен) без сторонних зависимостей.
 */
public final class Msg {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");
    private static final Pattern GRAD_PATTERN = Pattern.compile("<grad:#([0-9A-Fa-f]{6}):#([0-9A-Fa-f]{6})>(.*?)</grad>");

    private Msg() {}

    /**
     * Парсит цвет/градиент/&-коды и HEX (&#RRGGBB).
     * Поддерживаемые конструкции:
     *   &a &c &l &r ... стандартные цветовые коды
     *   &#RRGGBB       произвольный HEX-цвет
     *   <grad:#FFFF00:#FFA500>text</grad>  градиент
     */
    public static Component parse(String input) {
        if (input == null) return Component.empty();
        String s = input;

        Component result = Component.empty();
        Matcher gm = GRAD_PATTERN.matcher(s);
        int last = 0;
        while (gm.find()) {
            if (gm.start() > last) {
                result = result.append(parseColorCodes(s.substring(last, gm.start())));
            }
            int c1 = Integer.parseInt(gm.group(1), 16);
            int c2 = Integer.parseInt(gm.group(2), 16);
            result = result.append(gradient(gm.group(3), c1, c2));
            last = gm.end();
        }
        if (last < s.length()) {
            result = result.append(parseColorCodes(s.substring(last)));
        }
        return result.decoration(TextDecoration.ITALIC, false);
    }

    /** Применяет линейный RGB-градиент к строке. */
    public static Component gradient(String text, int startRgb, int endRgb) {
        if (text == null || text.isEmpty()) return Component.empty();
        Component out = Component.empty();
        int len = text.length();
        int sr = (startRgb >> 16) & 0xFF, sg = (startRgb >> 8) & 0xFF, sb = startRgb & 0xFF;
        int er = (endRgb   >> 16) & 0xFF, eg = (endRgb   >> 8) & 0xFF, eb = endRgb   & 0xFF;
        for (int i = 0; i < len; i++) {
            double t = len == 1 ? 0.0 : (double) i / (len - 1);
            int r = (int) Math.round(sr + (er - sr) * t);
            int g = (int) Math.round(sg + (eg - sg) * t);
            int b = (int) Math.round(sb + (eb - sb) * t);
            out = out.append(Component.text(String.valueOf(text.charAt(i)))
                    .color(TextColor.color(r, g, b)));
        }
        return out.decoration(TextDecoration.ITALIC, false);
    }

    private static Component parseColorCodes(String s) {
        // Сначала HEX
        Matcher hm = HEX_PATTERN.matcher(s);
        StringBuilder out = new StringBuilder();
        int last = 0;
        List<Object[]> hexSegments = new ArrayList<>();
        while (hm.find()) {
            if (hm.start() > last) hexSegments.add(new Object[]{s.substring(last, hm.start()), null});
            hexSegments.add(new Object[]{null, hm.group(1)});
            last = hm.end();
        }
        if (last < s.length()) hexSegments.add(new Object[]{s.substring(last), null});

        Component result = Component.empty();
        TextColor current = null;
        boolean bold = false, italic = false, underline = false, strike = false, magic = false;
        for (Object[] seg : hexSegments) {
            if (seg[1] != null) {
                current = TextColor.color(Integer.parseInt((String) seg[1], 16));
                continue;
            }
            String part = (String) seg[0];
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if ((c == '&' || c == '\u00A7') && i + 1 < part.length()) {
                    char next = Character.toLowerCase(part.charAt(i + 1));
                    TextColor mapped = mapColor(next);
                    if (mapped != null) {
                        current = mapped;
                        bold = italic = underline = strike = magic = false;
                        i++;
                        continue;
                    }
                    switch (next) {
                        case 'l': bold = true; i++; continue;
                        case 'o': italic = true; i++; continue;
                        case 'n': underline = true; i++; continue;
                        case 'm': strike = true; i++; continue;
                        case 'k': magic = true; i++; continue;
                        case 'r':
                            current = null; bold = italic = underline = strike = magic = false;
                            i++; continue;
                        default:
                    }
                }
                TextComponent comp = Component.text(String.valueOf(c));
                if (current != null) comp = comp.color(current);
                if (bold)      comp = comp.decorate(TextDecoration.BOLD);
                if (italic)    comp = comp.decorate(TextDecoration.ITALIC);
                if (underline) comp = comp.decorate(TextDecoration.UNDERLINED);
                if (strike)    comp = comp.decorate(TextDecoration.STRIKETHROUGH);
                if (magic)     comp = comp.decorate(TextDecoration.OBFUSCATED);
                result = result.append(comp);
            }
        }
        return result.decoration(TextDecoration.ITALIC, false);
    }

    private static TextColor mapColor(char c) {
        switch (c) {
            case '0': return NamedTextColor.BLACK;
            case '1': return NamedTextColor.DARK_BLUE;
            case '2': return NamedTextColor.DARK_GREEN;
            case '3': return NamedTextColor.DARK_AQUA;
            case '4': return NamedTextColor.DARK_RED;
            case '5': return NamedTextColor.DARK_PURPLE;
            case '6': return NamedTextColor.GOLD;
            case '7': return NamedTextColor.GRAY;
            case '8': return NamedTextColor.DARK_GRAY;
            case '9': return NamedTextColor.BLUE;
            case 'a': return NamedTextColor.GREEN;
            case 'b': return NamedTextColor.AQUA;
            case 'c': return NamedTextColor.RED;
            case 'd': return NamedTextColor.LIGHT_PURPLE;
            case 'e': return NamedTextColor.YELLOW;
            case 'f': return NamedTextColor.WHITE;
            default: return null;
        }
    }

    public static String stripFormatting(Component component) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
}
