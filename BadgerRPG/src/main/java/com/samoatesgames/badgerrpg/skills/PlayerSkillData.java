package com.samoatesgames.badgerrpg.skills;

import com.samoatesgames.badgerrpg.data.PlayerSkillTableData;
import com.samoatesgames.badgerrpg.events.SkillLevelChangeEvent;
import com.samoatesgames.samoatesplugincore.titlemanager.TitleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import uk.thecodingbadgers.bDatabaseManager.Database.BukkitDatabase;

/**
 *
 * @author Sam
 */
public class PlayerSkillData {

    /**
     *
     */
    private final Player m_player;

    /**
     *
     */
    private final RPGSkill m_skill;

    /**
     *
     */
    private final PlayerSkillTableData m_data;

    /**
     * 
     */
    private final BukkitDatabase m_database;
    
    /**
     *
     * @param player
     * @param skill
     * @param database
     */
    public PlayerSkillData(Player player, RPGSkill skill, final BukkitDatabase database) {
        m_player = player;
        m_skill = skill;
        m_data = new PlayerSkillTableData(m_player.getUniqueId().toString(), skill.getName(), 0, 0L);
        m_database = database;
    }

    /**
     *
     * @return
     */
    public int getXP() {
        return m_data.xp;
    }

    /**
     *
     * @param xp
     */
    public void setXP(final int xp) {
        m_data.xp = xp;
    }

    /**
     *
     * @param xp
     */
    public void addXP(final int xp) {
        final int oldLevel = getLevel();
        m_data.xp += xp;

        final int newLevel = getLevel();
        if (oldLevel != newLevel) {
            TitleManager.sendTitle(m_player, "Level Up!", ChatColor.AQUA, "You are level " + newLevel + " " + m_skill.getName(), ChatColor.GOLD, 1, 3, 1);
            m_player.playSound(m_player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            
            SkillLevelChangeEvent event = new SkillLevelChangeEvent(m_player, m_skill, oldLevel, newLevel);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }

        m_database.query("UPDATE `BadgerRPG_SkillData` SET `xp` = '" + m_data.xp + 
            "' WHERE `player` = '" + m_data.player + 
            "' AND `skillName` = '" + m_data.skillName + "'");
    }

    /**
     *
     */
    public void clearData() {
        m_database.query("UPDATE `BadgerRPG_SkillData` SET `xp` = '" + m_data.xp + 
            "' WHERE `player` = '" + m_data.player + 
            "' AND `skillName` = '" + m_data.skillName + "'", true);
    }

    /**
     *
     * @return
     */
    public int getLevel() {
        return (int) Math.floor(Math.pow((m_data.xp + 1), 0.325));
    }

    /**
     *
     * @return
     */
    public boolean canUseAbility() {
        return System.currentTimeMillis() >= m_data.abilityReset;
    }

    /**
     *
     * @param timeout
     */
    public void setTimeout(Long timeout) {
        m_data.abilityReset = timeout;

        m_database.query("UPDATE `BadgerRPG_SkillData` SET `abilityReset` = '" + m_data.abilityReset + 
            "' WHERE `player` = '" + m_data.player + 
            "' AND `skillName` = '" + m_data.skillName + "'");
    }

    /**
     *
     * @return
     */
    public String getTimeoutRemaining() {
        
        long timeLeft = m_data.abilityReset - System.currentTimeMillis();
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        final long oneHour = 1000 * 60 * 60;
        final long oneMinute = 1000 * 60;
        final long oneSecond = 1000;

        while (timeLeft > oneHour) {
            timeLeft -= oneHour;
            hours++;
        }

        while (timeLeft > oneMinute) {
            timeLeft -= oneMinute;
            minutes++;
        }
        
        while (timeLeft > oneSecond) {
            timeLeft -= oneSecond;
            seconds++;
        }

        String output = "";
        if (hours == 1) {
            output = hours + " hour";
        } else if (hours > 1) {
            output = hours + " hours";
        }

        if (minutes > 0 && hours > 0) {
            if (seconds > 0) {
                output += ", ";
            } else {
                output += " and ";
            }
        }

        if (minutes == 1) {
            output += minutes + " minute";
        } else if (minutes > 1) {
            output += minutes + " minutes";
        }
        
        if ((minutes > 0 || hours > 0) && seconds > 0) {
            output += " and ";
        }
        
        if (seconds == 1) {
            output += seconds + " second";
        } else if (seconds > 1) {
            output += seconds + " seconds";
        }
        
        return output;
    }
}
