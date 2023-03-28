package me.BerylliumOranges.misc;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.FreeTraitSlot;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class MiscItems {

	public static ItemStack getPurityAnvil() {
		return new ItemStack(Material.SMITHING_TABLE);
	}

	public static ItemStack getFreeBoots() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		item.setItemMeta(meta);
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(PurityItemAbstract.getTraitInstance(FreeTraitSlot.TRAIT_ID));
		ItemBuilder.buildItem(item, traits);
		return item;
	}
}
