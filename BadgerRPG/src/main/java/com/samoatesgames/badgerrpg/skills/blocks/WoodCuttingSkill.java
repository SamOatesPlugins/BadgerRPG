package com.samoatesgames.badgerrpg.skills.blocks;

import com.samoatesgames.badgerrpg.BadgerRPG;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

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
        m_blocks.put(Material.LOG, new BlockData(4));
        m_blocks.put(Material.LOG_2, new BlockData(4));
        
        // Register default tools (this can be changed via config)
        m_tools.add(Material.WOOD_AXE);
        m_tools.add(Material.STONE_AXE);
        m_tools.add(Material.IRON_AXE);
        m_tools.add(Material.GOLD_AXE);
        m_tools.add(Material.DIAMOND_AXE);
    }
    
    /**
     * 
     * @param event
     * @return 
     */
    public boolean isBlockBreakValid(BlockBreakEvent event) {
        
        final Player player = event.getPlayer();
        final ItemStack tool = player.getItemInHand();
        if (tool == null) {
            return false;
        }
        
        return !tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH);
    }    
}
