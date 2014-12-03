package dummy.sponge;

import org.spongepowered.api.util.event.Cancellable;
import org.spongepowered.api.event.player.PlayerEvent;

/**
 * Placeholder class to replace missing features in the Sponge API. <u>Only for development!</u>
 */
public interface PlayerCommandPreprocessEventDummy extends PlayerEvent, Cancellable
{
	String getCommand();
}
