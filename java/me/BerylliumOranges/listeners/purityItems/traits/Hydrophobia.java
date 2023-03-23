package me.BerylliumOranges.listeners.purityItems.traits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class Hydrophobia extends PurityItemAbstract {

	public static final int TOOL_HEAL_BONUS = 10;
	public static final int POTION_HEAL_BONUS = 30;

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 4;
	}

	public int getPotionSeconds() {
		return 180;
	}

	public static final String TRAIT_ID = "[Hydro]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.BLUE + "Teleport to a random nearby location when",
				ChatColor.BLUE + "you touch water " + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays
				.asList(ChatColor.BLUE + "Take " + ChatColor.WHITE + "2" + ChatColor.RED + " damage " + ChatColor.BLUE + "when you touch water"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {

			@Override
			public void run() {
			}
		};
	}

	@Override
	public String getName() {
		return ChatColor.RED + "Curse of Hydrophobia";
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
		return ChatColor.AQUA + "Pain";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(getToolName());

		meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
				new AttributeModifier(UUID.randomUUID(), "armor", 5, Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
				new AttributeModifier(UUID.randomUUID(), "toughness", 2, Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,
				new AttributeModifier(UUID.randomUUID(), "max_health", 5, Operation.ADD_NUMBER, EquipmentSlot.LEGS));

		meta.setDisplayName(getToolName());
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		item.setItemMeta(meta);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getPlayer() instanceof LivingEntity) {
			LivingEntity liv = (LivingEntity) e.getPlayer();
			if (liv.getLocation().getBlock().getType().equals(Material.WATER)) {
				int count = ItemBuilder.sumOfTraitInEquipment(liv, getTraitIdentifier(), true);
				liv.damage(count * 2);
				if (PurityItemAbstract.hasPotionTrait(liv, getTraitIdentifier())) {
					Location loc = liv.getLocation().clone().add(5 - Math.random() * 10, 5 - Math.random() * 10, 5 - Math.random() * 10);
					for (int i = 0; i < 10; i++) {
						if (loc.getBlock().getType().isSolid()) {
							loc.add(0, 1, 0);
						}
					}
					e.getPlayer().teleport(loc);
				}
			}
		}
	}
}
