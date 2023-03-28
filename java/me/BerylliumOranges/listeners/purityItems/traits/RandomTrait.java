package me.BerylliumOranges.listeners.purityItems.traits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class RandomTrait extends PurityItemAbstract {

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 10;
	}

	public int getPotionSeconds() {
		return 0;
	}

	public static final String TRAIT_ID = "[???]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.YELLOW + "Applies 2 random trait potions (?:??)"));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.YELLOW + "This is removed and a random trait is added"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {

			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					ArrayList<PurityItemAbstract> possibleTraits = new ArrayList<PurityItemAbstract>();
					for (PurityItemAbstract p : PurityItemAbstract.allPurityInstances) {
						if (!p.getTraitIdentifier().equals(RandomTrait.TRAIT_ID) && !hasPotionTrait(consumer, p.getTraitIdentifier())) {
							possibleTraits.add(p);
						}
					}
					if (possibleTraits.isEmpty())
						break;
					PurityItemAbstract p = possibleTraits.get((int) (possibleTraits.size() * Math.random()));
					addPotionTrait(consumer, p);
					consumer.sendMessage(
							ChatColor.GREEN + "+" + p.getName() + " " + ChatColor.WHITE + ItemBuilder.getTimeInMinutes(p.getPotionSeconds()));
				}
			}
		};

	}

	@Override
	public String getName() {
		return ChatColor.GOLD + "???";
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
		return ChatColor.GOLD + "Thrift Sword";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getToolName());
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		item.setItemMeta(meta);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		LivingEntity liv = (LivingEntity) e.getPlayer();
		for (int i = 0; i < EquipmentSlot.values().length; i++) {
			ItemStack item = liv.getEquipment().getItem(EquipmentSlot.values()[i]);
			if (item != null && item.hasItemMeta() && !ItemBuilder.isTraitPotion(item)) {
				replaceRandomTraits(liv, item);
			}
		}
	}

	public static void replaceRandomTraits(LivingEntity liv, ItemStack item) {
		ArrayList<PurityItemAbstract> traits = ItemBuilder.getAllItemTraits(item);
		int count = ItemBuilder.sumOfTraitInItem(liv, item, TRAIT_ID);
		if (count > 0) {
			for (int x = traits.size() - 1; x >= 0; x--) {
				if (traits.get(x).getTraitIdentifier().equals(TRAIT_ID)) {
					traits.remove(x);
				}
			}
			ArrayList<PurityItemAbstract> possibilities = new ArrayList<>();
			for (PurityItemAbstract pu : PurityItemAbstract.allPurityInstances) {
				if (item.getType().toString().toLowerCase().contains("pickaxe") || !pu.getTraitIdentifier().equals(Goblin.TRAIT_ID)) {
					possibilities.add(pu);
				}
			}
			for (int z = 0; z < count; z++) {
				traits.add(possibilities.get((int) (Math.random() * possibilities.size())));
			}
			ItemBuilder.buildItem(item, traits);
		}
	}
}
