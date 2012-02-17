package edu.berkeley.cs194.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import edu.berkeley.cs194.R;
import edu.berkeley.cs194.audio.RecorderThread;
import edu.berkeley.cs194.audio.RecorderThread.FrequencyReceiver;

public class AudioModemActivity extends Activity implements FrequencyReceiver {
	RecorderThread recorder;
	TextView status;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status = (TextView) findViewById(R.id.status);
	}

	@Override
	protected void onResume() {
		super.onResume();

		recorder = new RecorderThread(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		recorder.recording = false;
	}

	@Override
	public void updateFrequency(final int frequency, final int amplitude) {
		if (amplitude > 200000) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int binary = frequency > 500 ? 1 : 0;
					status.setText(String.format("%d (f:%d : a:%d)", binary,
							frequency, amplitude));
				}
			});
		}
	}
}