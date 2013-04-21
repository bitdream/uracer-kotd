
package com.bitfire.uracer.game.logic.post;

import com.bitfire.uracer.game.player.PlayerCar;

public interface PostProcessingAnimator {
	void update (float zoom, float warmUpCompletion);

	void alertWrongWayBegins (int milliseconds);

	void alertWrongWayEnds (int milliseconds);

	public void alertCollision (float collisionFactor, int milliseconds);

	void reset ();

	void setPlayer (PlayerCar player);
}
