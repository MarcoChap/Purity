package me.BerylliumOranges.misc;

import org.bukkit.inventory.ItemStack;

public class ResultAndCost {
	private ItemStack item;
	private int cost;

	public ResultAndCost(ItemStack item, int cost) {
		this.item = item;
		this.cost = cost;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}