package org.tmachine.games.escapefromthepit;

import java.util.*;

import org.tmachine.games.escapefromthepit.Components.CTouch.TouchType;
import org.tmachine.games.escapefromthepit.Components.*;

import com.wikidot.entitysystems.rdbmsbeta.*;

import android.content.*;
import android.util.*;
import android.view.*;
import android.view.View.OnTouchListener;

public class TouchListenerPlayerMovement implements OnTouchListener
{
	SubsystemTouchHandler touchHandler;
	
	public TouchListenerPlayerMovement( SubsystemTouchHandler th )
	{
		touchHandler = th;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{	
		touchHandler.addAsynchronousTouchInput( new FixedMotionEvent(event) );
		
		//Log.i(getClass().getSimpleName(), "touch event "+ event.getActionMasked() +"... added to queue! (ref = "+event.hashCode());
		return true;
	}
}