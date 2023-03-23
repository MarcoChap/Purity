package me.BerylliumOranges.misc;

import java.util.Comparator;

import me.BerylliumOranges.listeners.purityItems.traits.FreeTraitSlot;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;

public class SortByPurityTrait implements Comparator<PurityItemAbstract> {
	@Override
	public int compare(PurityItemAbstract o1, PurityItemAbstract o2) {
		if (o1.getTraitIdentifier().equals(FreeTraitSlot.TRAIT_ID))
			return -1;
		return o1.getName().compareTo(o2.getName());
	}
}