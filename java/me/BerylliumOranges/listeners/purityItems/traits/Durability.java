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

public class Durability extends PurityItemAbstract {

	public static final int TOOL_HEAL_BONUS = 10;
	public static final int POTION_HEAL_BONUS = 30;

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 4;
	}

	public int getPotionSeconds() {
		return 8 * 60;
	}

	public static final String TRAIT_ID = "[Durability]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(
				Arrays.asList(ChatColor.GRAY + "Your tools do not lose durability " + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Tool is " + ChatColor.AQUA + "Unbreakable"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return null;
	}

	@Override
	public String getName() {
		return ChatColor.GRAY + "Durability";
	}

	@Override
	public ItemStack getPotionItem() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.setBasePotionData(new PotionData(PotionType.SPEED));
		potion.setItemMeta(pm);

		return ItemBuilder.buildPotionItem(potion, this);
	}

	@Override
	public String getToolName() {
		return ChatColor.GRAY + "Vibranium Helm";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.TURTLE_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getToolName());
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		item.setItemMeta(meta);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity liv = (LivingEntity) e.getEntity();
			e.setAmount(e.getAmount() * (1 + TOOL_HEAL_BONUS * (ItemBuilder.sumOfTraitInEquipment(liv, getTraitIdentifier(), true)) / 100.0));
			if (PurityItemAbstract.hasPotionTrait(liv, getTraitIdentifier()))
				e.setAmount(e.getAmount() * (1 + POTION_HEAL_BONUS / 100.0));
		}
	}
}
