/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.data;

/**
 *
 * @author Sam
 */
public class PlayerSkillTableData {
    
    public final String player;
    public final String skillName;
    public int xp = 0;
    
    public PlayerSkillTableData(String playerUUID, String skillName, int xp) {
        this.player = playerUUID;
        this.skillName = skillName;
        this.xp = xp;        
    }
    
}
