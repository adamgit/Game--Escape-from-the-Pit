package org.tmachine.games.escapefromthepit;

import java.util.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
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
	 * @param a
	 * @param esmc
	 * @param v
	 */
	public MainRunThread( Activity a, EntityManager esmc, SurfaceViewThePit v, RenderSystemSimpleDrawable rs )
	{
		parentActivity = a;
		es = esmc;
		surfaceView = v;
		
		//screenManager = new DeviceScreenManager( surfaceView );
		renderingSystem = rs;
		//enemySystem = new EnemySystemWaves( es, renderingSystem ); // needs the rendering system so it can measure sprite-sizes etc
		
	}
	
	/**
	 * Simulate Thread.stop, because Sun refuses to.
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
			Log.i( getClass().getName(), "Main thread.start()" );
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
		
	}
	
	LinkedList<SubSystem> orderedSubSystems;
	
	public void loadAllCoreSubSystems()
	{
		orderedSubSystems = new LinkedList<SubSystem>();
		
		/**
		 * Here we'd normally load all the SubSystems, and add them in the order we want them to process each game-tick
		 * 
		 * But quick-n-dirty I ended up loading them all inside EscapeFromThePitActivity instead
		 */
	}
	
	public void run()
	{
		Log.i( getClass().getSimpleName(), "Starting thread (run method started)" );
		
		boolean gameOverTriggered = false;
		long currentFrameIndex = 0;
		long currentFrameTimesAccumulated = 0;
		long lastLoopStartTime = System.currentTimeMillis();
		//Debug.startMethodTracing( "escapePitTrace" );
		
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
				
				Thread.sleep( 5 );
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