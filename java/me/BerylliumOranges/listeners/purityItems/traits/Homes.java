package me.BerylliumOranges.listeners.purityItems.traits;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class Homes extends PurityItemAbstract {
	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 4;
	}

	public int getPotionSeconds() {
		return 120;
	}

	public static final String TRAIT_ID = "[Home]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(Arrays
				.asList(ChatColor.WHITE + "TPA is instant and does not require permission " + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.WHITE + "You can set an extra home"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return null;
	}

	@Override
	public String getName() {
		return ChatColor.of(new Color(220, 220, 220)) + "+1 Home";
	}

	@Override
	public ItemStack getPotionItem() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.setBasePotionData(new PotionData(PotionType.SLOW_FALLING));
		potion.setItemMeta(pm);

		return ItemBuilder.buildPotionItem(potion, this);
	}

	@Override
	public String getToolName() {
		return ChatColor.of(new Color(150, 150, 150)) + "Fond of Home";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(getToolName());
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		item.setItemMeta(meta);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

}
