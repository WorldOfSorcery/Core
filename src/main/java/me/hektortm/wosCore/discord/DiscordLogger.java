package me.hektortm.wosCore.discord;

import me.hektortm.wosCore.WoSCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static me.hektortm.wosCore.WoSCore.jda;

public class DiscordLogger {
    private static final WoSCore plugin = WoSCore.getPlugin(WoSCore.class);
    private static final boolean DEV_ENV;
    static {
        String env = plugin.getConfig().getString("env");
        DEV_ENV = env != null && env.equalsIgnoreCase("dev");
    }
    private static final String ERROR_CHANNEL_ID = "1380839940123791460";
    private static final String WARNING_CHANNEL_ID = "1380840054640869508";
    private static final String INFO_CHANNEL_ID = "1413623246406029452";

    private static final String DEV_ERROR_CHANNEL_ID = "1413639964121366618";
    private static final String DEV_WARNING_CHANNEL_ID = "1413640000620331079";
    private static final String DEV_INFO_CHANNEL_ID = "1413638430029643866";


    public static void log(DiscordLog log) {
        String channelId;
        String title;
        int color;

        if (log.getLevel() == Level.SEVERE) {
            if (DEV_ENV) channelId = DEV_ERROR_CHANNEL_ID;
            else channelId = ERROR_CHANNEL_ID;
            title = "Error";
            color = 0xdb2525;
        } else if (log.getLevel() == Level.WARNING) {
            if (DEV_ENV) channelId = DEV_WARNING_CHANNEL_ID;
            else channelId = WARNING_CHANNEL_ID;
            title = "Warning";
            color = 0xe6e025;
        } else if (log.getLevel() == Level.INFO) {
            if (DEV_ENV) channelId = DEV_INFO_CHANNEL_ID;
            else channelId = INFO_CHANNEL_ID;
            title = "Info";
            color = 0x25db4f;
        } else {
            return;
        }

        JavaPlugin plugin = log.getPlugin();
        String pluginName = plugin.getName();
        String pluginVersion = "v"+plugin.getPluginMeta().getVersion();
        String message = log.getMessage();
        String uuid = log.getUuid();

        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel == null) {
                return;
            }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedTime = LocalDateTime.now().format(formatter);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Debug | "+title + (DEV_ENV ? " | Localhost" : ""));
            embed.setDescription(message);
            embed.addField("Plugin", pluginName, true);
            embed.addField("Version", pluginVersion, true);
            embed.addField("uuid", uuid, true);
            embed.setFooter("Dev Logging • " + formattedTime);
            embed.setColor(color);

            channel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while sending the message: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void sendToDiscord(String webhookUrl, String content) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = "{\"content\":\"" + content.replace("\"", "\\\"") + "\"}";
            byte[] postData = jsonPayload.getBytes(StandardCharsets.UTF_8);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                System.err.println("Failed to send log to Discord: HTTP " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Error sending log to Discord: " + e.getMessage());
        }
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}