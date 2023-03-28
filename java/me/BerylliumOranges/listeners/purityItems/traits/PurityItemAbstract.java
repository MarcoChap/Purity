package me.BerylliumOranges.listeners.purityItems.traits;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.DirectoryTools;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.PotionTraitKey;

public abstract class PurityItemAbstract implements Listener {
	public static ArrayList<PurityItemAbstract> allPurityInstances = loadPurityObjects();

	public static final String TRAIT_ID = "NOTHING";

	protected PurityItemAbstract() {
	}

	public static PurityItemAbstract getTraitInstance(String traitID) {
		for (PurityItemAbstract p : allPurityInstances) {
			if (p.getTraitIdentifier().equals(traitID))
				return p;
		}
		return null;
	}

	public static ArrayList<PurityItemAbstract> loadPurityObjects() {
		ArrayList<PurityItemAbstract> instances = new ArrayList<>();
		for (Class<?> clazz : DirectoryTools.getClasses("me/BerylliumOranges/listeners/purityItems/traits")) {
			if (!clazz.equals(PurityItemAbstract.class) && PurityItemAbstract.class.isAssignableFrom(clazz)) {
				try {
					instances.add((PurityItemAbstract) clazz.newInstance());
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
		return instances;
	}

	public static ArrayList<PotionTraitKey> effectedEntities = new ArrayList<>();

	public static ArrayList<PurityItemAbstract> purityItemInstances = new ArrayList<>();

	public abstract boolean isCurse();

	public abstract int getXPCost();

	public abstract int getPotionSeconds();

	public abstract String getName();

	public abstract ItemStack getPotionItem();

	public abstract String getToolName();

	public abstract ItemStack getToolItem();

	public abstract String getTraitIdentifier();

	public abstract ArrayList<String> getPotionTraitDescription();

	public abstract ArrayList<String> getToolTraitDescription();

	public abstract BukkitRunnable getActivePotionRunnable(LivingEntity consumer);

	public static void tick() {
		for (int i = effectedEntities.size() - 1; i >= 0; i--) {
			PotionTraitKey p = effectedEntities.get(i);
			if (p.getActivePotionRunnable() != null) {
				p.getActivePotionRunnable().run();
			}
			if (p.setTicksLeft(p.getTicksLeft() - 1) <= 0) {
				effectedEntities.remove(p);
				if (p.getActivePotionRunnable() != null && p.getActivePotionRunnable().getTaskId() != -1)
					p.getActivePotionRunnable().cancel();
			}
		}
	}

	public static void registerPurityItemEvents() {
		for (PurityItemAbstract i : purityItemInstances) {
			registerPurityItemEvent(i);
		}
	}

	public static boolean registerPurityItemEvent(PurityItemAbstract instance) {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(instance, PluginMain.getInstance());
		return false;
	}

	public static boolean addPotionTrait(LivingEntity liv, PurityItemAbstract trait) {
		return addPotionTrait(liv, trait, false);
	}

	public static boolean addPotionTrait(LivingEntity liv, PurityItemAbstract trait, boolean force) {
		PotionTraitKey p = new PotionTraitKey(liv, trait);

		if (!force || effectedEntities.contains(p)) {

		}
		effectedEntities.add(p);
		return false;
	}

	public static boolean hasPotionTrait(LivingEntity liv, String traitID) {
		for (PotionTraitKey p : effectedEntities) {

			if (p.getOwner().equals(liv) && p.getTrait().getTraitIdentifier().equals(traitID)) {
				// Bukkit.broadcastMessage("CHECKING: " + p.getTrait().getName()
				// + " equals " +
				// p.getTrait().getTraitIdentifier().equals(traitID));
				return true;
			}
		}
		return false;
	}
}
