package com.phaseos.realisticlumber;

import com.phaseos.listener.BlockBreakListener;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class RealisticLumber extends JavaPlugin {

    public final HashMap<Player, Block[]> trees = new HashMap<>();
    private FileConfiguration config;
    public boolean defaultActive;
    public boolean useAnything;
    public boolean moreDamageToTools;
    public boolean interruptIfToolBreaks;
    public boolean logsMoveDown;
    public boolean onlyTrees;
    public boolean popLeaves;
    public int leafRadius;
    protected String[] allowedTools;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        loadConfig();
    }

    @Override
    public void onDisable() {
    }

    private String arrayToString(String[] array, String separator) {
        String outString = "";
        String[] arrayOfString;
        int j = (arrayOfString = array).length;
        for (int i = 0; i < j; i++) {
            String string = arrayOfString[i];
            if (!"".equals(outString)) {
                outString += separator;
            }
            outString += string;
        }
        return outString;
    }

    private void loadConfig() {
        reloadConfig();
        this.config = getConfig();
        this.defaultActive = this.config.getBoolean("active-by-default", true);
        this.config.set("active-by-default", this.defaultActive);
        this.useAnything = this.config.getBoolean("use-anything", true);
        this.config.set("use-anything", this.useAnything);
        this.allowedTools = this.config.getString("allowed-tools", "WOOD_AXE,STONE_AXE,IRON_AXE,GOLD_AXE,DIAMOND_AXE").split(",");
        this.config.set("allowed-tools", arrayToString(this.allowedTools, ","));
        this.moreDamageToTools = this.config.getBoolean("more-damage-to-tools", true);
        this.config.set("more-damage-to-tools", this.moreDamageToTools);
        this.interruptIfToolBreaks = this.config.getBoolean("interrupt-on-tool-break", false);
        this.config.set("interrupt-on-tool-break", this.interruptIfToolBreaks);
        this.logsMoveDown = this.config.getBoolean("logs-fall-down", true);
        this.config.set("logs-fall-down", this.logsMoveDown);
        this.onlyTrees = this.config.getBoolean("only-trees", true);
        this.config.set("only-trees", this.onlyTrees);
        this.popLeaves = this.config.getBoolean("break-leaves", true);
        this.config.set("break-leaves", this.popLeaves);
        this.leafRadius = this.config.getInt("leaf-radius", 50);
        this.config.set("leaf-radius", this.leafRadius);
        saveConfig();
    }
}
