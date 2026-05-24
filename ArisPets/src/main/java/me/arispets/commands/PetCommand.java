package me.arispets.commands;

import me.arispets.ArisPetsPlugin;
import me.arispets.managers.Msg;
import me.arispets.models.PetData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PetCommand implements CommandExecutor, TabCompleter {

    private final ArisPetsPlugin plugin;

    public PetCommand(ArisPetsPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }
        if (!p.hasPermission("arispets.use")) { p.sendMessage(Msg.parse("&cНет доступа.")); return true; }

        if (args.length == 0) {
            p.sendMessage(Msg.parse("&6[ArisPets] &7Использование:"));
            p.sendMessage(Msg.parse("  &e/pet list &7— список питомцев"));
            p.sendMessage(Msg.parse("  &e/pet spawn <питомец> &7— призвать питомца"));
            p.sendMessage(Msg.parse("  &e/pet remove &7— убрать питомца"));
            p.sendMessage(Msg.parse("  &e/pet rename <имя> &7— переименовать питомца"));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list" -> {
                p.sendMessage(Msg.parse("&6[ArisPets] &7Доступные питомцы:"));
                Set<String> owned = plugin.getPetManager().getOwnedPets(p.getUniqueId());
                for (PetData pd : plugin.getPetManager().allPetTypes()) {
                    boolean has = owned.contains(pd.id()) || p.hasPermission("arispets.admin");
                    boolean canUse = pd.permission().isEmpty() || p.hasPermission(pd.permission());
                    String status;
                    if (has && canUse) status = "&a[Доступен]";
                    else if (has) status = "&e[Нет доступа]";
                    else status = "&c[Не куплен]";
                    p.sendMessage(Msg.parse("  " + pd.displayName() + " &7(" + pd.id() + ") " + status));
                }
            }
            case "spawn", "select" -> {
                if (args.length < 2) { p.sendMessage(Msg.parse("&7/pet spawn <питомец>")); return true; }
                String id = args[1].toLowerCase();
                PetData pd = plugin.getPetManager().getPetType(id);
                if (pd == null) { p.sendMessage(Msg.parse("&cПитомец не найден: &e" + id)); return true; }
                if (!plugin.getPetManager().hasPet(p.getUniqueId(), id) && !p.hasPermission("arispets.admin")) {
                    p.sendMessage(Msg.parse("&cУ вас нет этого питомца.")); return true;
                }
                if (!pd.permission().isEmpty() && !p.hasPermission(pd.permission())) {
                    p.sendMessage(Msg.parse("&cНет разрешения на этого питомца.")); return true;
                }
                plugin.getPetManager().spawnPet(p, pd);
                p.sendMessage(Msg.parse("&aПитомец " + pd.displayName() + " &aпризван!"));
            }
            case "remove", "despawn", "hide" -> {
                plugin.getPetManager().despawn(p);
                p.sendMessage(Msg.parse("&aПитомец убран."));
            }
            case "rename", "name" -> {
                if (args.length < 2) { p.sendMessage(Msg.parse("&7/pet rename <имя>")); return true; }
                String name = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                if (name.length() > 32) { p.sendMessage(Msg.parse("&cМаксимум 32 символа.")); return true; }
                plugin.getPetManager().setCustomPetName(p.getUniqueId(), name);
                p.sendMessage(Msg.parse("&aПитомец переименован в: " + name));
            }
            default -> p.sendMessage(Msg.parse("&cНеизвестная подкоманда: &e" + sub));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("list", "spawn", "remove", "rename");
        if (args.length == 2 && ("spawn".equalsIgnoreCase(args[0]) || "select".equalsIgnoreCase(args[0]))) {
            List<String> ids = new ArrayList<>();
            for (PetData pd : plugin.getPetManager().allPetTypes()) ids.add(pd.id());
            return ids;
        }
        return List.of();
    }
}
