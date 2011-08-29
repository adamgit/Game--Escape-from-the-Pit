package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

public class SubsystemGhosts implements SubSystem
{
	EntityManager em;
	Game game;
	
	public SubsystemGhosts( EntityManager e, Game g )
	{
		em = e;
		game = g;
	}
	
	@Override
	public void processOneGameTick(long lastFrameTime)
	{
		Set<UUID> allGhosts = em.getAllEntitiesPossessingComponent( CGhost.class );
		
		for( UUID ghost : allGhosts )
		{
			CPosition pos = em.getComponent(ghost, CPosition.class );
			CMovable mov = em.getComponent(ghost, CMovable.class );
			CAndroidDrawable drawable = em.getComponent(ghost, CAndroidDrawable.class );
			CGhost gho = em.getComponent(ghost, CGhost.class );
			
			if( 
					(gho.lastDx == 0f && gho.lastDy == 0f )
			|| (
					((pos.x % game.mazeCellWidth) == 0)
					&& ((pos.y % game.mazeCellHeight)==0)
					&& 0.1f > Math.random()
				)
			)
			{
				if( 0.5f > Math.random() )
					if( 0.5f > Math.random() )
						mov.dx = 10;
					else
						mov.dx = -10;
				else
					if( 0.5f > Math.random() )
						mov.dy = 10;
					else
						mov.dy = -10;
			}
			else
			{
				mov.dx = gho.lastDx;
				mov.dy = gho.lastDy;
			}
			
			if( pos.x < 1 && mov.dx < 0 )
				mov.dx = 10;
			if( pos.y < 1 && mov.dy < 0 )
				mov.dy = 10;
			if( pos.x >= game.mazeCellWidth*game.widthOfMazeInCells && mov.dx > 0 )
				mov.dx = -10;
			if( pos.y >= game.mazeCellHeight*game.heightOfMazeInCells && mov.dy > 0 )
				mov.dy = -10;
			
			int oldDrawableID = drawable.resourceID;
			if( mov.dx > 0 )
				drawable.resourceID = R.drawable.ghostright;
			if( mov.dx < 0 )
				drawable.resourceID = R.drawable.ghostleft;
			if( drawable.resourceID != oldDrawableID )
				drawable.resource = null; // causes it to be refetched + recached
			
			gho.lastDx = mov.dx;
			gho.lastDy = mov.dy;
		}
	}

}
