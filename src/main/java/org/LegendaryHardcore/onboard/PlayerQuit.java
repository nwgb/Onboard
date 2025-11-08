package org.LegendaryHardcore.onboard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    private final Onboard plugin;

    public PlayerQuit(Onboard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.canAcceptRules.remove(event.getPlayer().getUniqueId());
    }
}