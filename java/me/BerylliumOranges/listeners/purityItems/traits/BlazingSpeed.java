package me.BerylliumOranges.listeners.purityItems.traits;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class BlazingSpeed extends PurityItemAbstract {

	public static final int ATTACK_COOLDOWN = 5;

	public boolean isCurse() {
		return false;
	}

	public int getXPCost() {
		return 4;
	}

	public int getPotionSeconds() {
		return 120;
	}

	public static final String TRAIT_ID = "[Blaze]";

	public String getTraitIdentifier() {
		return TRAIT_ID;
	}

	public ArrayList<String> getPotionTraitDescription() {
		return new ArrayList<>(
				Arrays.asList(COLOR + "Gain Speed V while on fire and stay on fire longer " + ItemBuilder.getTimeInMinutes(getPotionSeconds())));
	}

	public ArrayList<String> getToolTraitDescription() {
		return new ArrayList<>(Arrays.asList(COLOR + "Your first attack every " + ChatColor.WHITE + ATTACK_COOLDOWN + COLOR + " seconds deals",
				COLOR + "bonus damage based on " + ChatColor.WHITE + "movement speed"));
	}

	@Override
	public BukkitRunnable getActivePotionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {

			@Override
			public void run() {
				((Player) consumer).setWalkSpeed(0.2F);
				if (consumer.getFireTicks() >= 0) {
					consumer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 4));
				}
			}
		};
	}

	public static final ChatColor COLOR = ChatColor.of(new Color(235, 150, 70));

	@Override
	public String getName() {
		return ChatColor.of(new Color(255, 92, 40)) + "Blazing Speed";
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
		return ChatColor.of(new Color(150, 75, 0)) + "Smoldering Boots";
	}

	@Override
	public ItemStack getToolItem() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier(UUID.randomUUID(), "speed", 0.05, Operation.ADD_NUMBER, EquipmentSlot.FEET));
		meta.setDisplayName(getToolName());
		ArrayList<PurityItemAbstract> traits = new ArrayList<>();
		traits.add(this);
		item.setItemMeta(meta);
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	HashMap<Player, Integer> cooldowns = new HashMap<Player, Integer>();

	@EventHandler
	public void onFire(EntityCombustEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			if (PurityItemAbstract.hasPotionTrait((LivingEntity) e.getEntity(), getTraitIdentifier())) {
				e.setDuration(e.getDuration() * 2);
			}
		}
	}

	@EventHandler
	public void onTick(BossTickEvent e) {
		for (Map.Entry<Player, Integer> ent : cooldowns.entrySet()) {
			if (cooldowns.put(ent.getKey(), ent.getValue() - 1) <= 0) {
				cooldowns.remove(ent.getKey());
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if (e.getDamager() instanceof Player) {
			Player liv = (Player) e.getDamager();
			if (!cooldowns.containsKey(liv)) {
				int count = ItemBuilder.sumOfTraitInEquipment(liv, getTraitIdentifier(), true);
				if (count > 0) {
					cooldowns.put(liv, ATTACK_COOLDOWN * 20);
					int amplifier = 0;
					if (liv.getPotionEffect(PotionEffectType.SPEED) != null)
						amplifier = liv.getPotionEffect(PotionEffectType.SPEED).getAmplifier();

					double movement = (liv.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() - 0.05) * 10 + amplifier / 2.0;
					movement *= 1.50;
					movement *= count;

					liv.sendMessage("[" + getName() + ChatColor.WHITE + "]" + COLOR + " +" + ChatColor.WHITE + Math.floor(movement * 100) / 100.0
							+ COLOR + " damage");

					e.setDamage(e.getDamage() + movement);
					e.getDamager().getWorld().spawnParticle(Particle.LAVA,
							e.getDamager().getLocation().clone().add(0, e.getEntity().getHeight() / 2.0, 0)
									.subtract(e.getDamager().getLocation().clone().subtract(e.getEntity().getLocation()).multiply(0.7)),
							(int) Math.min(movement, 50));
				}
			}
		}
	}
}
