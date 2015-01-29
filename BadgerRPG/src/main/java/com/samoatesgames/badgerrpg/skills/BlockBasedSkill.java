/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.skills;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.samoatesplugincore.configuration.PluginConfiguration;
import java.io.File;
import java.util.EnumMap;
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
     * 
     */
    protected final Map<Material, Integer> m_blocks = new EnumMap<Material, Integer>(Material.class);
    
    /**
     * 
     * @param plugin
     * @param skillName
     * @param icon 
     */
    public BlockBasedSkill(BadgerRPG plugin, String skillName, Material icon) {
        super(plugin, skillName, icon);
    }    
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean initialize() {
                
        String pluginKey = this.getName().toLowerCase();
        File skillConfigFile = new File(m_plugin.getDataFolder(), "skills" + File.separator + pluginKey + ".yml");
        
        PluginConfiguration configuration = new PluginConfiguration(m_plugin);
        
        configuration.registerSetting("enabled", true);
        for (Entry<Material, Integer> blockData : m_blocks.entrySet()) {
            configuration.registerSetting("blocks." + blockData.getKey().name() + ".xp", blockData.getValue());
        }
        
        configuration.loadPluginConfiguration(skillConfigFile);
        
        m_blocks.clear();
        for (Material material : Material.values()) {
            int xp = configuration.getSetting("blocks." + material.name() + ".xp", 0, true);
            if (xp != 0) {
                m_blocks.put(material, xp);
            }
        }        
                
        return configuration.getSetting("enabled", true);
    }
    
    /**
     * Called when a player breaks a block
     * @param event 
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        
        final Player player = event.getPlayer();
        final PlayerSkillData skillData = this.getSkillData(player);
        if (skillData == null) {
            m_plugin.logError("Failed to find '" + player.getName() + "s' player skill data.");
            return;
        }
        
        final Material blockMaterial = event.getBlock().getType();
        if (!m_blocks.containsKey(blockMaterial)) {
            return;
        }
        
        int xp = m_blocks.get(blockMaterial);
        skillData.addXP(xp);        
    }
}
