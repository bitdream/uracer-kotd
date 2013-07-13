
package com.bitfire.uracer.game.task;

import com.bitfire.uracer.game.GameEvents;
import com.bitfire.uracer.game.events.TaskManagerEvent;
import com.bitfire.uracer.game.events.TaskManagerEvent.Order;
import com.bitfire.uracer.game.events.TaskManagerEvent.Type;

public abstract class Task implements TaskManagerEvent.Listener {

	private Order order;

	public Task () {
		this(Order.DEFAULT);
	}

	public Task (Order order) {
		this.order = order;
		GameEvents.taskManager.addListener(this, TaskManagerEvent.Type.onTick, order);
		GameEvents.taskManager.addListener(this, TaskManagerEvent.Type.onTickCompleted, order);
		GameEvents.taskManager.addListener(this, TaskManagerEvent.Type.onPause, order);
		GameEvents.taskManager.addListener(this, TaskManagerEvent.Type.onResume, order);
	}

	public void dispose () {
		GameEvents.taskManager.removeListener(this, TaskManagerEvent.Type.onTick, order);
		GameEvents.taskManager.removeListener(this, TaskManagerEvent.Type.onTickCompleted, order);
		GameEvents.taskManager.removeListener(this, TaskManagerEvent.Type.onPause, order);
		GameEvents.taskManager.removeListener(this, TaskManagerEvent.Type.onResume, order);
	}

	protected abstract void onTick ();

	protected void onTickCompleted () {
	}

	protected void onGamePause () {
	}

	protected void onGameResume () {
	}

	@Override
	public void handle (Object source, Type type, Order order) {
		switch (type) {
		case onTick:
			onTick();
			break;
		case onTickCompleted:
			onTickCompleted();
			break;
		case onPause:
			onGamePause();
			break;
		case onResume:
			onGameResume();
			break;
		}
	}
}
