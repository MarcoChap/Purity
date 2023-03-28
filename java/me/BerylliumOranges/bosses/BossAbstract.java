package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.purityItems.traits.Goblin;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.main.DirectoryTools;
import me.BerylliumOranges.main.PluginMain;

public abstract class BossAbstract implements Listener {
	public static ArrayList<Class<BossAbstract>> bossClasses = loadBossClasses();
	public static ArrayList<BossAbstract> bossInstances = new ArrayList<>();
	public ArrayList<LivingEntity> bosses = new ArrayList<>();
	public ArrayList<Player> participants = new ArrayList<>();

	public int ticksAlive = 0;
	public static int maxTicksAlive = 6 * 60 * 20;

	public BossAbstract() {
		bossInstances.add(this);
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	public static ArrayList<Class<BossAbstract>> loadBossClasses() {
		ArrayList<Class<BossAbstract>> instances = new ArrayList<>();
		for (Class<?> clazz : DirectoryTools.getClasses("me/BerylliumOranges/bosses")) {
			if (!clazz.equals(BossAbstract.class) && BossAbstract.class.isAssignableFrom(clazz)) {
				try {
					instances.add((Class<BossAbstract>) clazz);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
		return instances;
	}

	public static final ArrayList<ItemStack> allDescriptions = loadDescriptions();
	public static final ArrayList<ItemStack> allSpawnEggs = loadSpawnEggs();

	public static ArrayList<ItemStack> loadDescriptions() {
		ArrayList<ItemStack> boss = new ArrayList<>();
		for (Class<BossAbstract> clazz : bossClasses) {
			try {
				boss.add((ItemStack) clazz.getMethod("getDescription", null).invoke(null));
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return boss;
	}

	public static ArrayList<ItemStack> loadSpawnEggs() {
		ArrayList<ItemStack> boss = new ArrayList<>();
		for (Class<BossAbstract> clazz : bossClasses) {
			try {
				boss.add((ItemStack) clazz.getMethod("getSpawnEgg", null).invoke(null));
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return boss;
	}

	public static ItemStack getDescription() {
		return null;
	}

	public static ItemStack getSpawnEgg() {
		return null;
	}

	public abstract List<ItemStack> getDrops();

	public abstract void startFight(Location loc);

	public abstract LivingEntity spawnBoss(Location loc);

	public abstract void despawnAll();

	// public ItemStack getItem() {
	// return addCostText(this.getItemWithoutCostText(), this);
	// }

	// public static ItemStack addCostText(ItemStack item, BossAbstract boss) {
	// ItemMeta meta = item.getItemMeta();
	// ArrayList<String> lore = new ArrayList<>(meta.getLore());
	// lore.add("");
	// lore.add(ChatColor.GOLD + "Cost:");
	// if (boss.getMaterialCost().length == 0 && boss.getXPCost() == 0) {
	// lore.add(ChatColor.GREEN + " - FREE");
	// } else {
	// for (ItemStack i : boss.getMaterialCost()) {
	// lore.add(ChatColor.GREEN + "" + i.getAmount() + ChatColor.WHITE + "x" +
	// ChatColor.GREEN + i.getItemMeta().getDisplayName());
	// }
	// if (boss.getXPCost() > 0) {
	// lore.add(ChatColor.GREEN + "" + boss.getXPCost() + ChatColor.WHITE + "x"
	// + ChatColor.GREEN + "XP Levels");
	// }
	// }
	// return item;
	// }

	public static Player getRandomNearbyPlayer(Location l) {
		return getRandomNearbyPlayer(l, 30);
	}

	public static Player getRandomNearbyPlayer(Location l, int radius) {
		ArrayList<Player> ps = PluginMain.getNearbyPlayers(l, radius);
		if (ps.isEmpty())
			return null;
		return ps.get((int) (Math.random() * ps.size()));
	}

	public static Player getNearestPlayer(Location l, int radius) {
		Player closest = null;
		double min = Double.MAX_VALUE;
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			double d = p.getLocation().distanceSquared(l);
			if (min > d) {
				min = d;
				closest = p;
			}
		}
		if (min > radius * radius) {
			return null;
		}
		return closest;
	}

	public static void distrbuteDrops(List<ItemStack> drops, ArrayList<Player> participants) {
		if (participants.isEmpty())
			return;
		double multiplier = Math.pow(participants.size(), -0.2);
		for (Player p : participants) {
			for (ItemStack item : drops) {
				double temp = multiplier;
				if (PurityItemAbstract.hasPotionTrait(p, Goblin.TRAIT_ID)) {
					temp *= 1 + (Goblin.LOOT_BONUS / 100.0);
				}
				ItemStack copy = item.clone();
				copy.setAmount((int) ((1 + copy.getAmount()) * (Math.random() * temp)));

				p.getInventory().addItem(copy);
				p.giveExp(100);
			}
		}
	}

}
