package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;

import android.graphics.*;

import com.wikidot.entitysystems.rdbmsbeta.*;
import com.wikidot.entitysystems.rdbmsbeta.examplecomponents.*;

/**
 * Light the tunnels based on distance from the player
 * 
 * @author adam
 *
 */
public class SubsystemLighting implements SubSystem
{
	EntityManager em;
	Game game;
	double lightingDistanceFor100PercentFalloff;
	
	public SubsystemLighting( EntityManager em, Game g )
	{
		this.em = em;
		this.game = g;
	}
	
	public void processOneGameTick(long lastFrameTime)
	{
		Set<UUID> allTunnelCells = em.getAllEntitiesPossessingComponent( CTunnelCell.class ); // if you get ConcurrentModificationException's, you're doing it wrong ... should be SINGLE THREADED access to EntityManager!
		
		UUID playerEntity = em.getAllEntitiesPossessingComponent(CPlayer.class).iterator().next();
		CPosition playerPos = em.getComponent(playerEntity, CPosition.class );
		
		lightingDistanceFor100PercentFalloff = 2 * game.mazeCellHeight;
		
		for( UUID entityID : allTunnelCells )
		{
			CPosition pos = em.getComponent(entityID, CPosition.class );
			CTunnelCell tunnel = em.getComponent(entityID, CTunnelCell.class );
			CDrawableRectangle drawableRect = em.getComponent(entityID, CDrawableRectangle.class );
			
			double distanceFromPlayer = Math.sqrt( (pos.x - playerPos.x)*(pos.x - playerPos.x)
					+ (pos.y - playerPos.y)*(pos.y - playerPos.y) );
			
			double newLightingLevel = 1.0 - ( Math.min( lightingDistanceFor100PercentFalloff, distanceFromPlayer) / lightingDistanceFor100PercentFalloff );
			
			if( newLightingLevel > tunnel.lightingLevel )
			{
				tunnel.lightingLevel = (float) newLightingLevel;
				drawableRect.androidColour = Color.HSVToColor( new float[] { 0f, 0f, 0.25f + tunnel.lightingLevel/2.0f } );
			}
		}
	}
}