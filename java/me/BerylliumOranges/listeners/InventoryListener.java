package me.BerylliumOranges.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.ResultAndCost;

public class InventoryListener implements Listener {
	public InventoryListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void rightClickAnvil(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = e.getClickedBlock();
			if (b.getType().equals(Material.ANVIL)) {
				BlockData data = b.getBlockData();

			}
		}
	}

	@EventHandler
	public void onPrepareSmith(PrepareSmithingEvent e) {
		SmithingInventory inv = (SmithingInventory) e.getInventory();
		ItemStack main = e.getInventory().getItem(0);
		ItemStack material = e.getInventory().getItem(1);
		if (main != null && main.hasItemMeta() && material != null && material.hasItemMeta()) {
			ResultAndCost rc = ItemBuilder.combineItems(e.getView().getPlayer(), main, material);
			if (rc != null) {
				e.setResult(rc.getItem());
			}
		}
	}

	@EventHandler
	public void onSmith(SmithItemEvent e) {
		if (e.getCursor() != null && !e.getCursor().getType().equals(Material.AIR))
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (ItemBuilder.getAllItemTraits(e.getInventory().getResult()).size() > 0) {
			e.getInventory().getItem(0).setAmount(e.getInventory().getItem(0).getAmount() - 1);
			e.getInventory().getItem(1).setAmount(e.getInventory().getItem(1).getAmount() - 1);
			e.setCursor(e.getInventory().getResult());
			e.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}
