package me.arispets.commands;

import me.arispets.ArisPetsPlugin;
import me.arispets.managers.Msg;
import me.arispets.models.PetData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PetAdminCommand implements CommandExecutor, TabCompleter {

    private final ArisPetsPlugin plugin;

    public PetAdminCommand(ArisPetsPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("arispets.admin")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }

        if (args.length < 1) {
            sender.sendMessage(Msg.parse("&6[ArisPets Admin]"));
            sender.sendMessage(Msg.parse("  &e/petadmin give <ник> <питомец> &7— выдать питомца"));
            sender.sendMessage(Msg.parse("  &e/petadmin remove <ник> <питомец> &7— забрать питомца"));
            sender.sendMessage(Msg.parse("  &e/petadmin reload &7— перечитать конфиг"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (args.length < 3) { sender.sendMessage(Msg.parse("&7/petadmin give <ник> <питомец>")); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return true; }
                PetData pd = plugin.getPetManager().getPetType(args[2]);
                if (pd == null) { sender.sendMessage(Msg.parse("&cПитомец не найден.")); return true; }
                plugin.getPetManager().givePet(target.getUniqueId(), pd.id());
                sender.sendMessage(Msg.parse("&aПитомец " + pd.displayName() + " &aвыдан &e" + target.getName()));
                target.sendMessage(Msg.parse("&aВам выдан питомец: " + pd.displayName() + "&a! /pet spawn " + pd.id()));
            }
            case "remove" -> {
                if (args.length < 3) { sender.sendMessage(Msg.parse("&7/petadmin remove <ник> <питомец>")); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return true; }
                plugin.getPetManager().removePetOwnership(target.getUniqueId(), args[2]);
                plugin.getPetManager().despawn(target);
                sender.sendMessage(Msg.parse("&aПитомец забран у &e" + target.getName()));
            }
            case "reload" -> {
                plugin.reloadConfig();
                plugin.getPetManager().reload();
                sender.sendMessage(Msg.parse("&aКонфиг перечитан."));
            }
            default -> sender.sendMessage(Msg.parse("&cНеизвестная подкоманда."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("give", "remove", "reload");
        if (args.length == 2 && ("give".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0]))) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        if (args.length == 3) {
            List<String> ids = new ArrayList<>();
            for (PetData pd : plugin.getPetManager().allPetTypes()) ids.add(pd.id());
            return ids;
        }
        return List.of();
    }
}
