package com.bitfire.uracer.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bitfire.testtilemap.TestTilemap;
import com.bitfire.uracer.Config;
import com.bitfire.uracer.Director;
import com.bitfire.uracer.Input;
import com.bitfire.uracer.Physics;
import com.bitfire.uracer.VersionInfo;
import com.bitfire.uracer.debug.Debug;
import com.bitfire.uracer.entities.Car;
import com.bitfire.uracer.entities.EntityManager;
import com.bitfire.uracer.simulation.CarContactFilter;
import com.bitfire.uracer.simulation.CarContactListener;
import com.bitfire.uracer.utils.Convert;

public class CarTestScreen extends Screen
{
	private FPSLogger fpslog = new FPSLogger();
	private Car car;
//	private GhostCar ghost;
	private CarContactListener carContact;
	private CarContactFilter carFilter;

	// test
	private TestTilemap tm;
	private Vector2 carStartPos = new Vector2();
	private Vector2 carStartTilePos = new Vector2();
//	private Vector2 replayCarStartPos = new Vector2();
//	private float replayCarStartOrient;

	public CarTestScreen()
	{
		Gdx.graphics.setVSync( true );

		tm = new TestTilemap();
		tm.create();

		Vector2 scaled_worldsize_px = new Vector2( tm.map.width * tm.map.tileWidth / tm.strategy.tileMapZoomFactor, tm.map.height
				* tm.map.tileHeight / tm.strategy.tileMapZoomFactor );

		Config.asDefault( tm.strategy.tileMapZoomFactor );
		Convert.init( tm.strategy, tm.map );
		Director.createFromPixels( this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Vector2( 0, 0 ),
				scaled_worldsize_px );

		Debug.create();
		carContact = new CarContactListener();
		carFilter = new CarContactFilter();
		Physics.create( new Vector2( 0, 0 ), false );
		Physics.world.setContactListener( carContact );
		Physics.world.setContactFilter( carFilter );
		EntityManager.create();

		carStartPos.set( Convert.tileToPx( 1, 0 ).add( Convert.scaledPixels( 64, -64 ) ) );
		carStartTilePos.set( Convert.pxToTile( carStartPos.x, carStartPos.y ) );

		// carStartPos.set( Convert.scaledPosition( 64, 64 ) );
		car = Car.create( tm.map /* for orthomesh support */, carStartPos, 90 );
//		car.record( true );
//		ghost = GhostCar.create( tm.map, Convert.scaledPosition( 0, 0 ), 90 );

		// render car's mesh
		// tm.disposableMeshes.add( car.mesh );
	}

	@Override
	public void removed()
	{
		super.removed();
		Debug.dispose();
	}

	private Vector2 carTileAt = new Vector2();
	private Vector2 lastCarTileAt = new Vector2();

	@Override
	public void tick()
	{
		if( Input.isOn( Keys.R ) )
		{
//			ghost.resetPhysics();
			car.resetPhysics();
			car.setTransform( carStartPos, 90 );
		}

		Vector3 pos = Director.pos();
		if( Input.isOn( Keys.UP ) )
			pos.y += 10;
		if( Input.isOn( Keys.DOWN ) )
			pos.y -= 10;
		if( Input.isOn( Keys.LEFT ) )
			pos.x -= 10;
		if( Input.isOn( Keys.RIGHT ) )
			pos.x += 10;

		EntityManager.raiseOnTick();

		lastCarTileAt.set( carTileAt );
		carTileAt.set( Convert.pxToTile( car.pos().x, car.pos().y ) );
		// System.out.println("this " + carTileAt + " last " + lastCarTileAt );
		if( (lastCarTileAt.x != carTileAt.x) || (lastCarTileAt.y != carTileAt.y) )
		{
			onTileChanged( carTileAt );
		}
	}

	// FIXME
	// shit, shouldn't this be subscribed instead, and not self-raised like
	// this?
	//
//	private boolean doRecord = true;
	protected void onTileChanged( Vector2 carAt )
	{
		boolean onStartZone = ( carAt.x == 1 && carAt.y == 0 );
		if(onStartZone)
		{
//			if(doRecord)
//			{
//				System.out.println("Recording...");
//				replayCarStartPos.set( car.pos() );
//				replayCarStartOrient = car.orient();
//				car.record( true );
//			}
//			else
//			{
//				System.out.println("Playing...");
//				car.record( false );
//				ghost.resetPhysics();
//				ghost.setReplay( car.getReplay(), replayCarStartPos, replayCarStartOrient, car.carDesc );
//			}
//
//			doRecord = !doRecord;
		}
	}

	@Override
	public void beforeRender( float timeAliasingFactor )
	{
		EntityManager.raiseOnBeforeRender( timeAliasingFactor );
	}

	@Override
	public void render( float temporalAliasingFactor )
	{
		GL20 gl = Gdx.graphics.getGL20();

		gl.glClearDepthf( 1 );
		gl.glClear( GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT );

		// follow the car
		Director.setPositionPx( car.state().position, false );

		// update cams and matrices
		// (shouldn't it be done first in "tick" instead to be in-sync for use
		// by entities)
		Director.update();

		// TODO: refactor this SHIT!
		// sync tilemap orthocamera to director's camera
		OrthographicCamera oc = tm.orthoCam;
		PerspectiveCamera pc = tm.perspCam;

		// scale position
		oc.position.set( Director.getCamera().position );
		oc.position.mul( tm.strategy.tileMapZoomFactor );
		tm.updateCams( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );

		tm.tileMapRenderer.render( oc );
		EntityManager.raiseOnRender( temporalAliasingFactor );
		tm.renderMeshes( gl, oc, pc );

		//
		// debug
		//

		if( Gdx.app.getType() == ApplicationType.Desktop )
		{
			Debug.renderB2dWorld( Director.getMatViewProjMt() );
		}

		Debug.begin();

		EntityManager.raiseOnDebug();

		OrthographicCamera cam = Director.getCamera();
		Debug.renderFrameStats( temporalAliasingFactor );

		int fontW = 6;
		int fontH = 12;
		String uRacerInfo = "uRacer " + VersionInfo.versionName;
		int sw = uRacerInfo.length() * fontW;

		Debug.drawString( uRacerInfo, Gdx.graphics.getWidth() - sw, 0, fontW, fontH );
		Debug.drawString( "cam x=" + cam.position.x + ", y=" + cam.position.y, 0, 200 );
		Debug.drawString( "mouse x=" + Input.getMouseX() + ", y=" + Input.getMouseY(), 0, 214 );
		Debug.drawString( "temp_alias=" + temporalAliasingFactor, 0, 221 );
		Debug.drawString( "subframe=" + Config.SubframeInterpolation, 0, 228 );

		Debug.end();
		// fpslog.log();
	}
}
