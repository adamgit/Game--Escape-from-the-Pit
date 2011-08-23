package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.*;
import android.util.*;

public class Game
{
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
	
	protected void preSetupGame()
	{
		boolean[][] stones = new boolean[7][7];
		for( int k=0; k<stones[0].length; k++ )
			for( int i=0; i<stones.length; i++ )
			{
				if( i==0 || k==0 || i==stones.length-1 || k==stones[0].length-1 )
					stones[i][k] = true;
			}
		
		stones[2][1] = stones[2][2] = stones[2][3] = stones[2][4] = true;
		stones[4][5] = stones[4][4] = stones[4][3] = stones[4][2] = true;
		
		for( int k=0; k<stones[0].length; k++ )
			for( int i=0; i<stones.length; i++ )
			{
				if( stones[i][k] )
					new MetaEntity( new Position(100 + 100*i, 100 + 100*k, 100, 100), new CAndroidDrawable( R.drawable.rock2));
			}
		
		new MetaEntity( new Position( 600, 600, 100, 100), new CAndroidDrawable( R.drawable.rock2));
		
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
		
		Set<UUID> allMovables = em.getAllEntitiesPossessingComponent( Position.class );
		
		/**
		 * 
		 */
		if( false) 
			for( UUID entity : allMovables )
		{
			MetaEntity e = MetaEntity.loadFromEntityManager(entity);
			
			Position pos = e.get( Position.class );
			
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