package com.samoatesgames.badgerrpg.skills.mining;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.skills.BlockBasedSkill;
import org.bukkit.Material;

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
        m_blocks.put(Material.STONE, 1);
    }
}
