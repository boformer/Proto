package dummy.sponge;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.world.WorldEvent;
import org.spongepowered.api.world.Location;

/**
 * Placeholder class to replace missing features in the Sponge API. <u>Only for development!</u>
 */
public interface BlockBreakEventDummy extends WorldEvent, Cancellable
{
	public Player getPlayer();

	public Location getLocation();
}
