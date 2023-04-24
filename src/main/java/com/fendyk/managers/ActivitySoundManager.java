package com.fendyk.managers;

import com.fendyk.Main;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActivitySoundManager {

    static Main main = Main.instance;

    public static void play(Player player) {
        UUID uuid = player.getUniqueId();
        Audience audience = main.adventure().player(uuid);
        Sound sound = Sound.sound(Key.key("entity.firework_rocket.twinkle"), Sound.Source.PLAYER, 1f, 1f);
        audience.playSound(sound, Sound.Emitter.self());
    }

}
