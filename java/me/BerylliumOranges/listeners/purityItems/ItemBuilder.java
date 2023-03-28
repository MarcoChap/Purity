package me.BerylliumOranges.listeners.purityItems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.customEvents.TraitAppliedEvent;
import me.BerylliumOranges.listeners.purityItems.traits.FreeTraitSlot;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import me.BerylliumOranges.misc.ResultAndCost;
import me.BerylliumOranges.misc.SortByPurityTrait;
import net.md_5.bungee.api.ChatColor;

public class ItemBuilder {

	public static ResultAndCost combineItems(@Nullable LivingEntity liv, ItemStack main, ItemStack material) {
		if (main == null || !main.hasItemMeta() || material == null || !material.hasItemMeta()) {
			return null;
		}
		ItemMeta meta = main.getItemMeta();

		if (meta instanceof Damageable) {
			ArrayList<PurityItemAbstract> mainTraits = getAllItemTraits(main);
			ArrayList<PurityItemAbstract> materialTraits = getAllItemTraits(material);
			ArrayList<PurityItemAbstract> resultTraits = new ArrayList<>(mainTraits);
			resultTraits.addAll(materialTraits);

			int slotsToTake = materialTraits.size() - sumOfTraitInItem(material, FreeTraitSlot.TRAIT_ID);
			int openTraitSlots = sumOfTraitInItem(main, FreeTraitSlot.TRAIT_ID);
			int cost = 0;
			for (PurityItemAbstract p : materialTraits) {
				if (p.isCurse())
					slotsToTake--;
				cost += p.getXPCost();
			}

			if (openTraitSlots - slotsToTake >= 0 && isTraitPotion(material)) {
				if (slotsToTake > 0)
					for (int i = resultTraits.size() - 1; i >= 0; i--) {
						if (resultTraits.get(i).getTraitIdentifier().equals(FreeTraitSlot.TRAIT_ID)) {
							resultTraits.remove(i);
							if (--slotsToTake == 0)
								break;
						}
					}

				ItemStack result = main.clone();
				ItemMeta resultMeta = result.getItemMeta();
				String traitLoc = "";

				Collections.sort(resultTraits, new SortByPurityTrait());

				for (PurityItemAbstract p : resultTraits) {
					traitLoc += p.getTraitIdentifier();
				}

				resultMeta.setLocalizedName(traitLoc);
				result.setItemMeta(resultMeta);
				ItemBuilder.buildItem(result, resultTraits);
				int count = getAllItemTraits(result).size();
				ItemCombineEvent e = new ItemCombineEvent(liv, main, material, result, count, resultTraits);
				Bukkit.getPluginManager().callEvent(e);
				return new ResultAndCost(result, cost);
			}
		}
		return null;
	}

	public static final String POTION_IDENTIFIER = "isPotion";

	public static boolean isTraitPotion(ItemStack item) {
		if (item == null || !item.hasItemMeta()) {
			return false;
		}
		return item.getItemMeta().getLocalizedName().startsWith(POTION_IDENTIFIER);
	}

	public static ItemStack buildPotionItem(ItemStack item, ArrayList<PurityItemAbstract> traits) {
		ItemMeta meta = item.getItemMeta();

		Collections.sort(traits, new SortByPurityTrait());

		ArrayList<String> lore = new ArrayList<>();

		String displayName = "";
		String localName = POTION_IDENTIFIER;
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "When Consumed");
		for (int i = 0; i < traits.size(); i++) {
			PurityItemAbstract t = traits.get(i);

			if (i < traits.size() - 1)
				displayName += ChatColor.RESET + "" + ChatColor.WHITE + " + ";
			else
				displayName = t.getName();
			for (String d : t.getPotionTraitDescription()) {
				lore.add("  " + d);
			}
			lore.add("");
			lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "When Applied to Tool");
			for (String d : t.getToolTraitDescription()) {
				lore.add("  " + d);
			}
			localName += t.getTraitIdentifier();
		}
		meta.setDisplayName(displayName + ChatColor.RESET + ChatColor.WHITE + " Potion");
		meta.setLocalizedName(localName);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack buildPotionItem(ItemStack item, PurityItemAbstract p) {
		ArrayList<PurityItemAbstract> l = new ArrayList<PurityItemAbstract>();
		l.add(p);
		return buildPotionItem(item, l);
	}

	public static ItemStack buildItem(ItemStack item, ArrayList<PurityItemAbstract> traits) {
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> att = meta.getAttributeModifiers();

		String localName = "";
		if (isTraitPotion(item))
			localName = POTION_IDENTIFIER;
		for (PurityItemAbstract p : traits) {
			lore.add("");
			lore.add(p.getName());
			for (String s : p.getToolTraitDescription()) {
				lore.add("  " + s);
			}
			localName += p.getTraitIdentifier();
		}

		if (att != null) {
			lore.add("");
			boolean found = false;
			for (Map.Entry<Attribute, AttributeModifier> a : att.entries()) {
				if (!found) {
					lore.add(ChatColor.GRAY + "When on "
							+ StringUtils.capitalize(StringUtils.replaceChars(a.getValue().getSlot().toString().toLowerCase(), '_', ' ')) + ":");
					found = true;
				}
				String add = ChatColor.BLUE + "+";
				ChatColor attColor = getColorFromAtribute(a.getKey());
				if (a.getValue().getAmount() < 0
						|| (a.getValue().getOperation().equals(Operation.MULTIPLY_SCALAR_1) && a.getValue().getAmount() < 1)) {
					add = ChatColor.RED + "";
				}

				String operation = "";
				if (a.getValue().getOperation().equals(AttributeModifier.Operation.ADD_SCALAR)) {
					operation = "%";
				}

				// Your going to have check to see if MULITPLY_SCALAR is %x or
				// something

				lore.add(add + StringUtils.replaceChars("" + a.getValue().getAmount(), ".0", "") + operation + " " + attColor
						+ StringUtils.capitaliseAllWords(StringUtils.replaceChars(a.getValue().getName(), '_', ' ')));
			}

			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		meta.setLocalizedName(localName);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ChatColor getColorFromAtribute(Attribute a) {
		switch (a) {
		case GENERIC_ATTACK_SPEED:
			return ChatColor.GREEN;
		case GENERIC_MAX_HEALTH:
			return ChatColor.RED;
		default:
			return ChatColor.BLUE;
		}
	}

	public static ArrayList<PurityItemAbstract> getAllItemTraits(ItemStack item) {
		ArrayList<PurityItemAbstract> matches = new ArrayList<>();

		if (item == null || !item.hasItemMeta())
			return matches;

		String s = item.getItemMeta().getLocalizedName();

		boolean match;
		do {
			match = false;
			for (PurityItemAbstract p : PurityItemAbstract.allPurityInstances) {
				int index = s.indexOf(p.getTraitIdentifier());
				if (index > -1) {
					s = s.substring(0, index) + s.substring(index + p.getTraitIdentifier().length(), s.length());
					matches.add(p);
					match = true;
				}
			}
		} while (match);
		return matches;
	}

	public static int sumOfTraitInEquipment(LivingEntity liv, String traitID, boolean apply) {
		ArrayList<ItemStack> equipment = new ArrayList<>(Arrays.asList(liv.getEquipment().getArmorContents()));
		equipment.add(liv.getEquipment().getItemInMainHand());
		equipment.add(liv.getEquipment().getItemInOffHand());
		int count = 0;
		for (ItemStack item : equipment) {
			count += sumOfTraitInItem(liv, item, traitID);
		}
		return count;
	}

	/** This method creates a TraitAppliedEvent **/
	public static int sumOfTraitInItem(LivingEntity liv, ItemStack item, String traitID) {
		int matches = sumOfTraitInItem(item, traitID);
		TraitAppliedEvent e = new TraitAppliedEvent(liv, PurityItemAbstract.getTraitInstance(traitID), item, matches);
		if (matches > 0) {
			Bukkit.getPluginManager().callEvent(e);
		}
		return e.getTimesToApply();
	}

	/**
	 * This method is exclusively used for counting traits on an item and does
	 * not call the trait applied event.
	 **/
	@Deprecated
	public static int sumOfTraitInItem(ItemStack item, String traitID) {
		if (item == null || !item.hasItemMeta())
			return 0;
		return StringUtils.countMatches(item.getItemMeta().getLocalizedName(), traitID);
	}

	public static String colorTextFade(String text, Color[] colors) {
		if (colors.length == 0)
			return text;
		String temp = "";
		for (int i = 0; i < text.length(); i++) {
			int index = ((i * (colors.length - 1)) / text.length());
			Color color1 = colors[index % colors.length];
			Color color2 = colors[(index + 1) % colors.length];
			double charsAllowed;

			if (colors.length == 1)
				charsAllowed = text.length();
			else
				charsAllowed = (text.length() / (colors.length - 1));

			if (text.length() % 2 == 1)
				charsAllowed += 1;

			double am = (i % (charsAllowed)) / (charsAllowed - 1);
			temp += ChatColor.of(new Color((int) ((am * color2.getRed()) + ((1 - am) * color1.getRed())),
					((int) ((am * color2.getGreen()) + ((1 - am) * color1.getGreen()))),
					((int) ((am * color2.getBlue()) + ((1 - am) * color1.getBlue()))))) + "" + text.charAt(i);
		}
		return temp;
	}

	public static String getTimeInMinutes(int seconds) {
		String s = "" + seconds % 60;
		if (seconds % 60 < 10)
			s = "0" + seconds % 60;
		return "(" + (seconds / 60) + ":" + s + ")";
	}

}
