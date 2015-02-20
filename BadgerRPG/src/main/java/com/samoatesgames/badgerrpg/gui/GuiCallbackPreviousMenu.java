/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.gui;

import com.samoatesgames.samoatesplugincore.gui.GuiCallback;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Sam
 */
public class GuiCallbackPreviousMenu implements GuiCallback {

    private final GuiInventory m_menu;
    private final Player m_player;
    
    public GuiCallbackPreviousMenu(Player player, GuiInventory menu) {
        m_player = player;
        m_menu = menu;
    }    
    
    public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {
        inventory.close(m_player);
        m_player.closeInventory();
        m_menu.open(m_player);        
    }
    
}
