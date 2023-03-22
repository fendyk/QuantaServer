package com.fendyk.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Log {

    static final String prefix = "[QuantumCity] ";
    static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public static void error(String str) {
        final TextComponent textComponent = Component.text()
                .color(TextColor.color(239,68,68))
                .append(Component.text().content(prefix).decoration(TextDecoration.BOLD, true).build())
                .append(Component.text("\uD83C\uDF39: " + str))
                .build();
        sender.sendMessage(textComponent);
    }

    public static void warning(String str) {
        final TextComponent textComponent = Component.text()
                .color(TextColor.color(234,179,8))
                .append(Component.text().content(prefix).decoration(TextDecoration.BOLD, true).build())
                .append(Component.text("\uD83C\uDF44: " + str))
                .build();
        sender.sendMessage(textComponent);
    }

    public static void info(String str) {
        final TextComponent textComponent = Component.text()
                .color(TextColor.color(59,130,246))
                .append(Component.text().content(prefix).decoration(TextDecoration.BOLD, true).build())
                .append(Component.text("\uD83C\uDF4F: " + str))
                .build();
        sender.sendMessage(textComponent);
    }

    public static void success(String str) {
        final TextComponent textComponent = Component.text()
                .color(TextColor.color(34,197,94))
                .append(Component.text().content(prefix).decoration(TextDecoration.BOLD, true).build())
                .append(Component.text("\uD83D\uDD25: " + str))
                .build();
        sender.sendMessage(textComponent);
    }

}
