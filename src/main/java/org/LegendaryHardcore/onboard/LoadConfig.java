package org.LegendaryHardcore.onboard;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.LegendaryHardcore.onboard.ConfigData.PlayerLocation;
import org.LegendaryHardcore.onboard.ConfigData.TitleContent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.Map;

/*
 *  Load contents of config
 */
public class LoadConfig {
    private final Onboard plugin;

    public LoadConfig(Onboard plugin) {
        this.plugin = plugin;
    }

    /*
     *  Load contents of config.yml
     *  @return A ConfigData object, or null if the config file cannot be read
     */
    public ConfigData load() {
        FileConfiguration config = plugin.getConfig();

        try {
            // Server name
            String serverName = config.getString("SERVER_NAME","LEGENDARY HARDCORE");

            // LP pre-onboard and post-onboard groups
            String preOnboardGroup = config.getString("PRE_ONBOARD_GROUP", "DEFAULT");
            String postOnboardGroup = config.getString("POST_ONBOARD_GROUP", "player");

            String onboardGamemode = config.getString("ONBOARD_GAMEMODE", "SURVIVAL");

            // Location
            PlayerLocation onboardLocation = new PlayerLocation(
                    config.getString("POS_WORLD", "world"),
                    config.getDouble("POS_X", 0),
                    config.getDouble("POS_Y", 60),
                    config.getDouble("POS_Z", 0),
                    (float) config.getDouble("POS_YAW", 0),
                    (float) config.getDouble("POS_PITCH", 0)
            );

            // Rules sequence
            List<TitleContent> rulesContent = loadTitleContents(config.getList("RULES_SEQUENCE_CONTENT"));
            int rulesDuration = config.getInt("RULES_SEQUENCE_DURATION", 5);
            int rulesSequenceFadeIn = config.getInt("RULES_SEQUENCE_FADE_IN", 0);
            int rulesSequenceFadeOut = config.getInt("RULES_SEQUENCE_FADE_OUT", 0);

            // Prompt accept (title and chat message)
            List<TitleContent> promptAccept = loadTitleContents(config.getList("PROMPT_ACCEPT"));
            String promptChat = config.getString("PROMPT_CHAT");

            // Join sequence
            List<TitleContent> joinContent = loadTitleContents(config.getList("JOIN_SEQUENCE_CONTENT"));
            int joinDuration = config.getInt("JOIN_SEQUENCE_DURATION", 5);
            int joinSequenceFadeIn = config.getInt("JOIN_SEQUENCE_FADE_IN", 0);
            int joinSequenceFadeOut = config.getInt("JOIN_SEQUENCE_FADE_OUT", 0);

            // Random spawn
            int randomSpawnX1 = config.getInt("RANDOM_SPAWN_X1", 0);
            int randomSpawnZ1 = config.getInt("RANDOM_SPAWN_Z1", 0);
            int randomSpawnX2 = config.getInt("RANDOM_SPAWN_X2", 0);
            int randomSpawnZ2 = config.getInt("RANDOM_SPAWN_Z2", 0);
            return new ConfigData(
                    serverName,
                    preOnboardGroup,
                    postOnboardGroup,
                    onboardGamemode,
                    onboardLocation,
                    rulesContent,
                    rulesDuration,
                    rulesSequenceFadeIn,
                    rulesSequenceFadeOut,
                    promptAccept,
                    promptChat,
                    joinContent,
                    joinDuration,
                    joinSequenceFadeIn,
                    joinSequenceFadeOut,
                    randomSpawnX1,
                    randomSpawnZ1,
                    randomSpawnX2,
                    randomSpawnZ2
            );

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to load config file- ", e);
            return null;
        }
    }

    /*
     *  Load list of title and subtitles from config
     */
    private List<TitleContent> loadTitleContents(List<?> titleContents) {
        if (titleContents == null) {
            return new ArrayList<>();
        }

        List<TitleContent> contents = new ArrayList<>();
        for (Object item : titleContents) {
            if (item instanceof Map<?, ?> map) {
                Object titleObj = map.get("TITLE");
                Object subtitleObj = map.get("SUBTITLE");

                String title = (titleObj instanceof String) ? (String) titleObj : "";
                String subtitle = (subtitleObj instanceof String) ? (String) subtitleObj : "";

                contents.add(new TitleContent(title, subtitle));
            }
        }
        return contents;
    }
}


