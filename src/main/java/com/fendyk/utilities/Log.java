package com.fendyk.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Logger;

public class Log {

    static final String prefix = "[QuantumCity] ";
    static ConsoleCommandSender sender = Bukkit.getConsoleSender();
    static Logger logger = Bukkit.getLogger();

    public static void error(String str) {
        logger.severe(prefix + ": \uD83C\uDF39 " + str);
    }

    public static void warning(String str) {
        logger.warning(prefix + ": \uD83C\uDF44" + str);
    }

    public static void info(String str) {
        logger.info(prefix + ": \uD83C\uDF4F " + str);
    }

    public static void success(String str) {
        logger.info(prefix + ": \uD83D\uDD25 " + str);
    }

}
