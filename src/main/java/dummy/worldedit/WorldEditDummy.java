package dummy.worldedit;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.world.World;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;

/**
 * Static placeholder class to replace missing features in the WorldEdit API. <u>Only for development!</u>
 */
public class WorldEditDummy
{
	public static LocalWorld getLocalWorld(World world) 
	{
		return null;
	}
	
	public static LocalPlayer getLocalPlayer(Player player) 
	{
		return null;
	}
}
