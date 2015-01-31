/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.skills.blocks;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.skills.PlayerSkillData;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.samoatesplugincore.configuration.PluginConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Sam
 */
public class BlockBasedSkill extends RPGSkill {

    /**
     * All blocks registered with this skill
     */
    protected final Map<Material, BlockData> m_blocks = new EnumMap<Material, BlockData>(Material.class);
    
    /**
     * All tools which can be used by this skill
     */
    protected final List<Material> m_tools = new ArrayList<Material>();
    
    /**
     * Class constructor
     * @param plugin    Owner plugin
     * @param skillName The name of the skill
     * @param icon      The icon to show in the skill GUI
     */
    public BlockBasedSkill(BadgerRPG plugin, String skillName, Material icon) {
        super(plugin, skillName, icon);
    }    
    
    /**
     * Register and load all settings for the skill
     * @return True if the skill should be enabled
     */
    @Override
    public boolean initialize() {
                
        String pluginKey = this.getName().toLowerCase();
        File skillConfigFile = new File(m_plugin.getDataFolder(), "skills" + File.separator + pluginKey + ".yml");
        
        PluginConfiguration configuration = new PluginConfiguration(m_plugin);
        
        configuration.registerSetting("enabled", true);
        for (Entry<Material, BlockData> blockData : m_blocks.entrySet()) {
            BlockData data = blockData.getValue();
            configuration.registerSetting("blocks." + blockData.getKey().name() + ".xp", data.getXp());            
            if (data.getDataID() != 0) {
                configuration.registerSetting("blocks." + blockData.getKey().name() + ".data_id", data.getDataID());
            }
        }
        
        for (Material tool : m_tools) {
            configuration.registerSetting("tools." + tool.name() + ".enabled", true);
        }
        
        configuration.loadPluginConfiguration(skillConfigFile);
        
        m_blocks.clear();
        m_tools.clear();
        for (Material material : Material.values()) {
            
            // Try load the block configuration
            int xp = configuration.getSetting("blocks." + material.name() + ".xp", 0, true);
            int dataId = configuration.getSetting("blocks." + material.name() + ".data_id", 0, true);
            if (xp != 0) {
                m_blocks.put(material, new BlockData(xp, dataId));
            }
            
            // Try load the tool configuration
            if (configuration.getSetting("tools." + material.name() + ".enabled", false, true)) {
                m_tools.add(material);
            }
        }        
                
        return configuration.getSetting("enabled", true);
    }
    
    /**
     * Called when a player breaks a block
     * @param event The block break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        
        final Player player = event.getPlayer();
        final PlayerSkillData skillData = this.getSkillData(player);
        if (skillData == null) {
            m_plugin.logError("Failed to find '" + player.getName() + "s' player skill data.");
            return;
        }
        
        // See if we care about the block in this skill
        final Material blockMaterial = event.getBlock().getType();
        if (!m_blocks.containsKey(blockMaterial)) {
            return;
        }
        
        BlockData blockData = m_blocks.get(blockMaterial);
        if (blockData.getDataID() != event.getBlock().getData()) {
            return;
        }
        
        // See if the tool being used is valid for this skill
        final Material tool = player.getItemInHand().getType();
        if (!m_tools.contains(tool)) {
            return;
        }
        
        // Add the xp to the skill
        skillData.addXP(blockData.getXp());
    }
}
