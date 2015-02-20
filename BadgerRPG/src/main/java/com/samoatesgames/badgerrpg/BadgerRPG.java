package com.samoatesgames.badgerrpg;

import com.samoatesgames.badgerrpg.commands.SkillCommand;
import com.samoatesgames.badgerrpg.data.PlayerSkillTableData;
import com.samoatesgames.badgerrpg.listeners.PlayerListener;
import com.samoatesgames.badgerrpg.scoreboard.ScoreboardManager;
import com.samoatesgames.badgerrpg.skills.PlayerSkillData;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.badgerrpg.skills.blocks.ExcavationSkill;
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
        setupSkill(ExcavationSkill.class);

        // Setup other listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ScoreboardManager(this), this);

        // Register commands
        m_commandManager.registerCommandHandler("skills", new SkillCommand(this));

        // Get helper plugins
        if (this.getSetting(Setting.useLogblockLookup, true)) {
            Plugin logblockPlugin = pluginManager.getPlugin("LogBlock");
            if (logblockPlugin != null) {
                m_logblock = (LogBlock) logblockPlugin;
            }
        }

        // Setup the database
        m_database = bDatabaseManager.createDatabase(this.getSetting(Setting.databaseName, "my_database"), this, bDatabaseManager.DatabaseType.SQL);
        if (m_database.login(
                this.getSetting(Setting.databaseHost, "localhost"),
                this.getSetting(Setting.databaseUsername, "user"),
                this.getSetting(Setting.databasePassword, "password"),
                this.getSetting(Setting.databasePort, 3306))) {
            
            this.logInfo("Connected to database. Loading player skills");            

            if (!m_database.tableExists("BadgerRPG_SkillData")) {
                m_database.query("CREATE TABLE `BadgerRPG_SkillData` (player TEXT, skillName TEXT, xp int, abilityReset BIGINT)", true);
            }
            
            // For all online players, load in their skill data
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
        
        for (Player player : this.getServer().getOnlinePlayers()) {
            this.removePlayerData(player);
        }
        
        m_logblock = null;
        m_skills.clear();        
        m_database.freeDatabase();
        
        this.logInfo("Succesfully disabled.");
    }

    /**
     * 
     * @param player
     * @return 
     */
    public int getPlayerLevel(Player player) {
        
        int noofSkills = 0;
        int averageLevel = 0;
        for (RPGSkill skill : m_skills.values()) {
            PlayerSkillData data = skill.getSkillData(player);
            if (data != null) {
                averageLevel += data.getLevel();
            }
            noofSkills++;
        }
        
        if (noofSkills == 0) {
            return 1;
        }
        
        return (int)(averageLevel / noofSkills);
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

        this.registerSetting(Setting.databaseHost, "localhost");
        this.registerSetting(Setting.databasePort, 3306);
        this.registerSetting(Setting.databaseName, "my_database");
        this.registerSetting(Setting.databaseUsername, "user");
        this.registerSetting(Setting.databasePassword, "password");

        this.registerSetting(Setting.useLogblockLookup, true);
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
            skill.addPlayerData(player, m_database);

            ResultSet result = m_database.queryResult("SELECT * FROM `BadgerRPG_SkillData` WHERE `player` = '" + player.getUniqueId().toString() + "' AND `skillName` = '" + skill.getName() + "'");
            if (result != null) {
                try {
                    if (result.next()) {
                        PlayerSkillData data = skill.getSkillData(player);
                        data.setXP(result.getInt("xp"));
                        data.setTimeout(result.getLong("abilityReset"));
                    } else {
                        m_database.query("INSERT INTO `BadgerRPG_SkillData`(`player`, `skillName`, `xp`, `abilityReset`) VALUES ('" + 
                            player.getUniqueId().toString() + "','" + skill.getName() + "',0,0)", 
                            true);
                    }
                } catch (Exception ex) {
                    this.logException("Failed to load skill data for '" + player.getUniqueId().toString() + "'", ex);
                }
            }
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
