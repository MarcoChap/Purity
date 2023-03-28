package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.attacks.VexAttackPattern;
import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.listeners.purityItems.traits.Bark;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.BossBarListener;
import net.md_5.bungee.api.ChatColor;

public class Treant extends BossAbstract {
	public static final String NAME = ChatColor.DARK_GREEN + "Treant";

	public static ItemStack getDescription() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(NAME);
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Spawn Conditions:");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "   Drop a stack of saplings");
		lore.add(ChatColor.WHITE + "Difficulty: " + ChatColor.RESET + ChatColor.GREEN + "Easy");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpawnEgg() {
		ItemStack item = new ItemStack(Material.WITCH_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(NAME);
		ArrayList<String> lore = new ArrayList<>();
		meta.setLore(lore);
		meta.setLocalizedName(NAME);
		item.setItemMeta(meta);
		return item;
	}

	public static final List<ItemStack> DROPS = Arrays.asList(PurityItemAbstract.getTraitInstance(Bark.TRAIT_ID).getToolItem(),
			new ItemStack(Material.GOLD_INGOT, 4), new ItemStack(Material.DIAMOND, 1));

	@Override
	public List<ItemStack> getDrops() {
		return DROPS;
	}

	@Override
	public void startFight(Location loc) {
		spawnBoss(loc);
	}

	@Override
	public LivingEntity spawnBoss(Location loc) {
		return spawnBoss(loc, Material.OAK_SAPLING);
	}

	@Override
	public void despawnAll() {
		for (LivingEntity b : bosses) {
			b.remove();
		}
		bosses.clear();
		for (VexAttackPattern v : vexes) {
			v.getEntity().remove();
		}
		vexes.clear();
		HandlerList.unregisterAll(this);
	}

	ArrayList<VexAttackPattern> vexes = new ArrayList<VexAttackPattern>();

	public LivingEntity spawnBoss(Location loc, Material mat) {
		Zombie z = loc.getWorld().spawn(loc, Zombie.class);
		loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2, 0);
		bosses.add(z);
		z.setMaxHealth(200);
		z.setHealth(200);

		z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 1));
		z.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 1));

		z.setAdult();
		z.setCustomName(ChatColor.DARK_GREEN + "SYDNEY BRANCHY, GOD OF PESTILLANCE");
		new BossBarListener(z, BarColor.GREEN, 0);

		String s = mat.toString();
		s = s.substring(0, s.indexOf("_SAPLING"));
		Material m = Material.valueOf(s + "_LOG");
		if (m == null) {
			m = Material.OAK_LOG;
		}

		z.getEquipment().setHelmet(new ItemStack(m));
		z.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		z.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		z.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));

		int c = (int) (3 + Math.random() * 2);
		for (int i = 0; i < c; i++) {
			Vex v = loc.getWorld().spawn(loc, Vex.class);
			// v.setInvisible(true);
			v.setInvulnerable(true);
			// v.set
			// v.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(0.5);
			// v.getEquipment().setHelmet(new ItemStack(mat));
			v.getEquipment().setItemInHand(new ItemStack(mat));
			Player p = getRandomNearbyPlayer(loc);
			if (p != null)
				v.setTarget(p);
			vexes.add(new VexAttackPattern(v, p));
		}

		Player target = getNearestPlayer(loc, 30);
		if (target != null)
			z.setTarget(target);

		return z;
	}

	boolean replacingGround = false;

	public void replaceGround(Location center) {
		replacingGround = true;
		int radius = 20; // 2 blocks in each direction to make a 5x5 area
		Material newMaterial = Material.RED_CONCRETE;
		Map<Location, Material> originalBlocks = new HashMap<>();

		int last = 0;
		for (double x = 0; x < 15; x++) {
			double i = 0;
			do {

				double angle = (Math.PI * 2) * (i / x);
				Vector vec;
				if (i < 0)
					vec = new Vector(0, 0, 0);
				else
					vec = new Vector(Math.cos(angle) * x, 0, Math.sin(angle) * x);
				Location currentLocation = center.clone().add(0, -5, 0);
				Block block = currentLocation.add(vec).getBlock();

				// Find the surface block
				while (!block.getRelative(BlockFace.UP).getType().isAir()) {
					block = currentLocation.add(0, 1, 0).getBlock();
				}

				// Save the original block type
				originalBlocks.put(block.getLocation(), block.getType());

				// Calculate the delay based on the distance from the center
				int delay = (int) Math.abs(x);

				// Schedule a delayed task to replace the block
				new BukkitRunnable() {
					@Override
					public void run() {
						currentLocation.getBlock().setType(newMaterial);
					}
				}.runTaskLater(PluginMain.getInstance(), delay * 1L);
				i += 0.1;
			} while (i < x);
		}

		// Schedule a delayed task to revert blocks and deal damage to players
		// after 40 ticks
		new BukkitRunnable() {

			@Override
			public void run() {
				World world = center.getWorld();
				for (Map.Entry<Location, Material> entry : originalBlocks.entrySet()) {
					Location loc = entry.getKey();
					Material originalMaterial = entry.getValue();

					// Revert the block to its original type
					loc.getBlock().setType(originalMaterial);

					// Check if there is a player on the block and deal 20
					// damage
					for (Player player : world.getPlayers()) {
						Location playerLocation = player.getLocation();
						if (playerLocation.getBlockX() == loc.getBlockX() && playerLocation.getBlockY() - 1 == loc.getBlockY()
								&& playerLocation.getBlockZ() == loc.getBlockZ()) {
							player.damage(20);
						}
					}
				}
				replacingGround = false;
			}

		}.runTaskLater(PluginMain.getInstance(), 40L + 2L * (radius * 2 + 1));
	}

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
			if (liv.getFireTicks() < 1)
				liv.setHealth(Math.min(liv.getHealth() + 0.04, liv.getMaxHealth()));
		}

		for (VexAttackPattern v : vexes) {
			v.tick();
		}

		if (!replacingGround) {
			if (ticksAlive % 20 == 0 && Math.random() > 0.5) {
				replaceGround(bosses.get((int) (bosses.size() * Math.random())).getLocation().subtract(0, 1, 0));
			}
		}

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