package com.bitfire.uracer.game.tween;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.bitfire.uracer.game.GameEvents;
import com.bitfire.uracer.game.events.GameLogicEvent;
import com.bitfire.uracer.game.logic.hud.HudLabel;
import com.bitfire.uracer.game.logic.hud.HudLabelAccessor;
import com.bitfire.uracer.game.messager.Message;
import com.bitfire.uracer.game.messager.MessageAccessor;
import com.bitfire.uracer.utils.BoxedFloat;
import com.bitfire.uracer.utils.BoxedFloatAccessor;

public final class Tweener {
	private static final TweenManager manager = new TweenManager();

	private static final GameLogicEvent.Listener gameLogicEvent = new GameLogicEvent.Listener() {
		@Override
		public void gameLogicEvent( GameLogicEvent.Type type ) {
			switch( type ) {
			case onReset:
			case onRestart:
				clear();
				break;
			}
		}
	};

	public static void init() {
		GameEvents.gameLogic.addListener( gameLogicEvent );

		Tween.registerAccessor( Message.class, new MessageAccessor() );
		Tween.registerAccessor( HudLabel.class, new HudLabelAccessor() );
		Tween.registerAccessor( BoxedFloat.class, new BoxedFloatAccessor() );
	}

	private Tweener() {
	}

	public static void dispose() {
		clear();
	}

	public static void clear() {
		manager.killAll();
	}

	public static void start( Timeline timeline ) {
		timeline.start( manager );
	}

	public static void update( int deltaMillis ) {
		manager.update( deltaMillis );
	}
}