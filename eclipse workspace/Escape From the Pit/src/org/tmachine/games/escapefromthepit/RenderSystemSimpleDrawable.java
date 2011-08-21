package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.CAndroidDrawable;
import org.tmachine.games.escapefromthepit.Components.Movable;
import org.tmachine.games.escapefromthepit.Components.Position;

import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class RenderSystemSimpleDrawable implements SubSystem
{

	protected EntityManager entitySystem;
	protected Canvas canvas;
	protected SurfaceView surfaceView;
	
	protected Drawable imShip, imEvilCube1;
	protected HashMap<Integer, Drawable> drawablesCache;
	
	public RenderSystemSimpleDrawable( EntityManager em, SurfaceView sv )
	{
		entitySystem = em;
		surfaceView = sv;
	
		/**
		 * This is terrible app design, but it seems that Android forces us to do it;
		 * I can't see any other obvious way of doing this sensibly without writing
		 * lots and lots of boilerplate code 
		 */
		drawablesCache = new HashMap<Integer, Drawable>();
		encacheDrawable( R.drawable.medicine );
		/*encacheDrawable( R.drawable.rot2 );
		encacheDrawable( R.drawable.rot3 );
		encacheDrawable( R.drawable.spikey_level1 );
		encacheDrawable( R.drawable.spikey_level2 );
		encacheDrawable( R.drawable.spikey_level3 );
		
		imShip = surfaceView.getContext().getResources().getDrawable( R.drawable.ship );
		imEvilCube1 = surfaceView.getContext().getResources().getDrawable( R.drawable.evilcube1 );
		//imEvilCube1 = surfaceView.getContext().getResources().getDrawable( R.drawable.red_ball );
		 */
	}
	
	protected void encacheDrawable( int id )
	{
		drawablesCache.put( id, surfaceView.getContext().getResources().getDrawable( id ) );
	}
	
	protected void drawBackground()
	{
		canvas.drawColor( 0xFF000000 ); // poorly named method from Android API - this means "clear-screen-to-color()"
		paintStarfield( canvas );
	}
	
	public int getPlayAreaWidth()
	{
		return canvas.getWidth();
	}
	
	public int getPlayAreaHeight()
	{
		return canvas.getHeight();
	}
	
	//float rot = 0f;
	public void processOneGameTick(long lastFrameTime)
	{
		// Log.i( getClass().getName(), "doDraw()");
		
		/**
		 * just paint everything that has a CAndroidDrawable component
		 */
		Set<UUID> allDrawables = entitySystem.getAllEntitiesPossessingComponent( CAndroidDrawable.class );
		Paint paint = new Paint();
		paint.setARGB( 255, 127, 0, 0 );
		for( UUID entityID : allDrawables )
		{
			Position pos = entitySystem.getComponent(entityID, Position.class );
			
			Drawable androidDrawable = imEvilCube1;
			
			androidDrawable = drawablesCache.get( entitySystem.getComponent(entityID, CAndroidDrawable.class ).resourceID );
				
			/*
			 * RectF oval = new RectF( pos.x - 20, pos.y - 20, pos.x + 20, pos.y + 20 );
			 * 
			 * canvas.drawRoundRect( oval, 5, 5, paint );
			 */
			if( pos.rotationDegrees != 0.0f )
			{
			canvas.save();
			canvas.rotate( pos.rotationDegrees, pos.x, pos.y );
			}
			
			positionAndDraw( pos, androidDrawable );
			
			if( pos.rotationDegrees != 0 )
				canvas.restore();
		}
	}
	
	/**
	 * Utility method to make up for missing method in the Android library, and merge with Position class
	 * 
	 * @param p Position of the thing you want to draw
	 * @param d The sprite/rectangle/ellipse/whatever of the thing you want to draw
	 */
	protected void positionAndDraw( Position p, Drawable d )
	{
		d.setBounds( (int) p.x - d.getIntrinsicWidth() / 2, (int) p.y - d.getIntrinsicHeight() / 2, (int) p.x + d.getIntrinsicWidth() / 2, (int) p.y + d.getIntrinsicHeight() / 2 );
		d.draw( canvas );
	}
	
	
	Position[] starPositions;
	Movable[] starMotions;
	
	protected void paintStarfield( Canvas c )
	{
		if( starPositions == null )
		{
			int MAX_STARS = 60;
			starPositions = new Position[MAX_STARS];
			starMotions = new Movable[MAX_STARS];
			
			for( int i = 0; i < starPositions.length; i++ )
			{
				starPositions[i] = new Position();
				starPositions[i].x = (float) (Math.random() * c.getWidth());
				starPositions[i].y = (float) (Math.random() * c.getHeight());
				starPositions[i].width = 1;
				starPositions[i].height = 1;
				
				starMotions[i] = new Movable();
				starMotions[i].dx = 0f;
				starMotions[i].dy = (float) (Math.random() * 5f);
			}
		}
		
		/**
		 * Move the stars
		 */
		for( int i = 0; i < starPositions.length; i++ )
		{
			starPositions[i].x += starMotions[i].dx;
			starPositions[i].y += starMotions[i].dy;
		}
		
		/**
		 * Paint the stars
		 */
		Paint p = new Paint();
		for( int i = 0; i < starPositions.length; i++ )
		{
			int nearness = (int) ((255 * (2 + starMotions[i].dy)) / 8);
			p.setARGB( 255, nearness, nearness, nearness );
			
			c.drawCircle( starPositions[i].x, starPositions[i].y, starPositions[i].width, p );
		}
		
		/**
		 * Reset / create disappeared stars
		 */
		for( int i = 0; i < starPositions.length; i++ )
		{
			if( isOnscreen( starPositions[i] ) )
				;
			else
			{
				// starPositions[i] = new Position();
				starPositions[i].x = (float) (Math.random() * c.getWidth());
				starPositions[i].y = 0f;
				starPositions[i].width = 2;
				starPositions[i].height = 2;
			}
		}
	}
	
	protected boolean isOnscreen( Position p )
	{
		return isOnscreen( (int) p.x, (int) p.y, p.width, p.height );
	}
	
	protected boolean isOnscreen( int x, int y, int w, int h )
	{
		int screenWidth = surfaceView.getWidth();
		int screenHeight = surfaceView.getHeight();
		
		if( screenHeight < 1
		|| screenHeight < 1 )
			throw new IllegalArgumentException( "My surface is reporting a wacky width/height; cannot function");
		
		/**
		 * Check if *any part of* the thing is onscreen ...so give a leeway of width/2 (since x,y is the objects CENTER)
		 */
		if( x + w / 2 < 0 || x - w / 2 > screenWidth || y + h / 2 < 0 || y - h / 2 > screenHeight )
			return false;
		else
			return true;
	}
	
}