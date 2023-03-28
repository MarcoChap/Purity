package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.BerylliumOranges.attacks.CoalGoblinAttack;
import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.listeners.purityItems.traits.Homes;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.misc.BossBarListener;
import me.BerylliumOranges.misc.MiscItems;
import net.md_5.bungee.api.ChatColor;

public class CoalGoblin extends BossAbstract {
	public static final String NAME = ChatColor.DARK_GRAY + "Coal Goblin";

	public static ItemStack getDescription() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(NAME);
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Spawn Conditions:");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "   Chance to spawn when mining coal");
		lore.add(ChatColor.WHITE + "Difficulty: " + ChatColor.RESET + ChatColor.GREEN + "Easy");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpawnEgg() {
		ItemStack item = new ItemStack(Material.BAT_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(NAME);
		ArrayList<String> lore = new ArrayList<>();
		meta.setLore(lore);
		meta.setLocalizedName(NAME);
		item.setItemMeta(meta);
		return item;
	}

	public static final List<ItemStack> DROPS = Arrays.asList(MiscItems.getFreeBoots(),
			PurityItemAbstract.getTraitInstance(Homes.TRAIT_ID).getPotionItem(), new ItemStack(Material.COAL, 30),
			new ItemStack(Material.EXPERIENCE_BOTTLE, 10));

	@Override
	public List<ItemStack> getDrops() {
		return DROPS;
	}

	@Override
	public void startFight(Location loc) {
		spawnBoss(loc);
	}

	@Override
	public void despawnAll() {
		for (LivingEntity b : bosses) {
			b.remove();
		}
		bosses.clear();
		t = null;
		HandlerList.unregisterAll(this);
	}

	CoalGoblinAttack t;

	public LivingEntity spawnBoss(Location loc) {
		Zombie z = loc.getWorld().spawn(loc, Zombie.class);
		// loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
		// 2, 0);
		loc.getWorld().createExplosion(loc, 1);
		bosses.add(z);
		z.setMaxHealth(50);
		z.setHealth(50);

		z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 1));

		z.setBaby();
		BossBarListener b = new BossBarListener(z, BarColor.WHITE, 0);
		b.bar.setTitle(NAME);

		ItemStack leather = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) leather.getItemMeta();
		chestplateMeta.setColor(Color.BLACK);
		leather.setItemMeta(chestplateMeta);

		z.getEquipment().setHelmet(new ItemStack(Material.COAL));
		z.getEquipment().setChestplate(leather);
		leather.setType(Material.LEATHER_LEGGINGS);
		z.getEquipment().setLeggings(leather);
		leather.setType(Material.LEATHER_BOOTS);
		z.getEquipment().setBoots(leather);

		Player target = getNearestPlayer(loc, 30);
		if (target != null) {
			Vector dir = target.getLocation().clone().subtract(z.getLocation()).toVector().normalize();
			Location l = z.getLocation().clone().setDirection(dir);
			z.teleport(l);
			z.setVelocity(dir);
			z.setTarget(target);
		}
		t = new CoalGoblinAttack(z, target);
		return z;
	}

	boolean replacingGround = false;

	@EventHandler
	public void onDeath(EntityDeathEvent e) {

		if (bosses.remove(e.getEntity())) {
			e.getDrops().clear();
			if (bosses.isEmpty()) {
				distrbuteDrops(getDrops(), participants);
				despawnAll();
			}
		}
	}

	@EventHandler
	public void onHurt(EntityDamageByEntityEvent e) {
		if (bosses.contains(e.getEntity()) && e.getDamager() instanceof Player) {
			if (!participants.contains(e.getDamager())) {
				participants.add((Player) e.getDamager());
			}
		}
	}

	@EventHandler
	public void tick(BossTickEvent e) {
		for (LivingEntity liv : bosses) {
			if (liv.getFireTicks() > 1)
				liv.setHealth(Math.min(liv.getHealth() + 0.10, liv.getMaxHealth()));
		}

		t.tick();

		boolean allDead = true;
		for (LivingEntity d : bosses) {
			if (!d.isDead())
				allDead = false;
		}

		if (ticksAlive > maxTicksAlive || allDead) {
			despawnAll();
		}
		ticksAlive++;
	}
}