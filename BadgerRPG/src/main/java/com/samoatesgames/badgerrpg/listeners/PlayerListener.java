package com.samoatesgames.badgerrpg.listeners;

import com.samoatesgames.badgerrpg.BadgerRPG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Sam
 */
public class PlayerListener implements Listener {

    /**
     *
     */
    private final BadgerRPG m_plugin;

    /**
     *
     * @param plugin
     */
    public PlayerListener(BadgerRPG plugin) {
        m_plugin = plugin;
    }

    /**
     * Called when a player joins the server
     * @param event 
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        m_plugin.addPlayerData(event.getPlayer());        
    }

    /**
     * Called when a player quites the server
     * @param event 
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        m_plugin.removePlayerData(event.getPlayer());        
    }
}
