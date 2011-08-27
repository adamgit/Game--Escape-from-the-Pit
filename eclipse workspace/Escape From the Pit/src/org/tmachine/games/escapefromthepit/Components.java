package org.tmachine.games.escapefromthepit;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class Components
{
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
	
	public static class CAndroidDrawable implements Component
	{
		int resourceID;
		
		public CAndroidDrawable()
		{
			// TODO Auto-generated constructor stub
		}
		
		public CAndroidDrawable( int rid )
		{
			resourceID = rid;
		}
	}
	
	static class Position implements Component
	{
		float x, y;
		int width, height;
		float rotationDegrees;
		
		public Position()
		{
			// TODO Auto-generated constructor stub
		}
		
		public Position( float x, float y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		@Override public String toString()
		{
			return "("+super.toString()+" @ ("+x+","+y+") * rot."+rotationDegrees+")";
		}
	}
	
	static class Movable implements Component
	{
		float dx, dy;
		/** How much to rotate per second (will be multipled by frametime/1000) */
		float dRotationDegrees;
		
		@Override public String toString()
		{
			return "("+super.toString()+" delta:"+dx+","+dy+" * rot."+dRotationDegrees+")";
		}
	}
}