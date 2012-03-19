package edu.berkeley.cs194.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.berkeley.cs194.R;
import edu.berkeley.cs194.audio.RecorderThread;
import edu.berkeley.cs194.audio.RecorderThread.FrequencyReceiver;
import edu.berkeley.cs194.audio.SoundPlayer;

public class AudioModemActivity extends Activity implements FrequencyReceiver {
	RecorderThread recorder;
	SoundPlayer player;
	TextView status;
	EditText frequency;
	Button play, stop, test, high, low;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status = (TextView) findViewById(R.id.status);
		frequency = (EditText) findViewById(R.id.frequency);
		play = (Button) findViewById(R.id.play);
		stop = (Button) findViewById(R.id.stop);
		test = (Button) findViewById(R.id.test);
		high = (Button) findViewById(R.id.high);
		low = (Button) findViewById(R.id.low);

		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int f = Integer.parseInt(frequency.getText().toString());
				player.end();

				player = new SoundPlayer();
				player.output(new int[] { f });
			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				player.end();
			}
		});

		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				player.end();

				player = new SoundPlayer();
				player.output(new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
						SoundPlayer.HIGH, SoundPlayer.LOW });
			}
		});

		high.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				player.end();

				player = new SoundPlayer();
				player.output(new int[] { SoundPlayer.HIGH });
			}
		});

		low.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				player.end();

				player = new SoundPlayer();
				player.output(new int[] { SoundPlayer.LOW });
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		recorder = new RecorderThread(this);
		player = new SoundPlayer();
	}

	@Override
	protected void onPause() {
		super.onPause();

		recorder.end();
		player.end();
	}


	@Override
	public void updateFrequency(final int frequency, final int amplitude) {
		if (amplitude > 200000) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int binary = frequency > RecorderThread.THRESHOLD ? 1 : 0;
					status.setText(String.format("%d (f:%d : a:%d)", binary,
							frequency, amplitude));
				}
			});
		}
	}
}