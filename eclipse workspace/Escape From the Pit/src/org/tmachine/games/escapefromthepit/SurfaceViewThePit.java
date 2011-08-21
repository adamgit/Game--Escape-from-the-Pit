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
		 * Bad code from Android/Google/etc for being too stupid to understand the importance of width/height numbers on Auto-Rotate!
		 */
		
		
		//thread.resetShipPosition();
		
		game.fixPositionsAfterPhoneRotated( getContext().getResources().getDisplayMetrics().widthPixels, getContext().getResources().getDisplayMetrics().heightPixels );
		
		thread.start();
	}
	
	@Override public void surfaceDestroyed( SurfaceHolder holder )
	{
		Log.i( getClass().getName(), "surfaceDestroyed" );
		
		thread.waitUntilStoppedBecauseAndroidHasABrokenJVM();
	}
	
	/*@Override public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER )
		{
			Log.i( getClass().getName(), "DPAD_CENTER pressed; killing the main thread (debug feature: forces run thread to die, e.g. for tracing info)");
			thread.myThread = null;
		}
		
		return super.onKeyDown( keyCode, event );
	}*/
	
	protected float thisTouchX, thisTouchY;
	
	private void touch_start( float x, float y )
	{
		thisTouchX = x;
		thisTouchY = y;
	}
	
	/*
	private void touch_move( float x, float y )
	{
		if( thread.isRunning() ) // only move if the game is actually running
		{
			Entity ship = thread.getShip();
			
			if( ship != null )
			{
				Movable shipMove = es.getComponent( ship, Movable.class );
				
				shipMove.dx += x - thisTouchX;
				shipMove.dy += y - thisTouchY;
				
				// Log.i( getClass().getName(), "touch moved; moving ship from x = "+shipPosition.x+" to x = "+(shipPosition.x + shipMove.dx) );
				
				thisTouchX = x;
				thisTouchY = y;
			}
		}
	}
	*/
	
	private void touch_up()
	{
		
	}
	
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