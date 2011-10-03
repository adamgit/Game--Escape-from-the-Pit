package org.tmachine.games.escapefromthepit;

import android.content.*;
import android.util.*;
import android.view.*;

public class SurfaceViewThePit extends SurfaceView implements SurfaceHolder.Callback
{
	protected iRunThread thread;
	protected Game game;
	
	//public InvadersSurfaceView( Context c, EntitySystem es )
	public SurfaceViewThePit( Context c, Game g )
	{
		super( c );
		
		game = g;
		
		getHolder().addCallback( this );
	}
	
	@Override public void surfaceCreated( SurfaceHolder holder )
	{
		Log.i( getClass().getName(), "surfaceCreated" );
		
		//game.resetShipPosition();
		
		/**
		 * This is stupid; bad code from Android/Google/etc for being too stupid to understand the importance of width/height numbers
		 * /
		thread.fixPositionsAfterPhoneRotated( getContext().getResources().getDisplayMetrics().widthPixels, 0 );
		
		thread.start();
		*/
	}
	
	@Override public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
	{
		Log.i( getClass().getName(), "surfaceChanged" );
		
		/**
		 * This is stupid; Google/Android says to do this in surfaceCreated, but their own code screws-up the width/height
		 * if you follow their sample code.
		 * Bad code from Android/Google/etc for being too ignorant to understand the importance of width/height numbers on Auto-Rotate!
		 */
		
		Log.i( getClass().getName(), "surfaceChanged: about to (re?-)start the main thread..." );
		thread.start();
	}
	
	@Override public void surfaceDestroyed( SurfaceHolder holder )
	{
		Log.i( getClass().getName(), "surfaceDestroyed" );
		
		thread.waitUntilStoppedBecauseAndroidHasABrokenJVM();
	}
	
	/**
	 * I think this is unused - I factored it out into the {@link TouchListenerPlayerMovement} class instead
	 */
	protected float thisTouchX, thisTouchY;
	
	/**
	 * I think this is unused - I factored it out into the {@link TouchListenerPlayerMovement} class instead
	 */
	private void touch_start( float x, float y )
	{
		thisTouchX = x;
		thisTouchY = y;
	}
	
	/**
	 * I think this is unused - I factored it out into the {@link TouchListenerPlayerMovement} class instead
	 */
	private void touch_up()
	{
		
	}
	
	/**
	 * I think this is unused - I factored it out into the {@link TouchListenerPlayerMovement} class instead
	 */
	@Override public boolean onTouchEvent( MotionEvent event )
	{
		
		float x = event.getX();
		float y = event.getY();
		
		switch( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				touch_start( x, y );
				break;
			case MotionEvent.ACTION_MOVE:
				//touch_move( x, y );
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				break;
		}
		return true;
	}
}