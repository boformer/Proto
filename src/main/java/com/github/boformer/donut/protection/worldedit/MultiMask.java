package com.github.boformer.donut.protection.worldedit;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.masks.Mask;

public class MultiMask implements Mask
{
	private List<Mask> masks = new ArrayList<Mask>();


	public MultiMask(Mask mask)
	{
		masks.add(mask);
	}

	public MultiMask(List<Mask> masks)
	{
		this.masks.addAll(masks);
	}

	public void add(Mask mask)
	{
		masks.add(mask);
	}

	public boolean remove(Mask mask)
	{
		return masks.remove(mask);
	}

	public boolean has(Mask mask)
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
