package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.ChatManager;
import me.arischat.managers.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MsgCommand implements CommandExecutor, TabCompleter {

    private final ArisChatPlugin plugin;

    public MsgCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }
        if (args.length < 2) { p.sendMessage(Msg.parse("&7/msg <ник> <сообщение>")); return true; }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { p.sendMessage(Msg.parse("&cИгрок &e" + args[0] + " &cне в сети.")); return true; }
        if (target.equals(p)) { p.sendMessage(Msg.parse("&cНельзя писать себе.")); return true; }

        ChatManager cm = plugin.getChatManager();
        if (cm.isIgnoring(target, p.getUniqueId())) { p.sendMessage(Msg.parse("&cЭтот игрок вас игнорирует.")); return true; }

        String msg = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        String fmtSender = plugin.getConfig().getString("msg-format-sender", "&7[&dЯ → &f{target}&7] &f{message}")
                .replace("{target}", target.getName()).replace("{message}", msg).replace("{sender}", p.getName());
        String fmtReceiver = plugin.getConfig().getString("msg-format-receiver", "&7[&d{sender} → Я&7] &f{message}")
                .replace("{sender}", p.getName()).replace("{message}", msg).replace("{target}", target.getName());

        p.sendMessage(Msg.parse(fmtSender));
        target.sendMessage(Msg.parse(fmtReceiver));

        cm.setReplyTarget(p.getUniqueId(), target.getUniqueId());
        cm.setReplyTarget(target.getUniqueId(), p.getUniqueId());

        // Spy
        String spyFmt = plugin.getConfig().getString("spy-format", "&8[SPY] {sender} → {target}: {message}")
                .replace("{sender}", p.getName()).replace("{target}", target.getName()).replace("{message}", msg);
        Component spyMsg = Msg.parse(spyFmt);
        for (UUID spy : cm.getSpies()) {
            if (spy.equals(p.getUniqueId()) || spy.equals(target.getUniqueId())) continue;
            Player sp = Bukkit.getPlayer(spy);
            if (sp != null && sp.isOnline()) sp.sendMessage(spyMsg);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
}
