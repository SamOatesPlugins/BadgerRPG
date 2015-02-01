package com.samoatesgames.badgerrpg.skills;

import com.samoatesgames.badgerrpg.data.PlayerSkillTableData;
import com.samoatesgames.samoatesplugincore.titlemanager.TitleManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
        m_data = new PlayerSkillTableData(m_player.getUniqueId().toString(), skillName, 0);
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
    public void save() {
        m_skillTable.update(m_data, "`player` = '" + m_data.player + "' AND `skillName` = '" + m_data.skillName + "'", true);
    }
    
    /**
     * 
     * @return 
     */
    public int getLevel() {
       return (int)Math.floor(Math.pow((m_data.xp + 1), 0.325));
    }
}
