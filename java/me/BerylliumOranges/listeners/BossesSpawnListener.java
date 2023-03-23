package me.BerylliumOranges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.BerylliumOranges.bosses.BossAbstract;
import me.BerylliumOranges.bosses.Treant;
import me.BerylliumOranges.main.PluginMain;

public class BossesSpawnListener implements Listener {

	public BossesSpawnListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onReload(PluginDisableEvent e) {
		Bukkit.broadcastMessage("Marco is reloading the server because he is annoying... Expect lag");
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
