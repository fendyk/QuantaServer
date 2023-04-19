package com.fendyk.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PluginTextComponent {

    public static Component prefix() {
        return Component.empty()
                .append(Component.text("QC")
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" >> ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                );
    }

    public static Component info(String message) {
        return prefix()
                .append(Component.text("INFO")
                        .color(NamedTextColor.DARK_AQUA)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" > ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(message)
                        .color(NamedTextColor.DARK_AQUA)
                );
    }

    public static Component success(String message) {
        return prefix()
                .append(Component.text("SUCCESS")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" > ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(message)
                        .color(NamedTextColor.GREEN)
                );
    }

    public static Component warning(String message) {
        return prefix()
                .append(Component.text("WARNING")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" > ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(message)
                        .color(NamedTextColor.GOLD)
                );
    }

    public static Component error(String message) {
        return prefix()
                .append(Component.text("ERROR")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" > ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(message)
                        .color(NamedTextColor.RED)
                );
    }

    public static Component statistic(String key, String value) {
        return prefix()
                .append(Component.text(key)
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" > ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(value)
                        .color(NamedTextColor.WHITE)
                );
    }

    public static Component command(String name, String description) {
        return prefix()
                .append(Component.text(name)
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(" - ")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.text(description)
                        .decoration(TextDecoration.ITALIC, true)
                        .color(NamedTextColor.WHITE)
                );
    }



}
