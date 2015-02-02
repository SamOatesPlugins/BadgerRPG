package com.samoatesgames.badgerrpg.skills;

import com.samoatesgames.badgerrpg.BadgerRPG;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTable;

/**
 *
 * @author Sam
 */
public abstract class RPGSkill implements Listener {
    
    /**
     * The name of the skill
     */
    private final String m_skillName;
    
    /**
     * 
     */
    private final Material m_skillIcon;
    
    /**
     * The main plugin
     */
    protected final BadgerRPG m_plugin;
    
    /**
     *
     */
    private final Map<UUID, PlayerSkillData> m_playerData = new HashMap<UUID, PlayerSkillData>();
    
    /**
     * 
     * @param plugin
     * @param skillName 
     * @param icon 
     */    
    public RPGSkill(BadgerRPG plugin, String skillName, Material icon) {
        m_skillName = skillName;
        m_skillIcon = icon;
        m_plugin = plugin;
    }
    
    /**
     * 
     * @return 
     */
    public abstract boolean initialize();
    
    /**
     * 
     * @return 
     */
    public String getPermission() {
        return "rpg.skill." + m_skillName;
    }
    
    /**
     * 
     * @return 
     */
    public Material getIcon() {
        return m_skillIcon;
    }
        
    /**
     * 
     * @return 
     */
    public String getName() {
        return m_skillName;
    }
    
    /**
     * Add new player data (removing old if already registered)
     *
     * @param player
     * @param skillTable
     */
    public void addPlayerData(final Player player, final DatabaseTable skillTable) {

        final UUID uuid = player.getUniqueId();
        if (m_playerData.containsKey(uuid)) {
            m_playerData.remove(uuid);
        }

        m_playerData.put(uuid, new PlayerSkillData(player, this.getName(), skillTable));
    }

    /**
     * Remove player data from our map
     * @param player 
     */
    public void removePlayerData(final Player player) {
        final UUID uuid = player.getUniqueId();
        if (m_playerData.containsKey(uuid)) {
            PlayerSkillData data = m_playerData.get(uuid);
            data.clearData();            
            m_playerData.remove(uuid);
        }
    }
    
    /**
     * Return a players skill data
     * @param player
     * @return 
     */
    public PlayerSkillData getSkillData(final Player player) {
        final UUID uuid = player.getUniqueId();
        if (m_playerData.containsKey(uuid)) {
            return m_playerData.get(uuid);
        }
        return null;
    }
    
}
