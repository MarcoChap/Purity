package me.BerylliumOranges.listeners.purityItems.traits;

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

public class FreeTraitSlot extends PurityItemAbstract {

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 10;
	}

	public int getPotionSeconds() {
		return 90;
	}

	public static final String TRAIT_ID = "[Open]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.GOLD + "All " + ChatColor.YELLOW + "traits" + ChatColor.GOLD + " apply an extra time "
				+ ChatColor.GOLD + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays.asList(ChatColor.GRAY + "has space for a trait"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return null;
	}

	@Override
	public String getName() {
		return ChatColor.WHITE + "Free Trait Slot";
	}

	@Override
	public ItemStack getPotionItem() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.setBasePotionData(new PotionData(PotionType.LUCK));
		potion.setItemMeta(pm);

		return ItemBuilder.buildPotionItem(potion, this);
	}

	@Override
	public String getToolName() {
		return ChatColor.GRAY + "Swoperd";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getToolName());
		item.setItemMeta(meta);
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		traits.add(this);
		traits.add(this);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	@EventHandler
	public void onTraitApply(TraitAppliedEvent e) {
		if (e.getEntity() instanceof LivingEntity && PurityItemAbstract.hasPotionTrait((LivingEntity) e.getEntity(), getTraitIdentifier())) {
			e.setTimesToApply(e.getTimesToApply() * 2);
		}
	}
}
