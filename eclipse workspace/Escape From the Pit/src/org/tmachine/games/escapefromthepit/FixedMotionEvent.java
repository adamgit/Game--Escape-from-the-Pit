package org.tmachine.games.escapefromthepit;

import android.view.*;

/**
 * This is a workaround for what appears to be a MASSIVE bug in Android, affecting all versions up to and including 2.2 (possibly up to 3.2 - unconfirmed)
 * 
 * NB: I haven't checked this in detail, I discovered it by trial-and-error late at night while working on the 24 hour time-limit;
 * take this with a pinch of salt - I was pretty unhappy when I discovered what was causing my bugs - an apparent stupid design
 * decision in the Android motion events :(.
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