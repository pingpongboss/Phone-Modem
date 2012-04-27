package edu.berkeley.cs194.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.berkeley.cs194.R;
import edu.berkeley.cs194.audio.PitchDetector;
import edu.berkeley.cs194.audio.PitchDetector.FrequencyReceiver;
import edu.berkeley.cs194.audio.SoundPlayer;
import edu.berkeley.cs194.util.Utils;

public class AudioModemActivity extends Activity implements FrequencyReceiver {
	PitchDetector detector;
	SoundPlayer player;
	TextView status, status2;
	EditText frequency, message;
	Button play, stop, test, high, low, send;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status = (TextView) findViewById(R.id.status);
		status2 = (TextView) findViewById(R.id.status2);
		frequency = (EditText) findViewById(R.id.frequency);
		play = (Button) findViewById(R.id.play);
		stop = (Button) findViewById(R.id.stop);
		test = (Button) findViewById(R.id.test);
		high = (Button) findViewById(R.id.high);
		low = (Button) findViewById(R.id.low);
		message = (EditText) findViewById(R.id.message);
		send = (Button) findViewById(R.id.send);

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

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				player.end();

				player = new SoundPlayer();
				int[] frequencies = Utils.textToMorse(message.getText()
						.toString());
				Log.d("AudioModemActivity", frequencies.toString());
				player.output(frequencies);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		ArrayList<FrequencyReceiver> receivers = new ArrayList<PitchDetector.FrequencyReceiver>();
		receivers.add(this);
		receivers.add(receiver);
		detector = new PitchDetector(receivers);
		player = new SoundPlayer();
	}

	@Override
	protected void onPause() {
		super.onPause();

		detector.interrupt();
		player.end();
	}

	@Override
	public void updateFrequency(final double frequency, final double amplitude,
			final HashMap<Double, Double> frequencies) {

		if (amplitude > SoundPlayer.AMPLITUDE_THRESHOLD) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					status2.setText(String.format("(f:%d : a:%d)",
							Math.round(frequency), Math.round(amplitude)));
				}

			});
		}
	}

	FrequencyReceiver receiver = new FrequencyReceiver() {
		long lastTime = -1;
		ArrayList<Integer> tones = new ArrayList<Integer>();
		boolean started = false;

		@Override
		public void updateFrequency(double frequency, double amplitude,
				HashMap<Double, Double> frequencies) {
			long current = SystemClock.uptimeMillis();
			if (lastTime != -1) {
				// long diff = current - lastTime;
				// Log.d("Receiver", "diff: " + diff);
			}
			lastTime = current;

			if (amplitude > SoundPlayer.AMPLITUDE_THRESHOLD) {
				// we hear a tone
				int tone = frequency > SoundPlayer.MEDIUM ? SoundPlayer.HIGH
						: SoundPlayer.LOW;
				tones.add(tone);
				if (started) {
					// add tone to list and wait for silence
				} else {
					// listen for control signal
					if (tone == SoundPlayer.HIGH) {
						if (tones.size() == SoundPlayer.CONTROL_TONE_NUM) {
							tones.clear();
							started = true;
							connected();
							Log.d("Receiver",
									"Control tone succeeded. Connection opened.");
						} else if (tones.size() == 1) {
							Log.d("Receiver", "Listening for "
									+ SoundPlayer.CONTROL_TONE_NUM
									+ " HIGH tones.");
							connecting();
						}
					} else {
						// low tone
						tones.clear();
						Log.d("Receiver", "Control tone failed.");
						failed();
					}
				}
			} else {
				// silence
				if (started) {
					if (tones.isEmpty()) {
						started = false;
						Log.d("Receiver", "Connection closed.");
						disconnected();
					} else {
						String text = Utils.morseToText(tones);
						Log.d("Receiver", "Text: " + text);
						addLetter(text);
					}
					tones.clear();
				} else {
					// expected control tone
					if (!tones.isEmpty()) {
						tones.clear();
						Log.d("Receiver", "Control tone failed.");
						failed();
					}
				}
			}
		}
	};

	public void addLetter(final String letter) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status.setText(status.getText() + letter);
			}
		});
	}

	public void connecting() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status.setText("Connecting...");
			}
		});
	}

	public void connected() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status.setText("Connected: ");
			}
		});
	}

	public void disconnected() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status.setText(status.getText() + ".");
			}
		});
	}

	public void failed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status.setText("Idle...");
			}
		});
	}
}