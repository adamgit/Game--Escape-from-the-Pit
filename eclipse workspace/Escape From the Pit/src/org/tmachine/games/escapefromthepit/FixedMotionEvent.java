package org.tmachine.games.escapefromthepit;

import android.view.*;

/**
 * This is a workaround for a MASSIVE bug in Android, affecting all versions up to and including 2.2 (possibly up to 3.2 - unconfirmed)
 * 
 * The class "MotionEvent" is incorrectly implemented by Google, and a single MotionEvent object is re-used on every touch,
 * 
 * This makes it literally impossible to store touches, in an array, in a list, anywhere.
 * 
 * Instead, we have to re-create the class, and "not **** it up", unlike the Android API implementer.
 * 
 * NB: this behaviour is *not* documented in the API. If it were documented, it would be merely "stupid"; right now, it's a vicious bug.
 * 
 * @author adam
 *
 */
public class FixedMotionEvent
{
	float x, y;
	int touchType;
	
	public FixedMotionEvent( MotionEvent e)
	{
		x = e.getX();
		y = e.getY();
		touchType = e.getActionMasked();
	}
}