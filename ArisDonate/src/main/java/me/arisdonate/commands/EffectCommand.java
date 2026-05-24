package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectCommand extends BaseCommand {
    public EffectCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.effect")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length < 1) { p.sendMessage(Msg.parse("&7Использование: &e/effect <тип> [секунд] [уровень] | clear")); return; }
        if (args[0].equalsIgnoreCase("clear")) {
            for (PotionEffect e : p.getActivePotionEffects()) p.removePotionEffect(e.getType());
            p.sendMessage(Msg.parse("&aЭффекты сняты."));
            return;
        }
        PotionEffectType type = org.bukkit.Registry.EFFECT.get(org.bukkit.NamespacedKey.minecraft(args[0].toLowerCase()));
        if (type == null) { p.sendMessage(Msg.parse("&cНеизвестный эффект.")); return; }
        int dur = args.length > 1 ? safe(args[1], 30) : 30;
        int lvl = args.length > 2 ? safe(args[2], 1) - 1 : 0;
        p.addPotionEffect(new PotionEffect(type, dur * 20, lvl));
        p.sendMessage(Msg.parse("&aЭффект &e" + args[0] + " &aна " + dur + "с лвл " + (lvl + 1)));
    }

    private int safe(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
}
