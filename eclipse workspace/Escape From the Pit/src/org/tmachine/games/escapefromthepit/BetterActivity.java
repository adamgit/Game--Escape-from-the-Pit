package org.tmachine.games.escapefromthepit;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;

public abstract class BetterActivity extends Activity
{
	protected void killThreadBlockUntilDead(Thread t)
	{
		/**
		 * Code taken verbatim from Android official example source code; we're
		 * trying to simulate a thread.stop, since Android completely ignores
		 * that call, hence making app-data invalid, and ultimately crashing
		 * apps Bastards.
		 */
		boolean retry = true;
		while (retry)
		{
			try
			{
				t.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	protected Object handleAutoRotateSaveState()
	{
		return "Placeholder Object to indicate that an auto-rotate is happening";
	}

	protected void handleAutoRotateReloadState(Object o)
	{
	}

	protected void handleAutoRotateFinished()
	{
	}

	/**
	 * By default, this just calls {@link #handleActivityNew()}, and causes the
	 * entire Activity to be re-built from scratch; if you want to optimize this
	 * process, and use data saved during {@link #onSaveInstanceState(Bundle)},
	 * override this method and do so here.
	 */
	protected void handleActivityNewRestoreLocalVariables()
	{
		handleActivityNew();
	}

	protected abstract void handleActivityNew();

	/**
	 * For applications, but NOT general activities, cf
	 * markApplicationAsHavingCompletedFirstRun()
	 * 
	 * @see BetterApplication#markApplicationAsHavingCompletedFirstRun()
	 */
	protected abstract void handleActivityCreatedFirstTime();

	protected abstract void handleActivityReturnedToScreen();

	@Override
	protected void onStop()
	{
		super.onStop();

		Log.i(getClass().getSimpleName(), "STOPPING: "
				+ getClass().getSimpleName());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Log.i(getClass().getSimpleName(), "DESTROYING: "
				+ getClass().getSimpleName());
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();

		Log.i(getClass().getSimpleName(), "RESTARTING: "
				+ getClass().getSimpleName());

		handleActivityReturnedToScreen();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		Log.i(getClass().getSimpleName(), "SAVING INSTANCE STATE");

		outState.putString("test",
				"testing this freaky piece of shit from google");
	}

	@Override
	public Object onRetainNonConfigurationInstance()
	{
		Log.i(getClass().getSimpleName(),
				"onRetainNonConfigurationInstance: (time to do clever stuff for auto-rotate) ");

		Object result = handleAutoRotateSaveState();

		if (result == null)
			return "Placeholder value for onRetainNonConfigurationInstance() so that we can deduce an auto-rotate happened (Android sucks)";
		else
			return result;
	}

	/**
	 * Called in LOTS of places when the activity is NOT created - Android
	 * architecture design is appalling with lifecycle management.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.i(getClass().getSimpleName(), "CREATING: "
				+ getClass().getSimpleName());

		Log.i(getClass().getSimpleName(),
				"onCreate started; savedInstanceState? ("
						+ (null == savedInstanceState ? "null"
								: savedInstanceState.size())
						+ " entries); lastNonConfInst? (class: "
						+ (null == getLastNonConfigurationInstance() ? "null"
								: getLastNonConfigurationInstance().getClass())
						+ ")");

		/**
		 * Obtain shared prefs
		 */
		SharedPreferences prefs = getSharedPreferences("prefs", 0);
		boolean hasRunBefore = prefs.getBoolean("appHasRunAtLeastOnce", false);
		Log.i(getClass().getSimpleName(), "App has run before? = "
				+ hasRunBefore);

		/**
		 * turn off the window's title bar
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/**
		 * Finally ... make up for the appalling API design of Android by
		 * providing coherent, sane, properly-named method calls...
		 */
		if (null != getLastNonConfigurationInstance())
		{
			Log.i(getClass().getSimpleName(),
					"onCreate: ...app is auto-rotating");

			handleAutoRotateReloadState(getLastNonConfigurationInstance());
			handleAutoRotateFinished();
		}
		else if (savedInstanceState != null)
		{
			Log.i(getClass().getSimpleName(),
					"onCreate: ...app is de-suspending");
			handleActivityNewRestoreLocalVariables();
		}
		else
		{
			Log.i(getClass().getSimpleName(), "onCreate: ...app is "
					+ (hasRunBefore ? "re-starting"
							: "starting for the first time"));
			if (hasRunBefore)
				handleActivityNew();
			else
				handleActivityCreatedFirstTime();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.i(getClass().getSimpleName(), "STARTING: "
				+ getClass().getSimpleName());
	}

	/**
	 * Invoked when the Activity loses user focus
	 * 
	 * ALSO: *guaranteed* to be invoked before the app is killed - so save any
	 * app-level state here, as opposed to presentation state (should be saved
	 * in onSaveInstanceState)
	 */
	@Override
	protected void onPause()
	{
		super.onPause();

		Log.i(getClass().getSimpleName(), "PAUSING: "
				+ getClass().getSimpleName());

	}

	/**
	 * User can see the app; re-start animations, game playing, etc
	 */
	@Override
	protected void onResume()
	{
		Log.i(getClass().getSimpleName(), "RESUMING: "
				+ getClass().getSimpleName());

		super.onResume();
	}
}