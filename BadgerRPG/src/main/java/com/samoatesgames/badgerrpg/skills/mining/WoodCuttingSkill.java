package com.samoatesgames.badgerrpg.skills.mining;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.skills.BlockBasedSkill;
import org.bukkit.Material;

/**
 *
 * @author Sam
 */
public class WoodCuttingSkill extends BlockBasedSkill {
    
    /**
     * Class constructor
     * @param plugin
     */
    public WoodCuttingSkill(BadgerRPG plugin) {
        super(plugin, "WoodCutting", Material.DIAMOND_AXE);        
        m_blocks.put(Material.LOG, 4);
        m_blocks.put(Material.LOG_2, 4);
    }
    
}
