package me.phantom.bananimations.commands;

import me.phantom.bananimations.BanAnimations;
import me.phantom.bananimations.api.Animation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BATabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("test");
            suggestions.add("ban");
            suggestions.add("ipban");
            suggestions.add("tempban");
            suggestions.add("mute");
            suggestions.add("tempmute");
            suggestions.add("kick");
            suggestions.add("help");
            suggestions.add("list");
            return suggestions;
        }
        if(args.length == 2) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
            return suggestions;
        }
        if(args.length == 3) {
            for(Animation a : BanAnimations.animations.values()) {
                suggestions.add(a.getName());
            }
            return suggestions;
        }
        return null;
    }
}
