package edu.berkeley.cs194.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioIn extends Thread {
	private static final String TAG = "AudioIn";
	private boolean stopped = false;
	private AudioReceiver mReceiver;

	public AudioIn(AudioReceiver receiver) {
		mReceiver = receiver;
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		start();
	}

	@Override
	public void run() {
		AudioRecord recorder = null;
		short[][] buffers = new short[256][160];
		int ix = 0;

		try { // ... initialise

			int N = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);

			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, N * 10);

			recorder.startRecording();

			// ... loop

			while (!stopped) {
				short[] buffer = buffers[ix++ % buffers.length];

				N = recorder.read(buffer, 0, buffer.length);

				mReceiver.process(buffer);
			}
		} catch (Throwable x) {
			Log.w(TAG, "Error reading voice audio", x);
		} finally {
			recorder.release();
			recorder = null;
		}
	}

	public void close() {
		stopped = true;
	}

	public interface AudioReceiver {
		void process(short[] buffer);
	}
}