package org.tmachine.games.escapefromthepit;

import org.tmachine.games.escapefromthepit.Components.*;

import android.app.*;
import android.content.*;
import android.util.*;

import com.wikidot.entitysystems.rdbmsbeta.*;
import com.wikidot.entitysystems.rdbmsbeta.examplecomponents.*;

public class SubsystemTriggers implements SubSystem
{
	EscapeFromthePitActivity activityForGame;
	EntityManager em;
	Game game;
	
	public SubsystemTriggers( EscapeFromthePitActivity activity, EntityManager em, Game g )
	{
		activityForGame = activity;
		this.em = em;
		game = g;
	}
	
	@Override
	public void processOneGameTick(long lastFrameTime)
	{
		MetaEntity player = MetaEntity.loadFromEntityManager( em.getAllEntitiesPossessingComponent(CPlayer.class).iterator().next() );
		
		if( player.get(CPosition.class).y < 0 - game.heightOfMazeInCells )
		{
			/**
			 * Player has escaped the maze
			 */
			
			Intent i = activityForGame.getIntent();
			i.putExtra( "org.tmachine.games.escapefromthepit.entitymanager", em );
			i.putExtra( "org.tmachine.games.escapefromthepit.playerescaped", true );
			activityForGame.setResult( Activity.RESULT_OK, i );
			activityForGame.finish();
		}
		
	}
}