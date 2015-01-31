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
    
    private final int m_dataID;
    
    /**
     * 
     * @param xp 
     */
    public BlockData(int xp) {
        this(xp, 0);
    }
    
    /**
     * 
     * @param xp 
     * @param dataID 
     */
    public BlockData(int xp, int dataID) {
        m_xp = xp;
        m_dataID = dataID;
    }
    
    /**
     * 
     * @return 
     */
    public int getXp() {
        return m_xp;
    }
    
    /**
     * 
     * @return 
     */
    public int getDataID() {
        return m_dataID;
    }
}
