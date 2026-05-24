package me.arisauction.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Msg {
    private Msg() {}

    public static Component parse(String input) {
        if (input == null) return Component.empty();
        Component result = Component.empty();
        NamedTextColor current = null;
        boolean bold = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '&' || c == '\u00A7') && i + 1 < input.length()) {
                char next = Character.toLowerCase(input.charAt(i + 1));
                NamedTextColor mapped = mapColor(next);
                if (mapped != null) { current = mapped; bold = false; i++; continue; }
                if (next == 'l') { bold = true; i++; continue; }
                if (next == 'r') { current = null; bold = false; i++; continue; }
            }
            Component ch = Component.text(String.valueOf(c));
            if (current != null) ch = ch.color(current);
            if (bold) ch = ch.decoration(TextDecoration.BOLD, true);
            result = result.append(ch);
        }
        return result.decoration(TextDecoration.ITALIC, false);
    }

    private static NamedTextColor mapColor(char c) {
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
