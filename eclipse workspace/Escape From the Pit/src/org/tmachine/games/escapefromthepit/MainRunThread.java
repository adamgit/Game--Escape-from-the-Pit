package org.tmachine.games.escapefromthepit;

import java.util.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class MainRunThread extends Thread implements iRunThread
{
	class GameOverError extends Error
	{
		
	}
	
	protected Thread myThread;
	Activity parentActivity;
	protected SurfaceViewThePit surfaceView;
	//protected DeviceScreenManager screenManager;
	
	protected EntityManager es;
	//protected GameResult gameResult;
	protected RenderSystemSimpleDrawable renderingSystem;
	
	/**
	 * Remember to call {@link #setGameResult(GameResult)} as well with your new or resumed game data
	 * (default value is null) before you start the thread.
	 * 
	 * @param a
	 * @param esmc
	 * @param v
	 */
	public MainRunThread( Activity a, EntityManager esmc, SurfaceViewThePit v )
	{
		parentActivity = a;
		es = esmc;
		surfaceView = v;
		
		//screenManager = new DeviceScreenManager( surfaceView );
		renderingSystem = new RenderSystemSimpleDrawable( es, surfaceView );
		//enemySystem = new EnemySystemWaves( es, renderingSystem ); // needs the rendering system so it can measure sprite-sizes etc
		
	}
	
	/*public void setGameResult( GameResult gr )
	{
		gameResult = gr;
		enemySystem.gameResult = gameResult;
	}*
	
	/**
	 * This method is depressing; it exists because the Android maintainers are evil, and couldn't be bothered to implement a core Java Library method which Sun's arrogant language-maintainers marked as "deprecated" about 10 years ago and were too lazy to provide alternatives for - but Android doesn't throw an Error, it simply - SILENTLY - doesn't implement this core method; that's a pretty major bug!
	 */
	public void waitUntilStoppedBecauseAndroidHasABrokenJVM()
	{
		
		if( myThread != null )
		{
			Log.i( getClass().getSimpleName(), "Thread is running, but a stop is required; starting the busy-wait for thread to die (thanks for nothing, Android!)..." );
			
			boolean retry = true;
			myThread = null;
			while( retry )
			{
				try
				{
					join();
					retry = false;
				}
				catch( InterruptedException e )
				{
					Log.i( getClass().getSimpleName(), "I'm busy-waiting for the main render thread to die.." );
				}
			}
			
			Log.i( getClass().getSimpleName(), "Thread is NOW DEAD, according to the Android JVM (thanks for nothing, Android!)..." );
		}
	}
	
	public void start()
	{
		if( myThread == null )
		{
			myThread = new Thread( this );
			myThread.start();
		}
		else
		{
			Log.w( getClass().getName(), "Requested a thread.start(), but the thread is already running - ignoring this request! (myThread = " + myThread );
		}
	}
	
	public void loadFirstLevel()
	{
		/**
		 * If there are no waves, create a new one to start the game...
		 */
		/*if( es.getAllEntitiesPossessing( Wave.class ).size() < 1 )
		{
			enemySystem.createWave( 5, 1, 10f ); // Create the initial wave
		}*/
	}
	
	LinkedList<SubSystem> orderedSubSystems;
	
	public void loadAllCoreSubSystems()
	{
		orderedSubSystems = new LinkedList<SubSystem>();
		
		//orderedSubSystems.add( new EnemySystemWavesPatterned( null, es, renderingSystem ) ); // needs the rendering system so it can measure sprite-sizes etc
		
		//loopMoveAll( c, lastFrameTime ); // will move EVERYTHING that is movable and has a position
		
		//loopFireWeapons( lastFrameTime );
		
		/**
		 * This *REQUIRES* that the Canvas be static and not change underneath it If the screen changes size halfway through this method call it *MUST* be cancelled
		 */
		/*List<Entity> emigrants = loopCheckAndSelectEmigrants( c );
		if( myThread != null )
		{
			//Log.i( getClass().getSimpleName(), "Allegedly, the loop is safe; processing emigrants" );
			processEmigrants( emigrants );
		}
		else
		{
			Log.w( getClass().getName(), "WARNING: loop is unsafe, throwing away the emigrants-set" );
		}*/
		
		//loopCheckShotHits();
	}
	
	public void run()
	{
		Log.i( getClass().getSimpleName(), "Starting thread (run method started)" );
		
		boolean gameOverTriggered = false;
		long currentFrameIndex = 0;
		long currentFrameTimesAccumulated = 0;
		long lastLoopStartTime = System.currentTimeMillis();
		//Debug.startMethodTracing( );
		
		//gameResult.status = GameResultStatus.RUNNING;
		
		loadFirstLevel();
		
		if( orderedSubSystems == null )
			throw new IllegalStateException("Cannot start a MainRunThread until you've loaded all SubSystems; try calling loadAllCoreSubSystems() first" );
		
		/***********************************************************************
		 *         START OF MAIN BODY OF RUN LOOP
		 ***********************************************************************
		 */
		while( myThread != null )
		{
			long loopStartTime = System.currentTimeMillis();
			long lastFrameTime = loopStartTime - lastLoopStartTime;
			currentFrameTimesAccumulated += lastFrameTime;
			
			{
				/**
				 * Update the gameResult stats...
				 * 
				 * BUG in Android threading system, this var fails to write if you put it outside
				 * the while loop; can't see why, but if it's just the threading failing to update
				 * vars, then that's a very serious bug in the Android JVM 
				 */
				//gameResult.millisecondsPlayed += lastFrameTime;
			}
		
			Canvas c = surfaceView.getHolder().lockCanvas( null );
			try
			{
				/**
				 * Critical: lots of things in rendering depend on the size / shape of the Canvas;
				 * => we must make sure the renderingSystem has the latest, current, correct Canvas before we do anything else
				 */
				renderingSystem.canvas = c;
				
				for( SubSystem system : orderedSubSystems )
				{
					system.processOneGameTick(lastFrameTime);
				}
				
				synchronized( surfaceView.getHolder() )
				{
					renderingSystem.drawBackground();
					renderingSystem.processOneGameTick( lastFrameTime );
				}
				
				Thread.sleep( 10 );
			}
			catch( GameOverError goe )
			{
				Log.i( getClass().getSimpleName(), "GameOver; killing thread" );
				
				myThread = null;
				
				Log.i( getClass().getSimpleName(), "GameOver; locking Entity System" );
				es.freeze();
				
				gameOverTriggered = true;
			}
			catch( Throwable t )
			{
				Log.e( getClass().getSimpleName(), "Inside main draw loop, a major exception, killing draw thread:" + t );
				t.printStackTrace();
				myThread = null;
			}
			finally
			{
				// ANDROID EXAMPLE CODE COMMENT:
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if( c != null )
				{
					surfaceView.getHolder().unlockCanvasAndPost( c );
				}
				
				currentFrameIndex++;
				lastLoopStartTime = loopStartTime;
				int frameTimesPerSample = 25;
				if( currentFrameIndex % frameTimesPerSample == 0 )
				{
					//DEBUG: Log.i( getClass().getSimpleName(), "Averaged frame rate = " + frameTimesPerSample * 1000 / currentFrameTimesAccumulated + " fps" );
					currentFrameTimesAccumulated = 0;
				}
				
			}
		}
		//Debug.stopMethodTracing();
		
		if( gameOverTriggered )
		{
			/**
			 * Another bad design decision from the Android authors? This is not
			 * a great way to manage inter-Activity communication (is there a better way?)
			 */
			Intent i = parentActivity.getIntent();
			//gameResult.status = GameResultStatus.GAMEOVER;
			//i.putExtra( "com.redglasses.invaders.gameresult", gameResult );
			parentActivity.setResult( Activity.RESULT_OK, i );
			parentActivity.finish();
		}
		
		Log.i( getClass().getSimpleName(), "Thread-stop COMPLETE: (run method expired; mythread was set to null)" );
	}
	
	public boolean isRunning()
	{
		return myThread != null;
	}
}