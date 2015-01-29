package com.samoatesgames.badgerrpg;

import com.samoatesgames.badgerrpg.commands.SkillCommand;
import com.samoatesgames.badgerrpg.listeners.PlayerListener;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.badgerrpg.skills.mining.MiningSkill;
import com.samoatesgames.badgerrpg.skills.mining.WoodCuttingSkill;
import com.samoatesgames.samoatesplugincore.plugin.SamOatesPlugin;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * The main plugin class
 *
 * @author Sam Oates <sam@samoatesgames.com>
 */
public final class BadgerRPG extends SamOatesPlugin {

    /**
     * All registered skills
     */
    private final TreeMap<String, RPGSkill> m_skills = new TreeMap<String, RPGSkill>();

    /**
     * Class constructor
     */
    public BadgerRPG() {
        super("BadgerRPG", "RPG", ChatColor.DARK_RED);
    }

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {

        // Setup all skills
        setupSkill(MiningSkill.class);
        setupSkill(WoodCuttingSkill.class);

        // Setup other listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);

        // Register commands
        m_commandManager.registerCommandHandler("skills", new SkillCommand(this));
        
        this.logInfo("Succesfully enabled.");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        this.logInfo("Succesfully disabled.");
    }

    /**
     * Register all configuration settings
     */
    public void setupConfigurationSettings() {}

    /**
     *
     * @param skillClass
     */
    private void setupSkill(Class skillClass) {

        try {
            // Create an instance of the skill and see if it is enabled
            RPGSkill skill = (RPGSkill)skillClass.getDeclaredConstructor(BadgerRPG.class).newInstance(this);
            if (skill.initialize()) {
                // Resgister the listener of the skill
                PluginManager pluginManager = this.getServer().getPluginManager();
                pluginManager.registerEvents(skill, this);

                // Store the skill in our skill map
                m_skills.put(skill.getName(), skill);
                this.logInfo("Successfully setup the '" + skill.getName() + "' skill.");
            }
        } catch (Exception ex) {
            this.logException("Failed to setup skill '" + skillClass.getName() + "'.", ex);
        }
    }

    /**
     * 
     * @return 
     */
    public TreeMap<String, RPGSkill> getSkills() {
        return m_skills;
    }
    
    /**
     * 
     * @param player 
     */
    public void addPlayerData(final Player player) {
        for (RPGSkill skill : m_skills.values()) {
            skill.addPlayerData(player);
        }        
    }
    
    /**
     * 
     * @param player 
     */
    public void removePlayerData(final Player player) {
        for (RPGSkill skill : m_skills.values()) {
            skill.removePlayerData(player);
        }        
    }
}
