package me.BerylliumOranges.main;

import org.bukkit.Bukkit;

import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class EveryTick {
	public static void tick() {
		Bukkit.getServer().getPluginManager().callEvent(new BossTickEvent());
		PurityItemAbstract.tick();
	}
}
