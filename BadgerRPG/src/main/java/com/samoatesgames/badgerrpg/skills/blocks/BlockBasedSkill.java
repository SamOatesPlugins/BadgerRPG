package com.samoatesgames.badgerrpg.skills.blocks;

import com.samoatesgames.badgerrpg.BadgerRPG;
import com.samoatesgames.badgerrpg.gui.GuiCallbackPreviousMenu;
import com.samoatesgames.badgerrpg.skills.PlayerSkillData;
import com.samoatesgames.badgerrpg.skills.RPGSkill;
import com.samoatesgames.samoatesplugincore.configuration.PluginConfiguration;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import de.diddiz.LogBlock.BlockChange;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Sam
 */
public abstract class BlockBasedSkill extends RPGSkill {

    /**
     * All blocks registered with this skill
     */
    protected final Map<Material, BlockData> m_blocks = new EnumMap<Material, BlockData>(Material.class);

    /**
     * All tools which can be used by this skill
     */
    protected final List<Material> m_tools = new ArrayList<Material>();

    /**
     *
     */
    protected final List<Location> m_humanMadeBlocks = new ArrayList<Location>();

    /**
     * Class constructor
     *
     * @param plugin Owner plugin
     * @param skillName The name of the skill
     * @param icon The icon to show in the skill GUI
     */
    public BlockBasedSkill(BadgerRPG plugin, String skillName, Material icon) {
        super(plugin, skillName, icon);
    }

    /**
     * Register and load all settings for the skill
     *
     * @return True if the skill should be enabled
     */
    @Override
    public boolean initialize() {

        String pluginKey = this.getName().toLowerCase();
        File skillConfigFile = new File(m_plugin.getDataFolder(), "skills" + File.separator + pluginKey + ".yml");

        PluginConfiguration configuration = new PluginConfiguration(m_plugin);

        configuration.registerSetting("enabled", true);
        for (Entry<Material, BlockData> blockData : m_blocks.entrySet()) {
            BlockData data = blockData.getValue();
            configuration.registerSetting("blocks." + blockData.getKey().name() + ".xp", data.getXp());
        }

        for (Material tool : m_tools) {
            configuration.registerSetting("tools." + tool.name() + ".enabled", true);
        }

        configuration.loadPluginConfiguration(skillConfigFile);

        m_blocks.clear();
        m_tools.clear();
        for (Material material : Material.values()) {

            // Try load the block configuration
            int xp = configuration.getSetting("blocks." + material.name() + ".xp", 0, true);
            if (xp != 0) {
                m_blocks.put(material, new BlockData(xp));
            }

            // Try load the tool configuration
            if (configuration.getSetting("tools." + material.name() + ".enabled", false, true)) {
                m_tools.add(material);
            }
        }

        return configuration.getSetting("enabled", true);
    }

    /**
     * Called when a player breaks a block
     *
     * @param event The block break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        final Block block = event.getBlock();
        final Player player = event.getPlayer();

        // Do they have perms for this skill?
        if (!m_plugin.hasPermission(player, this.getPermission())) {
            return;
        }

        final PlayerSkillData skillData = this.getSkillData(player);
        if (skillData == null) {
            m_plugin.logError("Failed to find '" + player.getName() + "s' player skill data.");
            return;
        }

        // See if the tool being used is valid for this skill
        final Material tool = player.getItemInHand().getType();
        if (!m_tools.contains(tool)) {
            return;
        }

        // See if we care about the block in this skill
        final Material blockMaterial = block.getType();
        if (!m_blocks.containsKey(blockMaterial)) {
            return;
        }
        
        if (!isBlockBreakValid(event)) {
            return;
        }

        final BlockData blockData = m_blocks.get(blockMaterial);
        final int xp = blockData.getXp();

        final LogBlock logBlock = m_plugin.getLogBlock();
        if (logBlock == null) {
            // Not using logblock, just give the xp
            skillData.addXP(xp);
        } else {
            // If log block is setup, then protect against xp grinding            
            final QueryParams logBlockQuery = new QueryParams(logBlock);
            logBlockQuery.bct = BlockChangeType.BOTH;
            logBlockQuery.loc = block.getLocation();
            logBlockQuery.world = block.getWorld();
            logBlockQuery.radius = 0;
            logBlockQuery.limit = 1;

            try {
                List<BlockChange> changes = logBlock.getBlockChanges(logBlockQuery);
                if (changes.isEmpty()) {
                    // No block changes found at the blocks location
                    // Add the xp to the skill                            
                    skillData.addXP(xp);
                }   
            } catch (Exception ex) {}
        }
    }

    /**
     * 
     * @param event
     * @return 
     */
    public abstract boolean isBlockBreakValid(BlockBreakEvent event);
    
    /**
     * Called when a player interacts
     *
     * @param event The block break event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        // Do they have perms for this skill?
        if (!m_plugin.hasPermission(player, this.getPermission())) {
            return;
        }

        // See if the tool being used is valid for this skill
        final ItemStack tool = player.getItemInHand();
        if (!m_tools.contains(tool.getType())) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // See if we care about the block in this skill
        final Block block = event.getClickedBlock();
        final Material blockMaterial = block.getType();
        if (!m_blocks.containsKey(blockMaterial)) {
            return;
        }

        final PlayerSkillData skillData = this.getSkillData(player);
        if (skillData == null) {
            m_plugin.logError("Failed to find '" + player.getName() + "s' player skill data.");
            return;
        }

        if (!skillData.canUseAbility()) {
            m_plugin.sendMessage(player, "You can not use your " + this.getName() + " ability for another " + skillData.getTimeoutRemaining() + "...");
            return;
        }

        int secondsToHaveAbility = 3 + (int) Math.floor(skillData.getLevel() * 0.5);
        PotionEffect effect = new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * secondsToHaveAbility, 10, true);
        player.addPotionEffect(effect, true);

        int timeoutSeconds = secondsToHaveAbility + 300;
        skillData.setTimeout(System.currentTimeMillis() + (timeoutSeconds * 1000));

        m_plugin.sendMessage(player, "Activated your " + this.getName() + " ability!");
    }

    /**
     * 
     * @param event 
     */
    @EventHandler
    public void onItemHeldChanged(PlayerItemHeldEvent event) {
        
        final Player player = event.getPlayer();
        if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
            return;
        }
        
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.FAST_DIGGING) {
                if (effect.getAmplifier() == 20) {
                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                }
            }            
        }        
    }
    
    /**
     * 
     * @param inventory
     * @param ownerInventory
     * @param player 
     */
    @Override
    public void createSkillGUI(GuiInventory inventory, GuiInventory ownerInventory, Player player) {
        
        int slot = 0;
        for (Entry<Material, BlockData> entry : m_blocks.entrySet()) {

            Material block = entry.getKey();
            BlockData data = entry.getValue();

            ItemStack previewItem = new ItemStack(block);

            String[] details = new String[] {
                "XP Gained " + data.getXp()
            };

            inventory.addMenuItem(formatMaterialName(block.name()), previewItem, details, slot++, 1, new GuiCallbackPreviousMenu(player, ownerInventory));
        }

    }
    
    /**
     *
     * @param raw
     * @return
     */
    private String formatMaterialName(String raw) {

        String lower = raw.toLowerCase();
        String[] parts = lower.split("_");
        String formatted = "";
        for (String part : parts) {
            formatted += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
        }
        return formatted;

    }
}
