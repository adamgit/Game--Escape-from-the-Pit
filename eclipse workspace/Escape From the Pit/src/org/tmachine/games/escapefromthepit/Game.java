package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.*;
import android.util.*;

public class Game
{
	public class MazeLocation
	{
			int x;
			int y;
			
			public MazeLocation( int x, int y)
			{
				this.x = x;
				this.y = y;
			}
		
	}
	
	public class PotentialTunnel
	{
		MazeLocation cell;
		int dx, dy;
		
		public PotentialTunnel( MazeLocation ml, int deltax, int deltay)
		{
			cell = ml;
			this.dx = deltax;
			this.dy = deltay;
		}
	}

	protected EntityManager em;
	
	public Game( EntityManager initialEm )
	{
		em = initialEm;
	}
	/*
	protected void resetShipPosition()
	{
		MetaEntity ship = em.getAllEntitiesPossessing( PlayerShip.class ).get( 0 );
		ship.get( Position.class ).y = (9 * surfaceView.getHeight()) / 10 - renderingSystem.imShip.getIntrinsicHeight() / 2;
		ship.get( Position.class ).x = surfaceView.getWidth() / 2;
	}
	*/
	
	protected List<MazeLocation> getAdjacentLocations( MazeLocation cell, int maxWidth, int maxHeight )
	{
		List<MazeLocation> result = new LinkedList<Game.MazeLocation>();
		
		if( cell.x > 0)
			result.add(new MazeLocation(cell.x-1, cell.y));
		if( cell.x < maxWidth )
			result.add(new MazeLocation(cell.x+1, cell.y));
		if( cell.y > 0 )
			result.add(new MazeLocation(cell.x, cell.y -1 ));
		if( cell.y < maxHeight )
			result.add(new MazeLocation(cell.x, cell.y+1));
		
		return result;
	}
	
	protected List<MazeLocation> getAdjacentWallsFromBooleanArray( MazeLocation cell, boolean[][] blockingArray )
	{
		List<MazeLocation> result = new LinkedList<Game.MazeLocation>();
		
		if( cell.x > 1 && blockingArray[ cell.x -1 ][ cell.y ] )
			result.add(new MazeLocation(cell.x-1, cell.y));
		if( cell.x < (blockingArray.length-2) && blockingArray[ cell.x + 1 ][ cell.y ] )
			result.add(new MazeLocation(cell.x+1, cell.y));
		if( cell.y > 1 && blockingArray[ cell.x ][ cell.y -1 ] )
			result.add(new MazeLocation(cell.x, cell.y -1 ));
		if( cell.y < (blockingArray[0].length-2) && blockingArray[ cell.x ][ cell.y + 1 ] )
			result.add(new MazeLocation(cell.x, cell.y+1));
		
		return result;
	}
	
	protected boolean isTrueAndInBounds( int x, int y, boolean[][] a )
	{
		if( x > -1
		&& y > -1
		&& x < a.length
		&& y < a[0].length
		&& a[x][y])
			return true;
		else
			return false;
	}
	
	protected boolean[][] generateMap( int w, int h)
	{
		if( w % 2 == 0 ) // algorithm only works correctly with odd width/height array 
			w -=1;
		if( h % 2 == 0 )
			h -=1;
		
		boolean[][] stones = new boolean[w][h];
		for( int k=0; k<stones[0].length; k++ )
			for( int i=0; i<stones.length; i++ )
			{
				stones[i][k] = true;
			}
		
		List<PotentialTunnel> wallsThatMightBeTunnelable = new LinkedList<Game.PotentialTunnel>();
		MazeLocation startingCell = new MazeLocation(1, 1);
		stones[startingCell.x][startingCell.y] = false;
		wallsThatMightBeTunnelable.add( new PotentialTunnel( new MazeLocation(startingCell.x+1, startingCell.y), 1, 0));
		wallsThatMightBeTunnelable.add( new PotentialTunnel( new MazeLocation(startingCell.x, startingCell.y+1), 0, 1));
		
		while( ! wallsThatMightBeTunnelable.isEmpty() )
		{
			PotentialTunnel wall = wallsThatMightBeTunnelable.remove( (int)(Math.random()*wallsThatMightBeTunnelable.size()));
			
			//Log.i("", wallsThatMightBeTunnelable.size()+" left; after taking: ("+wall.cell.x+","+wall.cell.y+") -> "+wall.dx+"/"+wall.dy );
			
			if( stones[ wall.cell.x + wall.dx][ wall.cell.y + wall.dy] // the far side of the wall is solid
			 && ( wall.dx == 0 || isTrueAndInBounds( wall.cell.x, wall.cell.y + 1, stones ) ) // either X isn't changing ... OR check in the y-directions
			 && ( wall.dx == 0 || isTrueAndInBounds( wall.cell.x, wall.cell.y - 1, stones ) )
			 && ( wall.dy == 0 || isTrueAndInBounds( wall.cell.x+1, wall.cell.y, stones ) )
			 && ( wall.dy == 0 || isTrueAndInBounds( wall.cell.x-1, wall.cell.y, stones ) )
			 )
			{
				//Log.i("", " TRUE: ("+(wall.cell.x + wall.dx) +","+ (wall.cell.y + wall.dy) +")" );
				
				stones[ wall.cell.x ][ wall.cell.y ] = false;
				stones[ wall.cell.x + wall.dx ][ wall.cell.y + wall.dy ] = false;
				
				MazeLocation newSpace = new MazeLocation( wall.cell.x + wall.dx,  wall.cell.y + wall.dy );
				
				if( newSpace.x + 2 < stones.length && stones[ newSpace.x + 1 ][ newSpace.y ] )
				{
					PotentialTunnel newTunnel = new PotentialTunnel( new MazeLocation(newSpace.x+1, newSpace.y), 1, 0 );
					wallsThatMightBeTunnelable.add( newTunnel );
					//Log.i("", "    added: +1/0 ("+(newTunnel.cell.x + newTunnel.dx) +","+(newTunnel.cell.y + newTunnel.dy) +")" );
				}
				if( newSpace.y + 2 < stones[0].length && stones[ newSpace.x ][ newSpace.y + 1 ] )
				{
					PotentialTunnel newTunnel = new PotentialTunnel( new MazeLocation(newSpace.x, newSpace.y+1), 0, 1 );
					wallsThatMightBeTunnelable.add( newTunnel );
					//Log.i("", "    added: 0/+1 ("+newTunnel.cell.x + newTunnel.dx +","+newTunnel.cell.y + newTunnel.dy +")" );
				}
				if( newSpace.x - 2 > -1 && stones[ newSpace.x - 1 ][ newSpace.y ] )
				{
					PotentialTunnel newTunnel = new PotentialTunnel( new MazeLocation(newSpace.x-1, newSpace.y), -1, 0 );
					wallsThatMightBeTunnelable.add( newTunnel );
					//Log.i("", "    added: -1/0 ("+newTunnel.cell.x + newTunnel.dx +","+newTunnel.cell.y + newTunnel.dy +")" );
				}
				if( newSpace.y - 2 > -1 && stones[ newSpace.x ][ newSpace.y - 1 ] )
				{
					PotentialTunnel newTunnel = new PotentialTunnel( new MazeLocation(newSpace.x, newSpace.y-1), 0, -1 );
					wallsThatMightBeTunnelable.add( newTunnel );
					//Log.i("", "    added: 0/-1 ("+newTunnel.cell.x + newTunnel.dx +","+newTunnel.cell.y + newTunnel.dy +")" );
				}
			}
			/*else
				Log.i("", "(false)" );*/
			
		}
		
		return stones;
	}
	
	protected void preSetupGame()
	{
		boolean[][] stones = generateMap(10, 9);
		for( int k=0; k<stones[0].length; k++ )
			for( int i=0; i<stones.length; i++ )
			{
				if( stones[i][k] )
					new MetaEntity( new CPosition(50 + 50*i, 50 + 50*k, 50, 50), new CAndroidDrawable( R.drawable.rock2));
			}
		
		new MetaEntity( "player", new CPlayer(), new CPosition( 300, 300, 100, 100 ), new CAndroidDrawable( R.drawable.personleft ) );
		
		/*
		Position p1 = new Position();
		StraightShooter sh1 = new StraightShooter();
		StraightShooter sh2 = new StraightShooter();
		Destroyable des = new Destroyable();
		
		MetaEntity ship = getShip();
		
		if( null == ship )
		{
			Log.i( getClass().getSimpleName(), "Creating a new ship inside the ES; none already existed; ES = " + MetaEntity.defaultEntitySystem );
			ship = new MetaEntity( "PlayerShip", new PlayerShip(), p1, new Movable(), sh1, sh2, des );
		}
		
		p1.width = renderingSystem.imShip.getIntrinsicWidth();
		p1.height = renderingSystem.imShip.getIntrinsicHeight();
		
		sh1.fireProbability = 1.0f;
		sh1.fireRateMillis = 1000;
		sh1.shotDamage = 1;
		sh1.shotDx = 0;
		sh1.shotDy = -10f;
		sh1.shotRelativeOriginX = -18f;
		sh1.shotRelativeOriginY = -5f;
		sh1.alliedWithPlayer = true;
		
		sh2.fireProbability = 1.0f;
		sh2.fireRateMillis = 1000;
		sh2.shotDamage = 1;
		sh2.shotDx = 0;
		sh2.shotDy = -10f;
		sh2.shotRelativeOriginX = 18f;
		sh2.shotRelativeOriginY = -5f;
		sh2.alliedWithPlayer = true;
		
		des.totalHealth = des.remainingHealth = 5;
		*/
	}
	
	protected void fixPositionsAfterPhoneRotated( int w, int h )
	{
		Log.i( getClass().getSimpleName(), "Fixing up positions after a screen-rotate; new width = " + w + ", new height = " + h );
		
		Set<UUID> allMovables = em.getAllEntitiesPossessingComponent( CPosition.class );
		
		/**
		 * 
		 */
		if( false) 
			for( UUID entity : allMovables )
		{
			MetaEntity e = MetaEntity.loadFromEntityManager(entity);
			
			CPosition pos = e.get( CPosition.class );
			
			if( pos.x - pos.width / 2 < 0 )
				pos.x = pos.width / 2;
			if( pos.x + pos.width / 2 > w )
				pos.x = w - pos.width / 2;
			
			if( pos.y - pos.height / 2 < 0 )
				pos.y = pos.height / 2;
			if( pos.y + pos.height / 2 > h )
				pos.y = h - pos.height / 2;
		}
	}
}