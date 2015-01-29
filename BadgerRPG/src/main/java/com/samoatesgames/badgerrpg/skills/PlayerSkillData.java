package com.samoatesgames.badgerrpg.skills;

import org.bukkit.entity.Player;

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
    private int m_xp = 0;
    
    /**
     * 
     * @param player 
     */
    public PlayerSkillData(Player player) {
        m_player = player;
    }
    
    /**
     * 
     * @return 
     */
    public int getXP() {
        return m_xp;
    }
    
    /**
     * 
     * @param xp 
     */
    public void addXP(final int xp) {
        m_xp += xp;
    }
    
    /**
     * 
     * @return 
     */
    public int getLevel() {
       return (int)Math.floor(Math.pow(m_xp, 0.325));
    }
}
