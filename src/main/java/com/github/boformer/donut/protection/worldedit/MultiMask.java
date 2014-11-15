package com.github.boformer.donut.protection.worldedit;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.masks.Mask;

/**
 * A mask that is used to restrict WorldEdit to the plots claimed by the player. It is the union of a list of masks (e.g. one Mask for every plot).
 */
public class MultiMask implements Mask
{
	private List<Mask> masks = new ArrayList<Mask>();

	/**
	 * Adds a new mask.
	 * 
	 * @param mask The mask
	 */
	public void add(Mask mask)
	{
		masks.add(mask);
	}

	/**
	 * Removes a mask.
	 * 
	 * @param mask The mask
	 * @return <code>true</code> if this MultiMask contained the mask
	 */
	public boolean remove(Mask mask)
	{
		return masks.remove(mask);
	}

	/**
	 * Checks if a mask is part of this MultiMask
	 * 
	 * @param mask The mask
	 * @return <code>true</code> if this MultiMask contains the mask
	 */
	public boolean contains(Mask mask)
	{
		return masks.contains(mask);
	}

	@Override
	public void prepare(LocalSession session, LocalPlayer player, Vector target)
	{
		for (Mask mask : masks)
			mask.prepare(session, player, target);
	}

	@Override
	public boolean matches(EditSession editSession, Vector pos)
	{
		for (Mask mask : masks)
			if (mask.matches(editSession, pos)) return true;

		return false;
	}
}
