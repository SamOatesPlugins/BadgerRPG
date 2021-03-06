package com.samoatesgames.badgerrpg.skills.blocks;

import com.samoatesgames.badgerrpg.BadgerRPG;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Sam
 */
public class MiningSkill extends BlockBasedSkill {

    /**
     * Class constructor
     * @param plugin
     */
    public MiningSkill(BadgerRPG plugin) {
        super(plugin, "Mining", Material.DIAMOND_PICKAXE);

        // Register default blocks (this can be changed via config)
        m_blocks.put(Material.NETHERRACK, new BlockData(1));
        m_blocks.put(Material.ICE, new BlockData(1));                    
        m_blocks.put(Material.PACKED_ICE, new BlockData(1));                    
        m_blocks.put(Material.SANDSTONE, new BlockData(1));
        m_blocks.put(Material.PRISMARINE, new BlockData(1));
        m_blocks.put(Material.HARD_CLAY, new BlockData(1));
        m_blocks.put(Material.STAINED_CLAY, new BlockData(1));
        m_blocks.put(Material.STONE, new BlockData(1));        
        m_blocks.put(Material.SMOOTH_BRICK, new BlockData(1));
        m_blocks.put(Material.ENDER_STONE, new BlockData(1));
        m_blocks.put(Material.BRICK, new BlockData(1));
        //m_blocks.put(Material.COBBLESTONE, new BlockData(1));
        m_blocks.put(Material.MOSSY_COBBLESTONE, new BlockData(1));
        m_blocks.put(Material.NETHER_BRICK, new BlockData(1));
        m_blocks.put(Material.COAL_ORE, new BlockData(2));
        m_blocks.put(Material.QUARTZ_ORE, new BlockData(2));
        m_blocks.put(Material.IRON_ORE, new BlockData(4));
        m_blocks.put(Material.GOLD_ORE, new BlockData(5));
        m_blocks.put(Material.LAPIS_ORE, new BlockData(5));
        //m_blocks.put(Material.OBSIDIAN, new BlockData(5));
        m_blocks.put(Material.REDSTONE_ORE, new BlockData(6));
        m_blocks.put(Material.DIAMOND_ORE, new BlockData(10));
        m_blocks.put(Material.EMERALD_ORE, new BlockData(10));      
        
        // Register default tools (this can be changed via config)
        m_tools.add(Material.WOOD_PICKAXE);
        m_tools.add(Material.STONE_PICKAXE);
        m_tools.add(Material.IRON_PICKAXE);
        m_tools.add(Material.GOLD_PICKAXE);
        m_tools.add(Material.DIAMOND_PICKAXE);
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
