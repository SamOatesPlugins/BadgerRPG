package com.samoatesgames.badgerrpg.commands;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.skills.PlayerSkillData;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.samoatesplugincore.commands.BasicCommandHandler;
import com.samoatesgames.samoatesplugincore.commands.PluginCommandManager;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Sam
 */
public class SkillCommand extends BasicCommandHandler {

    /**
     * 
     */
    private final BadgerRPG m_plugin;
    
    /**
     * 
     * @param plugin 
     */
    public SkillCommand(BadgerRPG plugin) {
        super("rpg.command.skill");
        m_plugin = plugin;
    }

    /**
     * 
     * @param manager
     * @param sender
     * @param arguments
     * @return 
     */
    @Override
    public boolean execute(PluginCommandManager manager, CommandSender sender, String[] arguments) {
        
        if (!(sender instanceof Player)) {
            manager.sendMessage(sender, "Only players can execute the skill command.");
            return true;
        }
        
        final Player player = (Player) sender;
        
        if (arguments.length == 0) {
            showSkillsGUI(manager, player);
            return true;
        }
        
        return true;
    }
    
    /**
     * 
     * @param manager
     * @param player 
     */
    private void showSkillsGUI(PluginCommandManager manager, Player player) {
        
        final TreeMap<String, RPGSkill> skills = m_plugin.getSkills();
        final int noofKits = skills.size();
        final int rowCount = (int) Math.ceil(noofKits / 9.0f);

        GuiInventory inventory = new GuiInventory(m_plugin);
        inventory.createInventory("RPG Skills", rowCount);

        for (RPGSkill skill : skills.values()) {

            if (!manager.hasPermission(player, skill.getPermission())) {
                continue;
            }

            ItemStack item = new ItemStack(skill.getIcon());
            String[] details = new String[3];
            
            PlayerSkillData skillData = skill.getSkillData(player);
            if (skillData == null) {                
                continue;
            }
            
            details[0] = ChatColor.GREEN + "Level " + skillData.getLevel();
            details[1] = ChatColor.GOLD + "Experience " + skillData.getXP();

            inventory.addMenuItem(skill.getName(), item, details, null);
        }

        inventory.open(player);        
    }
    
}
