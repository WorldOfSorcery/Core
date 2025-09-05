package me.hektortm.wosCore.discord;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class DiscordLog {

    private final Level level;
    private final JavaPlugin plugin;
    private final String uuid;
    private final String message;

    public DiscordLog(Level level, JavaPlugin plugin, String uuid, String message) {
        this.level = level;
        this.plugin = plugin;
        this.uuid = uuid;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }
    public String getUuid() {
        return uuid;
    }
    public String getMessage() {
        return message;
    }
}
