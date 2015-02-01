package com.samoatesgames.badgerrpg;

import com.samoatesgames.badgerrpg.commands.SkillCommand;
import com.samoatesgames.badgerrpg.data.PlayerSkillTableData;
import com.samoatesgames.badgerrpg.listeners.PlayerListener;
import com.samoatesgames.badgerrpg.skills.PlayerSkillData;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.badgerrpg.skills.blocks.MiningSkill;
import com.samoatesgames.badgerrpg.skills.blocks.WoodCuttingSkill;
import com.samoatesgames.samoatesplugincore.plugin.SamOatesPlugin;
import de.diddiz.LogBlock.LogBlock;
import java.sql.ResultSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import uk.thecodingbadgers.bDatabaseManager.Database.BukkitDatabase;
import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTable;
import uk.thecodingbadgers.bDatabaseManager.bDatabaseManager;

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
     * The log block plugin
     */
    private LogBlock m_logblock = null;

    /**
     * Main database connection
     */
    private BukkitDatabase m_database = null;

    /**
     *
     */
    private DatabaseTable m_skillTable = null;

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
        super.onEnable();

        // Setup all skills
        setupSkill(MiningSkill.class);
        setupSkill(WoodCuttingSkill.class);

        // Setup other listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);

        // Register commands
        m_commandManager.registerCommandHandler("skills", new SkillCommand(this));

        // Get helper plugins
        Plugin logblockPlugin = pluginManager.getPlugin("LogBlock");
        if (logblockPlugin != null) {
            m_logblock = (LogBlock) logblockPlugin;
        }

        // Setup the database
        m_database = bDatabaseManager.createDatabase(this.getSetting("database.database", "my_database"), this, bDatabaseManager.DatabaseType.SQL);
        if (m_database.login(
                this.getSetting("database.host", "localhost"),
                this.getSetting("database.username", "user"),
                this.getSetting("database.password", "password"),
                this.getSetting("database.port", 3306))) {
            
            this.logInfo("Connected to database. Loading player skills");            
            m_skillTable = m_database.createTable("BadgerRPG_SkillData", PlayerSkillTableData.class);
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.addPlayerData(player);
            }
        } else {
            this.logError("Failed to login to database.");
        }

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
     *
     * @return
     */
    public LogBlock getLogBlock() {
        return m_logblock;
    }

    /**
     * Register all configuration settings
     */
    public void setupConfigurationSettings() {

        this.registerSetting("database.host", "localhost");
        this.registerSetting("database.port", 3306);
        this.registerSetting("database.database", "my_database");
        this.registerSetting("database.username", "user");
        this.registerSetting("database.password", "password");

    }

    /**
     *
     * @param skillClass
     */
    private void setupSkill(Class skillClass) {

        try {
            // Create an instance of the skill and see if it is enabled
            RPGSkill skill = (RPGSkill) skillClass.getDeclaredConstructor(BadgerRPG.class).newInstance(this);
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
            skill.addPlayerData(player, m_skillTable);

            ResultSet result = m_skillTable.selectAll("`player` = '" + player.getUniqueId().toString() + "' AND `skillName` = '" + skill.getName() + "'");
            try {
                if (result.next()) {
                    PlayerSkillData data = skill.getSkillData(player);
                    data.setXP(result.getInt("xp"));
                }
            } catch (Exception ex) {
                this.logException("Failed to load skill data for '" + player.getUniqueId().toString() + "'", ex);
            }
            m_database.freeResult(result);
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
