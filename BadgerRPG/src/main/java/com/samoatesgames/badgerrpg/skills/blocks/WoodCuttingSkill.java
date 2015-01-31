package com.samoatesgames.badgerrpg.skills.blocks;

import com.samoatesgames.badgerrpg.BadgerRPG;
import org.bukkit.Material;

/**
 *
 * @author Sam
 */
public class WoodCuttingSkill extends BlockBasedSkill {
    
    /**
     * Class constructor
     * @param plugin The owner plugin
     */
    public WoodCuttingSkill(BadgerRPG plugin) {
        super(plugin, "WoodCutting", Material.DIAMOND_AXE);
        
        // Register default logs (this can be changed via config)
        m_blocks.put(Material.LOG, new BlockData(4, 0));           // Oak
        m_blocks.put(Material.LOG, new BlockData(4, 1));           // Spruce
        m_blocks.put(Material.LOG, new BlockData(4, 2));           // Birch
        m_blocks.put(Material.LOG, new BlockData(4, 3));           // Jungle
        m_blocks.put(Material.LOG_2, new BlockData(4, 0));         // Acacia
        m_blocks.put(Material.LOG_2, new BlockData(4, 1));         // Dark Oak
        
        // Register default tools (this can be changed via config)
        m_tools.add(Material.WOOD_AXE);
        m_tools.add(Material.STONE_AXE);
        m_tools.add(Material.IRON_AXE);
        m_tools.add(Material.GOLD_AXE);
        m_tools.add(Material.DIAMOND_AXE);
    }
    
}
