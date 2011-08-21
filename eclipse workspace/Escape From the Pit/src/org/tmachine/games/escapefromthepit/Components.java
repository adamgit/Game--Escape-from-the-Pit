package org.tmachine.games.escapefromthepit;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class Components
{
	public static class CAndroidDrawable implements Component
	{
		int resourceID;
	}
	
	static class Position implements Component
	{
		float x, y;
		int width, height;
		float rotationDegrees;
		
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