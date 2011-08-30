package org.tmachine.games.escapefromthepit;

import android.graphics.*;
import android.graphics.drawable.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class Components
{
	public static class CPlayer implements Component
	{
		
	}
	
	public static class CGhost implements Component
	{
		float lastDx;
		float lastDy;
	}
	
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
	
	public static class CDrawableRectangle implements Component
	{
		public int androidColour;
		
		public CDrawableRectangle( int c )
		{
			this.androidColour = c;
		}
	}
	
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