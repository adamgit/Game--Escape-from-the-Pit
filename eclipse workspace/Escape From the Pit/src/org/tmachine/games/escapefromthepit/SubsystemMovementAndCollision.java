package org.tmachine.games.escapefromthepit;

import java.util.*;

import static org.tmachine.games.escapefromthepit.Components.*;

import android.graphics.*;
import android.util.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class SubsystemMovementAndCollision implements SubSystem
{
	public static final boolean debug = false;
	
	EntityManager em;
	RenderSystemSimpleDrawable renderingSystem;
	
	public SubsystemMovementAndCollision( EntityManager em, RenderSystemSimpleDrawable rs )
	{
		this.em = em;
		renderingSystem = rs;
	}
	
	public void processOneGameTick(long lastFrameTime)
	{
		Set<UUID> allMovables = em.getAllEntitiesPossessingComponent( CMovable.class ); // if you get ConcurrentModificationException's, you're doing it wrong ... should be SINGLE THREADED access to EntityManager!
		Set<UUID> allCollidableItems = em.getAllEntitiesPossessingComponent( CCollidable.class );
		//Set<UUID> allPositionedItems = em.getAllEntitiesPossessingComponent( CPosition.class );
		
		//DEBUG: Log.i(getClass().getName(), "Found "+allDrawables.size()+" CAndroidDrawables to render");
		
		for( UUID entityID : allMovables )
		{
			CPosition pos = em.getComponent(entityID, CPosition.class );
			CMovable mov = em.getComponent(entityID, CMovable.class );
			CCollidable thisCollider = em.getComponent(entityID, CCollidable.class );
			
			RectF sweep = sweptRectFor(pos, mov);
			//DEBUG: Log.i( getClass().getSimpleName(), "Sweep = "+sweep+" (pos = "+pos+", mov = "+mov+")");
			//DEBUG: Log.i( getClass().getSimpleName(), "Sweep = "+sweep );
			
			CMovable movXonly = new CMovable( mov.dx, 0);
			CMovable movYonly = new CMovable( 0, mov.dy);
			RectF sweepXonly = sweptRectFor(pos, movXonly );
			RectF sweepYonly = sweptRectFor(pos, movYonly );
			
			boolean blocked = false;
			int numCollisions = 0;
			for( UUID otherEntity : allCollidableItems )
			//for( UUID otherEntity : allPositionedItems )
			{
				CPosition otherPos = em.getComponent(otherEntity, CPosition.class );
				CCollidable otherCollidable = em.getComponent(otherEntity, CCollidable.class );
				
				//CMovable otherMov = em.getComponent(otherEntity, CMovable.class );
				
				if( otherEntity == entityID )
					continue;
				
				if( isBlockedBy( thisCollider, otherCollidable) )
				
				if( sweep.intersects(otherPos.x, otherPos.y, otherPos.x + otherPos.width, otherPos.y + otherPos.height) )
				{
					if( debug ) Log.i(getClass().getSimpleName(), ">>> Potential Collision for sweep = "+sweep+" (move = "+mov.dx+","+mov.dy+")" );
					
					if( mov.preferXMovesToYMoves )
					{
						/** attempt to block the YYYY move to allow the XXXX move */
					if( ! sweepXonly.intersects(otherPos.x, otherPos.y, otherPos.x + otherPos.width, otherPos.y + otherPos.height) )
					{
						mov.dy = 0;
						
						sweep = sweptRectFor(pos, mov);
						numCollisions++;
						
						if( debug ) Log.i(getClass().getSimpleName(), "...Block avoided by zeroing the Y motion with other = "+otherPos );
						continue;
					}
					/** attempt to block the XXXX move to allow the YYYY move */
					if( ! sweepYonly.intersects(otherPos.x, otherPos.y, otherPos.x + otherPos.width, otherPos.y + otherPos.height) )
					{
						mov.dx = 0;
						
						sweep = sweptRectFor(pos, mov);
						numCollisions++;
						
						if( debug ) Log.i(getClass().getSimpleName(), "...Block avoided by zeroing the X motion with other = "+otherPos );
						continue;
					}
					}
					else
					{
						/** attempt to block the XXXX move to allow the YYYY move */
						if( ! sweepYonly.intersects(otherPos.x, otherPos.y, otherPos.x + otherPos.width, otherPos.y + otherPos.height) )
						{
							mov.dx = 0;
							
							sweep = sweptRectFor(pos, mov);
							numCollisions++;
							
							if( debug ) Log.i(getClass().getSimpleName(), "...Block avoided by zeroing the X motion with other = "+otherPos );
							continue;
						}
						/** attempt to block the YYYY move to allow the XXXX move */
						if( ! sweepXonly.intersects(otherPos.x, otherPos.y, otherPos.x + otherPos.width, otherPos.y + otherPos.height) )
						{
							mov.dy = 0;
							
							sweep = sweptRectFor(pos, mov);
							numCollisions++;
							
							if( debug ) Log.i(getClass().getSimpleName(), "...Block avoided by zeroing the Y motion with other = "+otherPos );
							continue;
						}
					}
					
					/**
					 * Otherwise ... completely blocked
					 */
					mov.dx = 0;
					mov.dy = 0;
					
					if( debug ) Log.i(getClass().getSimpleName(), "...Block COULD NOT be avoided with other @ "+otherPos.x+", "+otherPos.y );
					blocked = true;
					
					break;
				}
			}
			
			//if( ! collided )
			{
				//DEBUG: 
				if( debug ) Log.i(getClass().getSimpleName(), "Moving from "+pos.x+","+pos.y+" ... dx = "+mov.dx+" dy = "+mov.dy+" ... BLOCKED? "+blocked+" ... Collisions? "+numCollisions);
				
				pos.x += mov.dx;
				mov.dx = 0;
				
				pos.y += mov.dy;
				mov.dy = 0;
				
				if( em.hasComponent(entityID, CPlayer.class ) )
				{
					renderingSystem.shiftCanvasToKeepPositionOnScreen( pos );
				}
			}
		}
	}
	
	protected RectF sweptRectFor( CPosition p, CMovable m )
	{
		float left = m.dx > 0 ? p.x : p.x + m.dx;
		float right = p.width + (m.dx > 0 ? p.x + m.dx : p.x);
		float top = m.dy > 0 ? p.y : p.y + m.dy;
		float bottom = p.height + (m.dy > 0 ? p.y + m.dy : p.y);
		
		return new RectF(left, top, right, bottom);
	}

	protected boolean isBlockedBy( CCollidable mover, CCollidable receiver )
	{
		switch( mover.type )
		{
			case PLAYER:
				switch( receiver.type )
				{
					case STONE_WALL:
						return true;
						
					default:
						return true;
				}
				
			case GHOST:
				return false;
			
			default:
				return true;
		}
	}
}