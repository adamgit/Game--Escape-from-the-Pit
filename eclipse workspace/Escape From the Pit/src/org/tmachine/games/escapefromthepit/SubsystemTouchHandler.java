package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;
import org.tmachine.games.escapefromthepit.Components.CTouch.*;

import android.util.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;
import com.wikidot.entitysystems.rdbmsbeta.examplecomponents.*;

public class SubsystemTouchHandler implements SubSystem
{
	LinkedList<FixedMotionEvent> pendingMotionEvents;
	EntityManager em;
	
	float lastX, lastY;
	MetaEntity onScreenArrowIndicator;
	
	@Override
	public void processOneGameTick(long lastFrameTime)
	{
		synchronized (pendingMotionEvents)
		{
			//Log.i(getClass().getSimpleName(), "Found "+pendingMotionEvents.size()+" pending touch events" );
			
			/**
			 * Store the "last" values because they will be destroyed by the next step
			 */
			float initialX = lastX;
			float initialY = lastY;
			
			/**
			 * Process ALL events that came in since last game-tick,
			 * and allow them to update the "last" vars
			 */
			for( FixedMotionEvent event : pendingMotionEvents )
			{
				processNextEvent( event );
			}
			
			/**
			 * Finally, look at the net resulting change in X / Y, and react accordingly
			 */
			if( onScreenArrowIndicator != null )
			{
				MetaEntity player = MetaEntity.loadFromEntityManager( em.getAllEntitiesPossessingComponent(CPlayer.class).iterator().next() );
				float thresholdForIgnoringInput = 2;
				if( Math.abs(initialX-lastX) > Math.abs(initialY - lastY) )
				{
					if( Math.abs(initialX-lastX) > thresholdForIgnoringInput )
						if( initialX > lastX )
						{
							CPosition pos = player.get(CPosition.class);
							pos.x -= 10;
							player.get(CAndroidDrawable.class).resourceID = R.drawable.personleft;
							//"left arrow"
							onScreenArrowIndicator.get( CAndroidDrawable.class ).resourceID = ( R.drawable.arrowleft);
							onScreenArrowIndicator.get( CTouch.class ).value = TouchType.LEFT;
						}
						else
						{
							CPosition pos = player.get(CPosition.class);
							pos.x += 10;
							player.get(CAndroidDrawable.class).resourceID = R.drawable.personright;
							onScreenArrowIndicator.get( CAndroidDrawable.class ).resourceID = ( R.drawable.arrowright);
							onScreenArrowIndicator.get( CTouch.class ).value = TouchType.RIGHT;
						}
				}
				else if( Math.abs(initialX-lastX) < Math.abs(initialY - lastY) )
				{
					if( Math.abs(initialY-lastY) > thresholdForIgnoringInput )
						if( initialY > lastY )
						{	
							CPosition pos = player.get(CPosition.class);
							pos.y -= 10;
							onScreenArrowIndicator.get( CAndroidDrawable.class ).resourceID = ( R.drawable.arrowup);
							onScreenArrowIndicator.get( CTouch.class ).value = TouchType.UP;
						}
						else
						{
							CPosition pos = player.get(CPosition.class);
							pos.y += 10;
							onScreenArrowIndicator.get( CAndroidDrawable.class ).resourceID = ( R.drawable.arrowdown );
							onScreenArrowIndicator.get( CTouch.class ).value = TouchType.DOWN;
						}
				}
				else
				{
					onScreenArrowIndicator.get( CAndroidDrawable.class ).resourceID = ( R.drawable.arrowdot);
					onScreenArrowIndicator.get( CTouch.class ).value = TouchType.NONE;
				}
			}

			pendingMotionEvents.clear();
		}
	}
	
	public SubsystemTouchHandler( EntityManager em )
	{
		this.em = em;
		
		pendingMotionEvents = new LinkedList<FixedMotionEvent>();
	}
	
	protected void touch_start( float x, float y )
	{
		//Log.i(getClass()+"", "touch_start: adding a dot arrow");
		onScreenArrowIndicator = new MetaEntity( "dot arrow initial", new CPosition( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowdot), new CTouch( TouchType.NONE ) );
	}
	
	protected void touch_move( float x, float y )
	{
		//Log.i( getClass().getSimpleName(), "touch_move... ("+x+","+y+")");
		
	}
	
	protected void touch_up( float x, float y )
	{
		onScreenArrowIndicator.kill();
		onScreenArrowIndicator = null;		
	}
	
	
	public void addAsynchronousTouchInput( FixedMotionEvent event )
	{
		synchronized (pendingMotionEvents)
		{
			pendingMotionEvents.addLast(event);
		}
	}
	
	protected void processNextEvent( FixedMotionEvent event )
	{	
		//Log.i(getClass().getSimpleName(), "Next event, masked = "+event.touchType+" (DOWN = "+MotionEvent.ACTION_DOWN+", MOVE = "+MotionEvent.ACTION_MOVE+")");
		switch( event.touchType )
		{
			case MotionEvent.ACTION_DOWN:
				touch_start( event.x, event.y );
				break;
			case MotionEvent.ACTION_MOVE:
				if( Math.abs(lastX-event.x) > 4
				|| Math.abs(lastY - event.y ) > 4 )
					touch_move( event.x, event.y );
				break;
			case MotionEvent.ACTION_UP:
				touch_up( event.x, event.y );
				break;
		}
		
		lastX = event.x;
		lastY = event.y;
		
		//Log.i("", "touched!");
	}
}