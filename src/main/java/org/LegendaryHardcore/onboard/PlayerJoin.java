package org.LegendaryHardcore.onboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.group.Group;
import java.util.concurrent.CompletableFuture;
import java.util.Collection;

public class PlayerJoin implements Listener {
    private final Onboard plugin;

    public PlayerJoin(Onboard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String preGroup = plugin.getConfigData().getPreOnboardGroup();

        plugin.canAcceptRules.put(player.getUniqueId(), false);

        // Check if player is in PRE_ONBOARD_GROUP. If they are, start the rules sequence for them.
        CompletableFuture<User> userFuture = plugin.getLuckPermsAPI().getUserManager().loadUser(player.getUniqueId());
        userFuture.thenAcceptAsync(user -> {

            Collection<Group> groups = user.getInheritedGroups(user.getQueryOptions());
            if (groups.stream().anyMatch(g -> g.getName().equals(preGroup))) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getRulesSequence().start(player);
                });
            }
        });
    }
}