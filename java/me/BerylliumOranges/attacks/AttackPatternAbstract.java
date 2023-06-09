package me.BerylliumOranges.attacks;

import org.bukkit.entity.LivingEntity;

public abstract class AttackPatternAbstract {
	protected LivingEntity entity;
	protected LivingEntity target;
	protected int attackIndex = 0;
	protected int ticks = 0;

	public AttackPatternAbstract(LivingEntity entity, LivingEntity target) {
		this.entity = entity;
		this.target = target;
	}

	public abstract void tick();

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public void setTarget(LivingEntity target) {
		this.target = target;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public int getAttackIndex() {
		return attackIndex;
	}

	public void setAttackIndex(int attackIndex) {
		this.attackIndex = attackIndex;
	}
}
