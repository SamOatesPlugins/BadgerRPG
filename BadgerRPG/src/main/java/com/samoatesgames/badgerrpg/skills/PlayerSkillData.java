package com.samoatesgames.badgerrpg.skills;

import com.samoatesgames.badgerrpg.data.PlayerSkillTableData;
import com.samoatesgames.samoatesplugincore.titlemanager.TitleManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTable;

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
    private final String m_skillName;

    /**
     *
     */
    private final PlayerSkillTableData m_data;

    /**
     *
     */
    private final DatabaseTable m_skillTable;

    /**
     *
     * @param player
     * @param skillName
     * @param skillTable
     */
    public PlayerSkillData(Player player, String skillName, final DatabaseTable skillTable) {
        m_player = player;
        m_skillName = skillName;
        m_data = new PlayerSkillTableData(m_player.getUniqueId().toString(), skillName, 0, 0L);
        m_skillTable = skillTable;
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
            TitleManager.sendTitle(m_player, "Level Up!", ChatColor.AQUA, "You are level " + newLevel + " " + m_skillName, ChatColor.GOLD, 1, 3, 1);
            m_player.playSound(m_player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        }

        m_skillTable.update(m_data, "`player` = '" + m_data.player + "' AND `skillName` = '" + m_data.skillName + "'", false);
    }

    /**
     *
     */
    public void clearData() {
        m_skillTable.update(m_data, "`player` = '" + m_data.player + "' AND `skillName` = '" + m_data.skillName + "'", true);
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
        m_skillTable.update(m_data, "`player` = '" + m_data.player + "' AND `skillName` = '" + m_data.skillName + "'", true);
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
