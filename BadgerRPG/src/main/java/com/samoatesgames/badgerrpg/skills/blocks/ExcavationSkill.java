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
public class ExcavationSkill extends BlockBasedSkill {
    
    /**
     * Class constructor
     * @param plugin The owner plugin
     */
    public ExcavationSkill(BadgerRPG plugin) {
        super(plugin, "Excavation", Material.DIAMOND_SPADE);
        
        // Register default logs (this can be changed via config)
        m_blocks.put(Material.GRASS, new BlockData(1));
        m_blocks.put(Material.DIRT, new BlockData(1));
        m_blocks.put(Material.GRAVEL, new BlockData(1));
        m_blocks.put(Material.SAND, new BlockData(1));
        m_blocks.put(Material.SOUL_SAND, new BlockData(1));
        
        // Register default tools (this can be changed via config)
        m_tools.add(Material.WOOD_SPADE);
        m_tools.add(Material.STONE_SPADE);
        m_tools.add(Material.IRON_SPADE);
        m_tools.add(Material.GOLD_SPADE);
        m_tools.add(Material.DIAMOND_SPADE);
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
