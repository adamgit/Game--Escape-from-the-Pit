package org.tmachine.games.escapefromthepit;

import android.graphics.*;
import android.graphics.drawable.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

/**
 * I placed all the components here because I wanted it to be trivial amount of typing
 * to access them within the game - I static-import everything in this class, and then
 * I don't have to worry about any other class imports.
 * 
 * Also, it makes maintenance MUCH easier having a single file with all your component
 * definitions side-by-side. Obviously this doesn't scale for bigger projects!
 * 
 * @author adam
 *
 */
public class Components
{
	/**
	 * I was lazy: this is my quick and dirty way to grab the player reference
	 * 
	 * @author adam
	 *
	 */
	public static class CPlayer implements Component
	{
		
	}
	
	/**
	 * Scrum / YAGNI: ghosts were the only object that needed a "last position"
	 * so I put that into their component
	 * 
	 * @author adam
	 *
	 */
	public static class CGhost implements Component
	{
		float lastDx;
		float lastDy;
	}
	
	/**
	 * The maze algorithm simulates "tunnelling" to generate a guaranteed-escapable maze
	 * 
	 * @author adam
	 *
	 */
	public static class CTunnelCell implements Component
	{
		float lightingLevel;
		
		public CTunnelCell( float l )
		{
			if( l > 1.0f || l < 0.0f )
				throw new IllegalArgumentException("Lighting level argument must be between 0.0 and 1.0 inclusive; you provided: "+l );
			
			this.lightingLevel = l;
		}
	}
	
	/**
	 * Very basic collision component: anything that has this
	 * is included in the collision-detection sweep every frame
	 * 
	 * @author adam
	 *
	 */
	public static class CCollidable implements Component
	{
		public enum CollisionType
		{
			PLAYER,
			STONE_WALL,
			GHOST
		}
		
		public CollisionType type;
		
		public CCollidable( CollisionType t )
		{
			type = t;
		}
	}
	
	/**
	 * The user's input
	 * 
	 * @author adam
	 *
	 */
	public static class CTouch implements Component
	{
		public enum TouchType
		{
			NONE,
			LEFT,
			RIGHT,
			UP,
			DOWN
		};
		
		public TouchType value;
		
		public CTouch( TouchType t )
		{
			value = t;
		}
	}
	
	/**
	 * The most basic renderable item: a coloured rectangle! (very useful to build up
	 * early prototypes of new features, and for debugging)
	 * 
	 * @author adam
	 *
	 */
	public static class CDrawableRectangle implements Component
	{
		public int androidColour;
		
		public CDrawableRectangle( int c )
		{
			this.androidColour = c;
		}
	}
	
	/**
	 * The second-most-basic renderable item: an image!
	 * 
	 * @author adam
	 *
	 */
	public static class CAndroidDrawable implements Component
	{
		int resourceID;
		transient Drawable resource;
		boolean ignoresCanvasTranslation;
		
		public CAndroidDrawable()
		{
			// TODO Auto-generated constructor stub
		}
		
		public CAndroidDrawable( int rid )
		{
			resourceID = rid;
		}
		
		public CAndroidDrawable( int rid, boolean ignoresCT )
		{
			this( rid );
			ignoresCanvasTranslation = ignoresCT;
		}
		
		public CAndroidDrawable( Drawable d )
		{
			resource = d;
		}
	}
	
	/**
	 * Present in almost every app: an on-screen position (And size, and rotation)
	 * 
	 * @author adam
	 *
	 */
	static class CPosition implements Component
	{
		float x, y;
		int width, height;
		float rotationDegrees;
		
		public CPosition()
		{
			// TODO Auto-generated constructor stub
		}
		
		public CPosition( float x, float y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		@Override public String toString()
		{
			if( rotationDegrees != 0.0 )
				return "("+super.toString()+" @ ("+x+","+y+") * rot."+rotationDegrees+")";
			else
				return "("+super.toString()+" @ ("+x+","+y+"))";
		}
	}
	
	/**
	 * Anything with this will get moved by the collision detection system
	 * each frame, using swept-volumes to test for collision
	 * 
	 * @author adam
	 *
	 */
	static class CMovable implements Component
	{
		float dx, dy;
		/** How much to rotate per second (will be multipled by frametime/1000) */
		float dRotationDegrees;
		/** for player-controlled objects, is the player leaning more towards the x-chang or the y-change */
		boolean preferXMovesToYMoves;
		
		public CMovable()
		{
			preferXMovesToYMoves = false;
		}
		
		public CMovable( float dx, float dy )
		{
			preferXMovesToYMoves = false;
			this.dx = dx;
			this.dy = dy;
		}
		
		@Override public String toString()
		{
			if( dRotationDegrees != 0.0 )
				return "("+super.toString()+" delta:"+dx+","+dy+" * rot."+dRotationDegrees+")";
			else
				return "("+super.toString()+" delta:"+dx+","+dy+")";
		}
	}
}