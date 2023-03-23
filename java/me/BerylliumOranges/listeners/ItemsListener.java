package me.BerylliumOranges.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.main.PluginMain;

public class ItemsListener implements Listener {
	public ItemsListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void potionDrink(PlayerItemConsumeEvent e) {
		if (ItemBuilder.isTraitPotion(e.getItem())) {
			ArrayList<PurityItemAbstract> traits = ItemBuilder.getAllItemTraits(e.getItem());
			for (PurityItemAbstract p : traits) {
				PurityItemAbstract.addPotionTrait(e.getPlayer(), p);
			}
			e.setCancelled(true);
			e.getItem().setAmount(e.getItem().getAmount() - 1);
		}
	}
}
