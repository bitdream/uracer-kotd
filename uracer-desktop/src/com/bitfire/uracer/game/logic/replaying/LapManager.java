
package com.bitfire.uracer.game.logic.replaying;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.bitfire.uracer.configuration.UserProfile;
import com.bitfire.uracer.game.actors.Car;
import com.bitfire.uracer.game.actors.CarForces;
import com.bitfire.uracer.game.logic.replaying.ReplayRecorder.RecorderError;

/** Manage player's performance recordings and keeps basic lap information */
public class LapManager implements Disposable {

	private final String levelId;
	private final ReplayRecorder recorder;
	private final ReplayManager manager;
	private Replay last;

	public LapManager (UserProfile userProfile, String levelId) {
		this.levelId = levelId;
		recorder = new ReplayRecorder(userProfile.userId);
		manager = new ReplayManager(userProfile, levelId);
		last = null;
	}

	@Override
	public void dispose () {
		manager.dispose();
		recorder.dispose();
	}

	/** Stops recording and invalidates last recorded replay, optionally resetting the record timer */
	public void reset (boolean resetTimer) {
		abortRecording(resetTimer);
		last = null;
	}

	public void resetTimer () {
		recorder.resetTimer();
	}

	/** Starts recording the player lap performance. Returns the Replay instance where the recording is being performed. */
	public void startRecording (Car car) {
		if (recorder.isRecording()) {
			Gdx.app.log("TrackLapManager", "Couldn't start recording since it's already started.");
			return;
		}

		recorder.beginRecording(car, levelId);
	}

	/** Add and record the specified CarForces */
	public RecorderError record (CarForces forces) {
		if (recorder.isRecording()) {
			return recorder.add(forces);
		}

		return RecorderError.RecordingNotEnabled;
	}

	/** Ends recording the previously started lap performance */
	public Replay stopRecording () {
		if (recorder.isRecording()) {
			last = recorder.endRecording();

			// will not be added if worse than the worst
			return manager.addReplay(last);
		}

		return null;
	}

	/** Discard the performance currently being recorded */
	public void abortRecording (boolean resetTimer) {
		recorder.reset();
		if (resetTimer) {
			resetTimer();
		}
	}

	/** Returns whether or not the lap manager is recording the player's performance */
	public boolean isRecording () {
		return recorder.isRecording();
	}

	public float getCurrentReplaySeconds () {
		return recorder.getElapsedSeconds();
	}

	/** Returns the Replay instance where the last recording took place */
	public Replay getLastRecordedReplay () {
		return last;
	}

	public Iterable<Replay> getReplays () {
		return manager.getReplays();
	}

	public Replay getBestReplay () {
		return manager.getBestReplay();
	}

	public void removeAllReplays () {
		manager.removeAll();
	}
}
