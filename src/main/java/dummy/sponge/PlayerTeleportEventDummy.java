package dummy.sponge;

import org.spongepowered.api.event.player.PlayerEvent;
import org.spongepowered.api.world.World;

/**
 * Placeholder class to replace missing features in the Sponge API. <u>Only for development!</u>
 */
public interface PlayerTeleportEventDummy extends PlayerEvent
{
	World getFromWorld();
	World getToWorld();
}
