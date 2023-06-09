package me.BerylliumOranges.customEvents;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class ItemCombineEvent extends EntityEvent implements Cancellable {
	ItemStack main;
	ItemStack material;
	ItemStack result;
	int xpLevelCost;
	boolean isCancelled;
	LivingEntity entity;
	ArrayList<PurityItemAbstract> traits;
	private static final HandlerList handlers = new HandlerList();

	public ItemCombineEvent(LivingEntity entity, ItemStack main, ItemStack material, ItemStack result, int xpLevelCost,
			ArrayList<PurityItemAbstract> traits) {
		super(entity);
		this.main = main;
		this.material = material;
		this.result = result;
		this.xpLevelCost = xpLevelCost;
		this.traits = traits;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public ItemStack getMainItem() {
		return main;
	}

	public void setMainItem(ItemStack main) {
		this.main = main;
	}

	public ItemStack getMaterialItem() {
		return material;
	}

	public void setMaterialItem(ItemStack material) {
		this.material = material;
	}

	public ItemStack getResult() {
		return result;
	}

	public void setResult(ItemStack result) {
		this.result = result;
	}

	public int getXpLevelCost() {
		return xpLevelCost;
	}

	public void setXpLevelCost(int xpLevelCost) {
		this.xpLevelCost = xpLevelCost;
	}

	public ArrayList<PurityItemAbstract> getTraits() {
		return traits;
	}

	public void setTraits(ArrayList<PurityItemAbstract> traits) {
		this.traits = traits;
	}

}
