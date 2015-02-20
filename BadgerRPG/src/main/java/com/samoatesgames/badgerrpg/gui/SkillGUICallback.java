/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.gui;

import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.samoatesplugincore.gui.GuiCallback;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Sam
 */
public class SkillGUICallback implements GuiCallback {

    private final Player m_player;
    private final RPGSkill m_skill;
    
    public SkillGUICallback(Player player, RPGSkill skill) {
        m_player = player;
        m_skill = skill;
    }
    
    public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {
        
        // Skill callback        
        GuiInventory infoInvent = new GuiInventory(inventory.getPlugin());
        infoInvent.createInventory(m_skill.getName() + " Information", 3);
        
        m_skill.createSkillGUI(infoInvent, inventory, m_player);
        
        inventory.close(m_player);
        m_player.closeInventory();
        infoInvent.open(m_player);
        
    }
    
}
