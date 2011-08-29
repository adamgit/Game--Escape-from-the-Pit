package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.CAndroidDrawable;
import org.tmachine.games.escapefromthepit.Components.CDrawableRectangle;
import org.tmachine.games.escapefromthepit.Components.CMovable;
import org.tmachine.games.escapefromthepit.Components.CPosition;

import android.*;
import android.R.*;
import android.content.res.Resources.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class RenderSystemSimpleDrawable implements SubSystem
{

	protected EntityManager entitySystem;
	protected Canvas canvas;
	protected SurfaceView surfaceView;
	
	protected Drawable imShip, imEvilCube1;
	protected HashMap<Integer, Drawable> drawablesCache;
	
	Game game;
	protected float canvasTranslationX = 0;
	protected float canvasTranslationY = 0;
	
	public RenderSystemSimpleDrawable( EntityManager em, SurfaceView sv, Game g )
	{
		entitySystem = em;
		surfaceView = sv;
		game = g;
	
		/**
		 * This is terrible app design, but it seems that Android forces us to do it;
		 * I can't see any other obvious way of doing this sensibly without writing
		 * lots and lots of boilerplate code 
		 */
		/*drawablesCache = new HashMap<Integer, Drawable>();
		/*encacheDrawable( R.drawable.rock2 );
		encacheDrawable( R.drawable.arrowdot );
		encacheDrawable( R.drawable.arrowleft );
		encacheDrawable( R.drawable.arrowup );
		encacheDrawable( R.drawable.arrowright );
		encacheDrawable( R.drawable.arrowdown );*/
	}
	
	protected void encacheDrawable( int id )
	{
		try
		{
		drawablesCache.put( id, surfaceView.getContext().getResources().getDrawable( id ) );
		}
		catch (NotFoundException e)
		{
			Log.e(getClass().getName(), "Asked to encache a non-existent drawable with id = "+id, e );
			//for( surfaceView.getContext().getResources() )
			Log.i( getClass().getName(), "Current drawables:");
		}
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
		Set<UUID> allDrawables = entitySystem.getAllEntitiesPossessingComponent( CAndroidDrawable.class ); // if you get ConcurrentModificationException's, you're doing it wrong ... should be SINGLE THREADED access to EntityManager!
		Set<UUID> allColouredRectangles = entitySystem.getAllEntitiesPossessingComponent( CDrawableRectangle.class ); // if you get ConcurrentModificationException's, you're doing it wrong ... should be SINGLE THREADED access to EntityManager!
		
		//DEBUG: Log.i(getClass().getName(), "Found "+allDrawables.size()+" CAndroidDrawables to render");
		
		canvas.translate( canvasTranslationX, canvasTranslationY );
		
		Paint paint = new Paint();
		for( UUID entityID : allColouredRectangles )
		{
			CPosition pos = entitySystem.getComponent(entityID, CPosition.class );
			
			if( pos.rotationDegrees != 0.0f )
			{
			canvas.save();
			canvas.rotate( pos.rotationDegrees, pos.x, pos.y );
			}
			
			paint.setColor( entitySystem.getComponent(entityID, CDrawableRectangle.class).androidColour );
			positionAndDrawRect( pos, paint );
			
			if( pos.rotationDegrees != 0 )
				canvas.restore();
		}
		
		paint.setARGB( 255, 127, 0, 0 );
		for( UUID entityID : allDrawables )
		{
			CPosition pos = entitySystem.getComponent(entityID, CPosition.class );
			
			Drawable androidDrawable = null;
			
			//androidDrawable = drawablesCache.get( entitySystem.getComponent(entityID, CAndroidDrawable.class ).resourceID );
			try
			{
				//androidDrawable = surfaceView.getContext().getResources().getDrawable( entitySystem.getComponent(entityID, CAndroidDrawable.class ).resourceID );
				androidDrawable = entitySystem.getComponent(entityID, CAndroidDrawable.class ).resource;
				if( androidDrawable == null )
					androidDrawable = entitySystem.getComponent(entityID, CAndroidDrawable.class ).resource = surfaceView.getContext().getResources().getDrawable( entitySystem.getComponent(entityID, CAndroidDrawable.class ).resourceID ); 
					
			}
			catch( NotFoundException e )
			{
				Log.e(getClass().getName(), "Failed to find the Drawable resource (id = "+entitySystem.getComponent(entityID, CAndroidDrawable.class ).resourceID+") for entity: "+MetaEntity.loadFromEntityManager(entityID), e);
				continue;
			}
			
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
	
	public void shiftCanvasToKeepPositionOnScreen( CPosition pos )
	{
		if( canvasTranslationX + pos.x < (2*game.mazeCellWidth) )
		{
			canvasTranslationX = (2*game.mazeCellWidth) - pos.x;
		}
		else if( canvasTranslationX + pos.x > (canvas.getWidth()-(2*game.mazeCellWidth)) )
		{
			canvasTranslationX = -1 * (pos.x - (canvas.getWidth()-(2*game.mazeCellWidth)));
		}
		
		if( canvasTranslationY + pos.y < (2*game.mazeCellHeight) )
		{
			canvasTranslationY = (2*game.mazeCellHeight) - pos.y;
		}
		else if( canvasTranslationY + pos.y > (canvas.getHeight()-(2*game.mazeCellHeight)) )
		{
			canvasTranslationY = -1 * (pos.y - (canvas.getHeight()-(2*game.mazeCellHeight)));
		}
	}
	
	/**
	 * Utility method to make up for missing method in the Android library, and merge with Position class
	 * 
	 * @param p Position of the thing you want to draw
	 * @param d The sprite/rectangle/ellipse/whatever of the thing you want to draw
	 */
	protected void positionAndDraw( CPosition p, Drawable d )
	{
		int w = p.width < 1 ? d.getIntrinsicWidth() : p.width;
		int h = p.height < 1 ? d.getIntrinsicHeight() : p.height;
		
		d.setBounds( (int) p.x - w / 2, (int) p.y - h / 2, (int) p.x + w / 2, (int) p.y + h / 2 );
		d.draw( canvas );
	}
	
	protected void positionAndDrawRect( CPosition p, Paint paint )
	{
		canvas.drawRect( new RectF( p.x - p.width / 2, p.y - p.height / 2, p.x + p.width / 2, p.y + p.height / 2 ), paint );
	}
	
	
	CPosition[] starPositions;
	CMovable[] starMotions;
	
	protected void paintStarfield( Canvas c )
	{
		if( starPositions == null )
		{
			int MAX_STARS = 60;
			starPositions = new CPosition[MAX_STARS];
			starMotions = new CMovable[MAX_STARS];
			
			for( int i = 0; i < starPositions.length; i++ )
			{
				starPositions[i] = new CPosition();
				starPositions[i].x = (float) (Math.random() * c.getWidth());
				starPositions[i].y = (float) (Math.random() * c.getHeight());
				starPositions[i].width = 1;
				starPositions[i].height = 1;
				
				starMotions[i] = new CMovable();
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
		
		p.setARGB(255, 255, 128, 128);
		p.setTextSize(20);
		c.drawText("Default Background (stars)", 100, 100, p);
	}
	
	protected boolean isOnscreen( CPosition p )
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