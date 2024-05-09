package me.loutres.grouppets.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Util {
    private static final String PREFIX = "[GroupPets] ";

    public static void sendSuccessMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.GREEN + message);
    }

    public static void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.RED + message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.RESET + message);
    }
}
