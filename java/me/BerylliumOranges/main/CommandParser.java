package me.BerylliumOranges.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.BossAbstract;
import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.misc.MiscItems;
import me.BerylliumOranges.misc.PotionTraitKey;

public class CommandParser {

	public static boolean findCommand(CommandSender sender, Command cmd, String label, String[] args) {
		{
			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("dummy")) {
				return true;
			} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("potions")) {
				sendPotions((Player) sender);
				return true;
			}

			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("checkitem")) {
				sender.sendMessage(((Player) sender).getItemInHand().getItemMeta().getLocalizedName());
				return true;
			}

			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("getitems")) {
				getItems(sender, args);
				return true;
			}
		}
		return false;
	}

	public static void sendPotions(Player p) {
		boolean found = false;
		for (PotionTraitKey pot : PurityItemAbstract.effectedEntities) {
			if (pot.getOwner().equals(p)) {
				if (!found) {
					p.sendMessage(ChatColor.GREEN + "Active Potions");
				}
				p.sendMessage(" -" + pot.getTrait().getName() + " " + ChatColor.WHITE + ItemBuilder.getTimeInMinutes(pot.getTicksLeft() / 20));
				for (String s : pot.getTrait().getPotionTraitDescription()) {
					p.sendMessage("   " + s.substring(0, s.length() - ItemBuilder.getTimeInMinutes(pot.getTrait().getPotionSeconds()).length()));
				}
				p.sendMessage("");
				found = true;
			}
		}
		if (!found)
			p.sendMessage(ChatColor.RED + "No Active Potions");
	}

	public static boolean getItems(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		Inventory inv = Bukkit.createInventory(p, 54, ChatColor.GREEN + "Plugin Items");

		inv.addItem(MiscItems.getPurityAnvil());

		for (PurityItemAbstract pa : PurityItemAbstract.allPurityInstances) {
			inv.addItem(pa.getPotionItem());
		}
		for (PurityItemAbstract pa : PurityItemAbstract.allPurityInstances) {
			inv.addItem(pa.getToolItem());
		}

		for (ItemStack item : BossAbstract.allSpawnEggs) {
			inv.addItem(item);
		}

		for (ItemStack item : BossAbstract.allDescriptions) {
			inv.addItem(item);
		}

		p.openInventory(inv);
		return true;
	}

	public static boolean GM(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Player pToSet = null;
		GameMode gm = null;
		if (args.length == 1)
			pToSet = (Player) sender;
		else {
			String name = args[1];
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getName().contains(name)) {
					pToSet = p;
					if (p.getName().equals(name))
						break;
				}
			}
		}

		if (args[0].contains("0")) {
			gm = GameMode.SURVIVAL;
		} else if (args[0].contains("1")) {
			gm = GameMode.CREATIVE;
		} else if (args[0].contains("2")) {
			gm = GameMode.ADVENTURE;
		} else if (args[0].contains("3")) {
			gm = GameMode.SPECTATOR;
		}
		if (gm == null || pToSet == null)
			return false;
		pToSet.setGameMode(gm);

		return true;
	}
}
