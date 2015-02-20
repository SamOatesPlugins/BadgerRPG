package com.samoatesgames.badgerrpg.events;

import com.samoatesgames.badgerrpg.skills.RPGSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Sam
 */
public class SkillLevelChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player m_player;
    private final RPGSkill m_skill;
    private final int m_oldLevel;
    private final int m_newLevel;
    
    public SkillLevelChangeEvent(Player player, RPGSkill skill, int oldLevel, int newLevel) {
        m_player = player;
        m_skill = skill;
        m_oldLevel = oldLevel;
        m_newLevel = newLevel;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
        return m_player;
    }
    
    public RPGSkill getSkill() {
        return m_skill;
    }
 
    public int getOldLevel() {
        return m_oldLevel;
    }
    
    public int getNewLevel() {
        return m_newLevel;
    }
}
