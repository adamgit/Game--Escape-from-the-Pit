package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;
import org.tmachine.games.escapefromthepit.Components.CTouch.*;

import android.util.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class SubsystemTouchHandler implements SubSystem
{
	LinkedList<FixedMotionEvent> pendingMotionEvents;
	EntityManager em;
	
	float lastX, lastY;
	
	@Override
	public void processOneGameTick(long lastFrameTime)
	{
		synchronized (pendingMotionEvents)
		{
			//Log.i(getClass().getSimpleName(), "Found "+pendingMotionEvents.size()+" pending touch events" );
			
			for( FixedMotionEvent event : pendingMotionEvents )
			{
				processNextEvent( event );
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
		new MetaEntity( "dot arrow initial", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowdot), new CTouch( TouchType.NONE ) );
	}
	
	protected void touch_move( float x, float y )
	{
		Log.i( getClass().getSimpleName(), "touch_move... ("+x+","+y+")");
		for( UUID entity : em.getAllEntitiesPossessingComponent( CTouch.class ) )
		{
			Log.i("", "Removing entity that has CTOUCH: "+entity);
			em.killEntity(entity);
			Log.i("", "...removed entity that has CTOUCH: "+entity);
		}
		
		if( Math.abs(lastX-x) > Math.abs(lastY - y) )
		{
			if( lastX > x )
				new MetaEntity( "left arrow", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowleft), new CTouch( TouchType.LEFT ) );
			else
				new MetaEntity( "right arrow", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowright), new CTouch( TouchType.RIGHT ) );
		}
		else if( Math.abs(lastX-x) < Math.abs(lastY - y) )
		{
			if( lastY > y )
				new MetaEntity( "up arrow", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowup), new CTouch( TouchType.UP ) );
			else
				new MetaEntity( "down arrow", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowdown), new CTouch( TouchType.DOWN ) );
		}
		else
			new MetaEntity( "dot arrow transitory", new Position( 300, 300, 100, 100), new CAndroidDrawable( R.drawable.arrowdot), new CTouch( TouchType.NONE ) );
		
	}
	
	protected void touch_up( float x, float y )
	{
		for( UUID entity : em.getAllEntitiesPossessingComponent( CTouch.class ) )
		{
			Log.i("", "Removing entity that has CTOUCH: "+entity);
			em.killEntity(entity);
			Log.i("", "...removed entity that has CTOUCH: "+entity);
		}
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