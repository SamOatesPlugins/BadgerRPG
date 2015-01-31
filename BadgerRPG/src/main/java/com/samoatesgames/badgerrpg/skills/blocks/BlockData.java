/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samoatesgames.badgerrpg.skills.blocks;

/**
 *
 * @author Sam
 */
public class BlockData {
 
    private final int m_xp;
    
    /**
     * 
     * @param xp 
     */
    public BlockData(int xp) {
        m_xp = xp;
    }
       
    /**
     * 
     * @return 
     */
    public int getXp() {
        return m_xp;
    }
}
