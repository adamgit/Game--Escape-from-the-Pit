package org.tmachine.games.escapefromthepit;

import android.os.*;
import android.util.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class EscapeFromthePitActivity extends BetterActivity
{
	EntityManager em;
	
	/** Called when the activity is first created. */
	/*@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		Log.i(""+this.getClass(), "activity onCreate");
	}*/
	
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
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		em = new EntityManager();
		Game game = new Game( em );
		
		/**
		 * create the surface + thread
		 */
		SurfaceViewThePit surfaceView = new SurfaceViewThePit( this, game );
		MainRunThread runGameThread = new MainRunThread( this, em, surfaceView );
		//runGameThread.setGameResult( gameToStart );
		surfaceView.thread = runGameThread;
		Log.i( getClass().getSimpleName(), "initialized thread and surface" );
		
		/**
		 * Finally ... tell the game that the ES is now valid, it's ship reference is OK, and it can do game-setup
		 */
		game.preSetupGame();
		
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