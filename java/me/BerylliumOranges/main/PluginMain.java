package me.BerylliumOranges.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.BerylliumOranges.bosses.BossAbstract;
import me.BerylliumOranges.listeners.BossesSpawnListener;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class PluginMain extends JavaPlugin implements Listener {
	public static BossesSpawnListener pl;
	private static ArrayList<PurityItemAbstract> purityItemsInstances = new ArrayList<>();
	private static PluginMain instance;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;

		// This is just here to ensure the BossAbstract class is loaded first
		BossAbstract.loadBossClasses();

		// loads trait listeners
		for (PurityItemAbstract p : PurityItemAbstract.allPurityInstances) {
			this.getServer().getPluginManager().registerEvents(p, this);
		}

		// loads listeners
		ArrayList<Listener> listeners = new ArrayList<>();
		for (Class<?> clazz : DirectoryTools.getClasses("me/BerylliumOranges/listeners")) {
			if (!PurityItemAbstract.class.isAssignableFrom(clazz) && Listener.class.isAssignableFrom(clazz)) {
				try {
					listeners.add((Listener) clazz.newInstance());
					Bukkit.getConsoleSender().sendMessage("Loaded listener " + clazz);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
		try {

			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {

					EveryTick.tick();
				}
			}, 0, 0);
		} catch (Exception er) {

		}

	}

	@Override
	public void onDisable() {

	}

	public static PluginMain getInstance() {
		return instance;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandParser.findCommand(sender, cmd, label, args);
	}

	public static int generateRand(int max, int min) {
		Random rand = new Random();
		int n = rand.nextInt(max) + min;
		return n;
	}

	public static Entity[] getNearbyEntities(Location l, double radius) {
		double chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();

		for (double chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (double chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
					if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
						radiusEntities.add(e);
				}
			}
		}

		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}

	public static ArrayList<Player> getNearbyPlayers(Location l, double radius) {
		ArrayList<Player> ps = new ArrayList<>();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(l) < radius * radius) {
				ps.add(p);
			}
		}
		return ps;
	}

	public static ArrayList<PurityItemAbstract> getPurityItemsInstances() {
		return purityItemsInstances;
	}

	public static void setPurityItemsInstances(ArrayList<PurityItemAbstract> purityItemsInstances) {
		PluginMain.purityItemsInstances = purityItemsInstances;
	}
}
