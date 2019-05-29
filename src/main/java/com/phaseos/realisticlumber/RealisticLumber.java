package com.phaseos.realisticlumber;

import com.phaseos.listener.BlockBreakListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class RealisticLumber extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
