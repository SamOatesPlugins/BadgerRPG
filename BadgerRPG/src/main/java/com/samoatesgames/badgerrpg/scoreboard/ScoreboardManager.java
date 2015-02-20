/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.scoreboard;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.events.SkillLevelChangeEvent;
import java.security.MessageDigest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Sam
 */
public class ScoreboardManager implements Listener {

    /**
     * 
     */
    private final BadgerRPG m_plugin;
    
    /**
     * 
     */
    private final Scoreboard m_rpgScoreboard;
    
    /**
     * 
     * @param plugin
     */
    public ScoreboardManager(BadgerRPG plugin) {
        m_plugin = plugin;
        m_rpgScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {            
                    setupTeam(player);         
                }
            }            
        }, 20l);                        
    }
    
    /**
     * 
     * @param player 
     */
    private void setupTeam(Player player) {
        final String teamName = "" + player.getName().hashCode();            
        Team team = m_rpgScoreboard.getTeam(teamName);
        if (team == null) {
            team = m_rpgScoreboard.registerNewTeam(teamName);
            team.setAllowFriendlyFire(true);
            team.setCanSeeFriendlyInvisibles(false);
        }
        
        int level = m_plugin.getPlayerLevel(player);
        team.setSuffix(" [Lvl " + level + "]");
        
        Team currentTeam = m_rpgScoreboard.getPlayerTeam(player);
        if (currentTeam != team) {
            if (currentTeam != null) {
                currentTeam.removePlayer(player);
            }
            team.addPlayer(player);
        }
        
        player.setScoreboard(m_rpgScoreboard);
    }
    
    /**
     * 
     * @param event 
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLevelChange(SkillLevelChangeEvent event) {
         final Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin, new Runnable() {
            public void run() {
                setupTeam(player);  
            }            
        }, 20l);           
    }
    
    /**
     * Called when a player joins
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin, new Runnable() {
            public void run() {
                setupTeam(player);  
            }            
        }, 20l);        
    }

    /**
     * Called when a player leaves
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLeave(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        final String teamName = "" + player.getName().hashCode();            
        Team team = m_rpgScoreboard.getTeam(teamName);
        if (team != null) {
            team.removePlayer(player);
        }
        
    }

}
