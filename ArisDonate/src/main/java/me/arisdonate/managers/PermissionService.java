package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.models.StaffRank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Раздаёт игрокам реальные Bukkit-пермы по их донат-рангу и стаф-рангу.
 *
 * Базовая таблица перм заранее зашита в {@link #DEFAULT_PERMS}. Любую запись
 * можно переопределить через config.yml:
 *
 *   ranks:
 *     stellar:
 *       permissions:
 *         - arisdonate.fly
 *         - arisdonate.kit.stellar
 *
 * Если в ранге задан {@code kit:}, право {@code arisdonate.kit.<kitId>}
 * добавляется автоматически. Перм-сет накапливается: владелец высокого ранга
 * получает все права своего ранга и всех более низких (по {@code weight}).
 *
 * Стаф-ранги дают модераторские пермы (ban, mute, kick, check и т.д.)
 * в зависимости от веса ранга.
 */
public class PermissionService {

    private static final Map<String, List<String>> DEFAULT_PERMS = new LinkedHashMap<>();
    static {
        DEFAULT_PERMS.put("spark",    List.of());
        DEFAULT_PERMS.put("luna",     List.of("arisdonate.hat", "arisdonate.skull"));
        DEFAULT_PERMS.put("stellar",  List.of("arisdonate.fly", "arisdonate.feed", "arisdonate.heal"));
        DEFAULT_PERMS.put("nova",     List.of("arisdonate.speed"));
        DEFAULT_PERMS.put("comet",    List.of(
                "arisdonate.jump", "arisdonate.top", "arisdonate.back",
                "arisdonate.tphere"));
        DEFAULT_PERMS.put("galaxy",   List.of("arisdonate.repair"));
        DEFAULT_PERMS.put("nebula",   List.of("arisdonate.nick"));
        DEFAULT_PERMS.put("cosmos",   List.of("arisdonate.vanish", "arisdonate.god", "arisdonate.cure"));
        DEFAULT_PERMS.put("phoenix",  List.of("arisdonate.smite", "arisdonate.burn"));
        DEFAULT_PERMS.put("aris",     List.of(
                "arisdonate.enderchest", "arisdonate.invsee",
                "arisdonate.tppos", "arisdonate.tpall",
                "arisdonate.setwarp", "arisdonate.delwarp"));
        DEFAULT_PERMS.put("arisplus", List.of(
                "arisdonate.gamemode", "arisdonate.give", "arisdonate.item",
                "arisdonate.more", "arisdonate.effect",
                "arisdonate.commandspy", "arisdonate.socialspy",
                "arisdonate.weather", "arisdonate.time",
                "arisdonate.broadcast", "arisdonate.clearinv",
                "arisdonate.enderchest.others"));
    }

    /**
     * Стаф-пермы по минимальному весу ранга.
     * Helper (100+) — check, clearchat
     * D.Helper (110+) — + mute, kick
     * ML.Moder (120+) — + ban, freeze, warn
     * Moder (130+) — + jail, invsee
     * D.Moder (140+) — + vanish, tp
     * Admin (170+) — + все остальные
     */
    private static final int WEIGHT_HELPER    = 100;
    private static final int WEIGHT_DHELPER   = 110;
    private static final int WEIGHT_MLMODER   = 120;
    private static final int WEIGHT_MODER     = 130;
    private static final int WEIGHT_DMODER    = 140;
    private static final int WEIGHT_ADMIN     = 170;

    private final ArisDonatePlugin plugin;
    private final Map<UUID, PermissionAttachment> attachments = new java.util.HashMap<>();

    public PermissionService(ArisDonatePlugin plugin) {
        this.plugin = plugin;
    }

    /** Полный (кумулятивный) набор перм для донат-ранга, с учётом всех более низких. */
    public List<String> permissionsFor(DonateRank rank) {
        if (rank == null) return List.of();
        Set<String> all = new LinkedHashSet<>();
        for (DonateRank r : plugin.getDonateManager().getRanks().values()) {
            if (r.weight() > rank.weight()) continue;
            all.addAll(permsForRankOnly(r));
        }
        return new ArrayList<>(all);
    }

    /** Набор перм для стаф-ранга, кумулятивно по весу. */
    public List<String> staffPermissionsFor(StaffRank rank) {
        if (rank == null) return List.of();
        Set<String> perms = new LinkedHashSet<>();
        int w = rank.weight();

        if (w >= WEIGHT_HELPER) {
            perms.add("arisdonate.check");
            perms.add("arisdonate.clearchat");
        }
        if (w >= WEIGHT_DHELPER) {
            perms.add("arisdonate.mute");
            perms.add("arisdonate.kick");
        }
        if (w >= WEIGHT_MLMODER) {
            perms.add("arisdonate.ban");
            perms.add("arisdonate.freeze");
            perms.add("arisdonate.warn");
        }
        if (w >= WEIGHT_MODER) {
            perms.add("arisdonate.jail");
            perms.add("arisdonate.setjail");
            perms.add("arisdonate.invsee");
            perms.add("arisdonate.vanish");
            perms.add("arisdonate.vanish.see");
        }
        if (w >= WEIGHT_DMODER) {
            perms.add("arisdonate.tp");
            perms.add("arisdonate.tphere");
            perms.add("arisdonate.tpall");
            perms.add("arisdonate.back");
        }
        if (w >= WEIGHT_ADMIN) {
            perms.add("arisdonate.broadcast");
            perms.add("arisdonate.socialspy");
            perms.add("arisdonate.commandspy");
            perms.add("arisdonate.clearinv");
            perms.add("arisdonate.gamemode");
            perms.add("arisdonate.fly");
            perms.add("arisdonate.speed");
            perms.add("arisdonate.heal");
            perms.add("arisdonate.feed");
            perms.add("arisdonate.god");
            perms.add("arisdonate.cure");
            perms.add("arisdonate.effect");
            perms.add("arisdonate.give");
            perms.add("arisdonate.item");
            perms.add("arisdonate.more");
            perms.add("arisdonate.top");
            perms.add("arisdonate.jump");
            perms.add("arisdonate.repair");
            perms.add("arisdonate.time");
            perms.add("arisdonate.weather");
            perms.add("arisdonate.worldtp");
            perms.add("arisdonate.nick");
            perms.add("arisdonate.hat");
            perms.add("arisdonate.smite");
            perms.add("arisdonate.burn");
            perms.add("arisdonate.sudo");
            perms.add("arisdonate.enderchest");
            perms.add("arisdonate.enderchest.others");
            perms.add("arisdonate.setwarp");
            perms.add("arisdonate.delwarp");
            perms.add("arisdonate.setspawn");
            perms.add("arisdonate.tppos");
        }

        return new ArrayList<>(perms);
    }

    private List<String> permsForRankOnly(DonateRank r) {
        Set<String> set = new LinkedHashSet<>();
        // 1) Override из config.yml: ranks.<id>.permissions: [ ... ]
        ConfigurationSection sect = plugin.getConfig().getConfigurationSection("ranks." + r.id());
        if (sect != null && sect.isList("permissions")) {
            for (String p : sect.getStringList("permissions")) {
                if (p != null && !p.isBlank()) set.add(p.trim());
            }
        } else {
            // 2) Базовая таблица
            set.addAll(DEFAULT_PERMS.getOrDefault(r.id(), List.of()));
        }
        // 3) Кит из поля kit: ранга
        if (r.kitId() != null && !r.kitId().isBlank()) {
            set.add("arisdonate.kit." + r.kitId().toLowerCase());
        }
        return new ArrayList<>(set);
    }

    /** Перевыдать пермы конкретному онлайн-игроку (после join / give / set / remove). */
    public void apply(Player p) {
        if (p == null || !p.isOnline()) return;
        PermissionAttachment att = attachments.get(p.getUniqueId());
        if (att == null) {
            att = p.addAttachment(plugin);
            attachments.put(p.getUniqueId(), att);
        } else {
            for (String old : new ArrayList<>(att.getPermissions().keySet())) {
                att.unsetPermission(old);
            }
        }
        // Донат-пермы
        DonateRank rank = plugin.getDonateManager().getPlayerRank(p.getName());
        for (String perm : permissionsFor(rank)) {
            att.setPermission(perm, true);
        }
        // Стаф-пермы
        StaffRank staffRank = plugin.getStaffManager().getPlayerRank(p.getName());
        for (String perm : staffPermissionsFor(staffRank)) {
            att.setPermission(perm, true);
        }
        p.recalculatePermissions();
    }

    /** Сбросить аттач при выходе. */
    public void remove(Player p) {
        if (p == null) return;
        PermissionAttachment att = attachments.remove(p.getUniqueId());
        if (att != null) {
            try { p.removeAttachment(att); } catch (IllegalArgumentException ignored) {}
        }
    }

    /** Перепринять для всех онлайн-игроков (после reload или старта плагина). */
    public void refreshAll() {
        for (Player p : plugin.getServer().getOnlinePlayers()) apply(p);
    }
}
