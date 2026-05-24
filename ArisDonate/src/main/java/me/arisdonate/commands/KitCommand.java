package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.KitManager;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import me.arisdonate.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitCommand extends BaseCommand {
    public KitCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) {
            plugin.getKitsGui().open(p);
            return;
        }

        // /kit give <player> <kit> — выдать кит другому игроку
        if (args[0].equalsIgnoreCase("give") && args.length >= 3) {
            handleGive(p, args);
            return;
        }

        KitManager.Kit kit = plugin.getKitManager().getKit(args[0]);
        if (kit == null) { p.sendMessage(Msg.parse("&cКит не найден. Открой &e/kits &cдля списка.")); return; }
        if (!plugin.getKitManager().canTakeKit(p, kit)) {
            p.sendMessage(Msg.parse("&cНет доступа к этому киту. Купите донат: &e/donate"));
            return;
        }
        long left = plugin.getKitManager().cooldownLeft(p, kit.id);
        if (left > 0) {
            p.sendMessage(Msg.parse("&cКит на кулдауне: ещё &e" + TimeUtil.fmt(left)));
            return;
        }
        plugin.getKitManager().giveKit(p, kit);
        p.sendMessage(Msg.parse("&aПолучен кит " + kit.displayName));
    }

    private void handleGive(Player sender, String[] args) {
        String targetName = args[1];
        String kitId = args[2];

        KitManager.Kit kit = plugin.getKitManager().getKit(kitId);
        if (kit == null) {
            sender.sendMessage(Msg.parse("&cКит &e" + kitId + " &cне найден."));
            return;
        }

        if (!plugin.getKitManager().canTakeKit(sender, kit)) {
            sender.sendMessage(Msg.parse("&cУ вас нет доступа к этому киту."));
            return;
        }

        long left = plugin.getKitManager().cooldownLeft(sender, kit.id);
        if (left > 0) {
            sender.sendMessage(Msg.parse("&cКит на кулдауне: ещё &e" + TimeUtil.fmt(left)));
            return;
        }

        Player target = Players.online(targetName);
        if (target == null) {
            sender.sendMessage(Msg.parse("&cИгрок &e" + targetName + " &cне в сети."));
            return;
        }

        plugin.getKitManager().giveKit(target, kit);
        // Кулдаун ставится отправителю, а не получателю
        plugin.getKitManager().setCooldown(sender, kit.id);

        sender.sendMessage(Msg.parse("&aВы выдали кит " + kit.displayName + " &aигроку &e" + target.getName()));
        target.sendMessage(Msg.parse("&aВам выдан кит " + kit.displayName + " &aот &e" + sender.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> ids = new ArrayList<>();
            ids.add("give");
            for (KitManager.Kit k : plugin.getKitManager().all()) ids.add(k.id);
            return ids;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Players.onlineNames(args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> ids = new ArrayList<>();
            for (KitManager.Kit k : plugin.getKitManager().all()) ids.add(k.id);
            return ids;
        }
        return List.of();
    }
}
