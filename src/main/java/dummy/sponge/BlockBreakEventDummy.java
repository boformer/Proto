package dummy.sponge;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.world.WorldEvent;
import org.spongepowered.api.world.Location;

public interface BlockBreakEventDummy extends WorldEvent, Cancellable
{
	public Player getPlayer();

	public Location getLocation();
}
