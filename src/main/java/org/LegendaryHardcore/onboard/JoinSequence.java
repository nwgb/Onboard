package org.LegendaryHardcore.onboard;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.LegendaryHardcore.onboard.ConfigData.TitleContent;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

/*
 *  Class for the join sequence after accepting rules.
 *  Displays join messages and assigns player to post-onboard group
 */
public class JoinSequence {
    private final Onboard plugin;

    // For converting seconds to ticks (1 second = 20 ticks)
    private static final int TPS = 20;

    public JoinSequence(Onboard plugin) {
        this.plugin = plugin;
    }

    public void start(Player player) {
        // Reset so player can no longer run /accept
        plugin.canAcceptRules.put(player.getUniqueId(), false);

        // Give player blindness
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1, false, false));
        }, 1L);

        /* Join Sequence */
        List<TitleContent> contents = plugin.getConfigData().getJoinSequenceContent();
        final int duration = plugin.getConfigData().getJoinSequenceDuration() * TPS;
        final int fadeIn = plugin.getConfigData().getJoinSequenceFadeIn() * TPS;
        final int fadeOut = plugin.getConfigData().getJoinSequenceFadeOut() * TPS;

        int delay = duration + fadeIn + fadeOut;
        int current_delay = 0;

        // For each join sequence message, display title and subtitle
        for (int i = 0; i < contents.size(); i++) {
            final TitleContent content = contents.get(i);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String title = content.title();
                String subtitle = content.subtitle();
                player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
            }, current_delay);
            current_delay += delay;
        }

        // Reset player effects, gamemode, etc. and random teleport
        final int randomSpawnX1 = plugin.getConfigData().getRandomSpawnX1();
        final int randomSpawnZ1 = plugin.getConfigData().getRandomSpawnZ1();
        final int randomSpawnX2 = plugin.getConfigData().getRandomSpawnX2();
        final int randomSpawnZ2 = plugin.getConfigData().getRandomSpawnZ2();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Clear effects and set gamemdode back to survival
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.setGameMode(GameMode.SURVIVAL);
            player.setGravity(true);
            player.setInvisible(false);
            player.setInvulnerable(false);

            // Teleport to random location
            String mainWorldName = plugin.getConfigData().getOnboardLocation().worldName();
            World mainWorld = Bukkit.getWorld(mainWorldName);
            int x = ThreadLocalRandom.current().nextInt(randomSpawnX1, randomSpawnX2);
            int z = ThreadLocalRandom.current().nextInt(randomSpawnZ1, randomSpawnZ2);
            int y = mainWorld.getHighestBlockYAt(x, z);

            Location randomLoc = new Location(mainWorld, x + 0.5, y + 1, z + 0.5);
            player.teleport(randomLoc);

            // Move player to post-onboard group
            updateLuckPermsGroup(player);
            plugin.canAcceptRules.put(player.getUniqueId(), false);
            }, current_delay);

    }
    /*
     * Asynchronously move player to post-onboard group
     */
    private void updateLuckPermsGroup(Player player) {
        String preOnboardGroup = plugin.getConfigData().getPreOnboardGroup();
        String postOnboardGroup = plugin.getConfigData().getPostOnboardGroup();

        LuckPerms lp = plugin.getLuckPermsAPI();

        lp.getUserManager().loadUser(player.getUniqueId()).thenAcceptAsync(user -> {

            // Remove from pre-onboard group
            user.data().clear(NodeType.INHERITANCE.predicate(node ->
                    node.getGroupName().equalsIgnoreCase(preOnboardGroup)
            ));

            // Add to post-onboard group
            user.data().add(InheritanceNode.builder(postOnboardGroup).build());

            lp.getUserManager().saveUser(user).join();

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.recalculatePermissions();
            });
        });
    }
}