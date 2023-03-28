package me.BerylliumOranges.listeners.purityItems.traits;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.customEvents.TraitAppliedEvent;
import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class Goblin extends PurityItemAbstract {

	public static final int LOOT_BONUS = 10;
	public static final int GOBLIN_SPAWN_CHANCE_BONUS = 15;

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 10;
	}

	public int getPotionSeconds() {
		return 90;
	}

	public static final String TRAIT_ID = "[Goblin]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public static final ChatColor COLOR = ChatColor.of(new Color(242, 255, 143));

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(Arrays
				.asList(ChatColor.BLUE + "+" + LOOT_BONUS + "%" + COLOR + " Loot from bosses " + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(
				Arrays.asList(ChatColor.BLUE + "+" + GOBLIN_SPAWN_CHANCE_BONUS + "%" + COLOR + " Chance for Goblins to spawn when mining ores"));
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
		return ChatColor.YELLOW + "Goblin's Luck Potion";
	}

	@Override
	public ItemStack getPotionItem() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
		potion.setItemMeta(pm);

		return ItemBuilder.buildPotionItem(potion, this);
	}

	@Override
	public String getToolName() {
		return ChatColor.YELLOW + "Goblin's Pickaxe";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getToolName());
		item.setItemMeta(meta);
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		traits.add(this);
		ItemBuilder.buildItem(item, traits);
		return item;
	}
}
