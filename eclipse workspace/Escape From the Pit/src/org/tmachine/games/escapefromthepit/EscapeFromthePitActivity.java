package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.CPosition;

import android.os.*;
import android.util.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class EscapeFromthePitActivity extends BetterActivity
{
	EntityManager em;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		ArrayList<EntityManager> cachedData = (ArrayList<EntityManager>) getLastNonConfigurationInstance();
		if( cachedData != null )
		{
			Log.i( getClass().getSimpleName(), "onCreate: ... found a LastNonConfigurationInstance, attempting to load it..." );
			em = (EntityManager) cachedData.get( 0 );
			
			
			/**
			 * We loaded a cached copy of the ES, so have to manually do this auto-setup
			 */
			MetaEntity.defaultEntityManager = em;
		}

		
		Log.i(""+this.getClass(), "activity onCreate");
	}
	
	protected void handleActivityCreatedFirstTime()
	{
		Log.i(""+this.getClass(), "activity created first time");
		
	}
	
	protected void handleActivityNew()
	{
		Log.i(""+this.getClass(), "activity created new");
	}
	
	protected void handleActivityReturnedToScreen()
	{
		Log.i(""+this.getClass(), "activity returned to screen");
	}
	
	protected Object handleAutoRotateSaveState()
	{
		Log.i(""+this.getClass(), "asked to prep object to save state prior to autorotate");
		
		ArrayList<EntityManager> l = new ArrayList<EntityManager>();
		l.add( em );
		return l;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		em = new EntityManager();
		MetaEntity.defaultEntityManager = em;
		Game game = new Game( em );
		
		/**
		 * create the surface + thread
		 */
		SurfaceViewThePit surfaceView = new SurfaceViewThePit( this, game );
		RenderSystemSimpleDrawable renderSystem = new RenderSystemSimpleDrawable(em, surfaceView, game );
		MainRunThread runGameThread = new MainRunThread( this, em, surfaceView, renderSystem );
		runGameThread.loadAllCoreSubSystems();
		
		//runGameThread.setGameResult( gameToStart );
		SubsystemTouchHandler systemTh = new SubsystemTouchHandler(em);
		runGameThread.orderedSubSystems.addLast(systemTh); // MUST be before the Collision Subsystem
		TouchListenerPlayerMovement thv = new TouchListenerPlayerMovement( systemTh );
		
		SubsystemMovementAndCollision systemCollision = new SubsystemMovementAndCollision( em, renderSystem );
		runGameThread.orderedSubSystems.addLast(systemCollision);
		
		SubsystemLighting systemLighting = new SubsystemLighting(em, game);
		runGameThread.orderedSubSystems.addLast(systemLighting);
		
		surfaceView.setOnTouchListener(thv);
		surfaceView.thread = runGameThread;
		Log.i( getClass().getSimpleName(), "initialized thread and surface" );
		
		/**
		 * Finally ... tell the game that the ES is now valid, it's ship reference is OK, and it can do game-setup
		 */
		game.preSetupGame();
		renderSystem.shiftCanvasToKeepPositionOnScreen( new CPosition(game.initialPlayerLocation.x, game.initialPlayerLocation.y, game.mazeCellWidth, game.mazeCellHeight) );
		
		// turn off the window's title bar
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		setContentView( surfaceView );
	}
	
	protected void onResume()
	{
		super.onResume();
	};
}