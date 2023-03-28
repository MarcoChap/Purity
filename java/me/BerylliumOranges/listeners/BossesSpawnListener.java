package me.BerylliumOranges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.BerylliumOranges.bosses.BossAbstract;
import me.BerylliumOranges.bosses.CoalGoblin;
import me.BerylliumOranges.bosses.Treant;
import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.Goblin;
import me.BerylliumOranges.main.PluginMain;

public class BossesSpawnListener implements Listener {

	public BossesSpawnListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onReload(PluginDisableEvent e) {
		Bukkit.broadcastMessage("Reloading Server...");
	}

	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		if (BossAbstract.class != null && BossAbstract.bossInstances != null) {
			for (BossAbstract boss : BossAbstract.bossInstances) {
				boss.despawnAll();
			}
			BossAbstract.bossInstances.clear();
		}
	}

	public static final double COAL_GOBLIN_SPAWN_CHANCE = 0.008;

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		double spawnMultiplier = Math.pow(1 - (Goblin.GOBLIN_SPAWN_CHANCE_BONUS / 100.0),
				ItemBuilder.sumOfTraitInEquipment(e.getPlayer(), Goblin.TRAIT_ID, true));

		if (e.getBlock().getType().equals(Material.COAL_ORE) || e.getBlock().getType().equals(Material.DEEPSLATE_COAL_ORE)) {
			if (e.isDropItems()) {
				if (Math.random() * spawnMultiplier < COAL_GOBLIN_SPAWN_CHANCE) {
					CoalGoblin g = new CoalGoblin();
					g.spawnBoss(e.getBlock().getLocation().add(0.5, 0, 0.5));
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack() != null) {
			if (e.getItemDrop().getItemStack().getAmount() == 64
					&& e.getItemDrop().getItemStack().getType().toString().toLowerCase().endsWith("sapling")) {
				e.getItemDrop().getLocation().getWorld().playSound(e.getItemDrop().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
				BukkitTask task = new BukkitRunnable() {
					int counter = 0;

					public void run() {
						counter++;
						if (counter >= 40) {
							Treant b = new Treant();
							b.spawnBoss(e.getItemDrop().getLocation(), e.getItemDrop().getItemStack().getType());
							this.cancel();
						}
					}
				}.runTaskTimer(PluginMain.getInstance(), 1, 1);

				e.getItemDrop().remove();
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getItem() != null && e.getItem().hasItemMeta()) {
				String localName = e.getItem().getItemMeta().getLocalizedName();
				for (Class<BossAbstract> boss : BossAbstract.bossClasses) {
					try {
						String n = (String) boss.getField("NAME").get(null);
						if (n.equals(localName)) {
							BossAbstract b = boss.newInstance();
							b.spawnBoss(e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection()));
							e.getItem().setAmount(e.getItem().getAmount() - 1);
							e.setCancelled(true);
							return;
						}
					} catch (Exception er) {
						er.printStackTrace();
					}
				}

			}
		}
	}

	@EventHandler
	public void onInteract(CreatureSpawnEvent e) {
		if (e.getSpawnReason().equals(SpawnReason.SPAWNER_EGG) && e.getEntity().getCustomName() != null) {
			for (Class<BossAbstract> boss : BossAbstract.bossClasses) {
				try {
					String n = (String) boss.getField("NAME").get(null);
					if (n.equals(e.getEntity().getCustomName())) {
						e.setCancelled(true);
						return;
					}
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
	}
}
