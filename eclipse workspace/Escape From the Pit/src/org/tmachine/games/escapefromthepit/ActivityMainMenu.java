package org.tmachine.games.escapefromthepit;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.app.*;
import android.content.*;
import android.util.*;

public class ActivityMainMenu extends BetterActivity
{
	@Override
	protected void handleActivityCreatedFirstTime()
	{
		Intent i = new Intent( this, EscapeFromthePitActivity.class);
		startActivityForResult( i, 0 );
	}
	
	@Override
	protected void handleActivityNew()
	{
	}
	
	@Override
	protected void handleActivityReturnedToScreen()
	{
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		setContentView(R.layout.main);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( data != null )
		{
			EntityManager em = (EntityManager)data.getSerializableExtra( "org.tmachine.games.escapefromthepit.entitymanager" );
			boolean didPlayerEscape = data.getBooleanExtra("org.tmachine.games.escapefromthepit.playerescaped", false );
		}
		else
			Log.e(getClass().getSimpleName(), "Returned to this activity, but no result found; I have no idea how you got here?");
	}
}