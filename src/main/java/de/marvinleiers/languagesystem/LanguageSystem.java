package de.marvinleiers.languagesystem;

import de.marvinleiers.languagesystem.language.SupportedLanguages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class LanguageSystem extends JavaPlugin implements Listener
{
    private static HashMap<Player, SupportedLanguages> languagesHashMap = new HashMap<>();
    private static HashMap<SupportedLanguages, Map<String, Object>> languageContent = new HashMap<>();

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        this.loadLanguageFiles();

        for (SupportedLanguages languages : SupportedLanguages.values())
            getLogger().info(getMessage(languages, "test-message"));
    }

    private void loadLanguageFiles()
    {
        for (SupportedLanguages languages : SupportedLanguages.values())
        {
            String fileName = languages.getLanguage();
            File file = new File(this.getDataFolder().getPath() + "/" + fileName + ".yml");

            saveResource(fileName + ".yml", false);

            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            languageContent.put(languages, configFile.getValues(true));
            getLogger().info("Loaded language \"" + fileName + "\"");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (languagesHashMap.containsKey(player))
        {
            send(player, "welcome");
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                SupportedLanguages language = SupportedLanguages.ENGLISH;

                for (SupportedLanguages lang : SupportedLanguages.values())
                {
                    if (player.getLocale().contains(lang.getLanguage()))
                    {
                        language = lang;
                        break;
                    }
                }

                languagesHashMap.put(player, language);
                send(player, "welcome");
            }
        }.runTaskLater(this, 5 * 20);
    }

    public static void send(Player player, String messageKey)
    {
        if (getMessage(player, messageKey) != null)
        {
            player.sendMessage(getMessage(player, messageKey));
        }
    }

    public static SupportedLanguages getPlayerLanguage(Player player)
    {
        return languagesHashMap.get(player);
    }

    public static String getMessage(Player player, String messageKey)
    {
        return getMessage(getPlayerLanguage(player), messageKey);
    }

    public static String getMessage(SupportedLanguages language, String messageKey)
    {
        return ChatColor.translateAlternateColorCodes('&', languageContent.get(language).get(messageKey).toString());
    }
}
