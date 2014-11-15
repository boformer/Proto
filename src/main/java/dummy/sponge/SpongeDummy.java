package dummy.sponge;

import java.util.UUID;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.world.World;

/**
 * Static placeholder class to replace missing features in the Sponge API. <u>Only for development!</u>
 */
public class SpongeDummy
{
	public static UUID getPlayerUniqueId(Player player)
	{
		//TODO
		return null;
	}

	public static World getPlayerWorld(Player player)
	{
		//TODO
		return null;
	}
	
	public static int getWorldMaxHeight(World world) 
	{
		//TODO
		return 256;
	}

	public static boolean hasWorldPermission(Player player, World world, String permission)
	{
		//TODO
		return false;
	}

}
