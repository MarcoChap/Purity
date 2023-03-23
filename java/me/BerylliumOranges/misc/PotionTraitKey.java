package me.BerylliumOranges.misc;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class PotionTraitKey implements Comparable {
	private LivingEntity owner;
	private PurityItemAbstract trait;
	private BukkitRunnable activePotionRunnable;
	private int ticksLeft;

	public PotionTraitKey(LivingEntity liv, PurityItemAbstract trait) {
		owner = liv;
		this.trait = trait;
		this.activePotionRunnable = trait.getActivePotionRunnable(liv);
		this.ticksLeft = trait.getPotionSeconds() * 20;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}

	public PurityItemAbstract getTrait() {
		return trait;
	}

	public void setTrait(PurityItemAbstract trait) {
		this.trait = trait;
	}

	public BukkitRunnable getActivePotionRunnable() {
		return activePotionRunnable;
	}

	public void setActivePotionRunnable(BukkitRunnable activePotionRunnable) {
		this.activePotionRunnable = activePotionRunnable;
	}

	public int getTicksLeft() {
		return ticksLeft;
	}

	public int setTicksLeft(int ticksLeft) {
		int t = this.ticksLeft;
		this.ticksLeft = ticksLeft;
		return t;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof PotionTraitKey) {
			PotionTraitKey p = (PotionTraitKey) arg0;
			if (p.getOwner().equals(owner) && p.getTrait().equals(trait)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof PotionTraitKey) {
			PotionTraitKey p = (PotionTraitKey) arg0;
			if (p.getOwner().equals(owner) && p.getTrait().equals(trait)) {
				return 0;
			}
		}
		return -1;
	}
}