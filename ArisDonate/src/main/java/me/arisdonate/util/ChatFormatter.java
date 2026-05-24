package me.arisdonate.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.models.StaffRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Префикс перед ником в чате и в табе.
 *
 * Приоритет: стаф-ранг → донат → «Игрок».
 * В таб используется scoreboard team prefix, как vanilla-способ показать цветной префикс.
 */
public class ChatFormatter {

    private final ArisDonatePlugin plugin;

    public ChatFormatter(ArisDonatePlugin plugin) {
        this.plugin = plugin;
    }

    public Component buildPrefix(Player p) {
        StaffRank staff = plugin.getStaffManager() == null
                ? null
                : plugin.getStaffManager().getPlayerRank(p.getName());
        if (staff != null) return Msg.parse(staff.gradientName());

        DonateRank rank = plugin.getDonateManager().getPlayerRank(p.getName());
        if (rank != null) return Msg.parse(rank.gradientName());

        return Component.text("Игрок", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    /** Обновляет tab-prefix через scoreboard team (sortable по весу). */
    public void applyTabPrefix(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        StaffRank staff = plugin.getStaffManager() == null
                ? null
                : plugin.getStaffManager().getPlayerRank(p.getName());
        DonateRank rank = plugin.getDonateManager().getPlayerRank(p.getName());

        // Сортировка в табе = алфавит по имени команды.
        // Нужный порядок: стаф «выше» доната, донат выше обычных игроков.
        //   aa_… — стаф (сверху),
        //   am_… — донат (середина),
        //   zz_player — «Игрок» (снизу).
        String teamId;
        Component prefix;
        if (staff != null) {
            int sortWeight = 999 - staff.weight();
            teamId = "aa_" + pad4(Math.max(0, sortWeight)) + "_" + staff.id();
            prefix = Msg.parse(staff.gradientName() + " ");
        } else if (rank != null) {
            int sortWeight = 9999 - rank.weight();
            teamId = "am_" + pad4(Math.max(0, sortWeight)) + "_" + rank.id();
            prefix = Msg.parse(rank.gradientName() + " ");
        } else {
            teamId = "zz_player";
            prefix = Component.text("Игрок ", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);
        }
        if (teamId.length() > 16) teamId = teamId.substring(0, 16);

        // Снять с любых наших команд (иначе останется старый префикс после reload/обновления).
        for (Team t : sb.getTeams()) {
            String n = t.getName();
            if (n.startsWith("aa_") || n.startsWith("am_") || n.startsWith("zz_")
             || n.startsWith("ad_") || n.startsWith("as_") || n.startsWith("az_")
             || n.startsWith("Zzz_default")) {
                t.removeEntry(p.getName());
            }
        }

        Team team = sb.getTeam(teamId);
        if (team == null) team = sb.registerNewTeam(teamId);
        team.prefix(prefix);
        team.addEntry(p.getName());

        p.setPlayerListName(null);
    }

    private static String pad4(int n) { return String.format("%04d", n); }
}
