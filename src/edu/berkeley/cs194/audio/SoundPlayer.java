package edu.berkeley.cs194.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SoundPlayer extends Thread {
	public final static int HIGH = 1046;
	public final static int LOW = 880;
	public final static int MEDIUM = (HIGH + LOW) / 2;
	public final static int SILENT = -1;

	public final static int CONTROL_TONE_NUM = 6;
	public final static int AMPLITUDE_THRESHOLD = 10000;

	public final static double duration = .35; // seconds
	private final int sampleRate = 8000;
	private final int numSamples = (int) (duration * sampleRate);
	private double sample[];
	private byte generatedSnd[];
	private int[] frequencies;
	private AudioTrack audioTrack;

	boolean playing;

	public void output(int[] frequencies) {
		sample = new double[numSamples * frequencies.length];
		generatedSnd = new byte[2 * numSamples * frequencies.length];
		this.frequencies = frequencies;
		start();
	}

	@Override
	public void run() {
		playing = true;
		genTone();
		playSound();
	}

	void genTone() {
		// fill out the array
		for (int j = 0; j < frequencies.length; j++) {
			for (int i = 0; i < numSamples; ++i) {
				if (!playing)
					return;
				sample[i + j * numSamples] = Math.sin(2 * Math.PI * i
						/ (sampleRate / frequencies[j]));
			}
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			if (!playing)
				return;
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

		}
	}

	void playSound() {
		if (!playing)
			return;

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, numSamples * frequencies.length
						* 2, AudioTrack.MODE_STATIC);

		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}

	public void end() {
		playing = false;
		if (audioTrack != null
				&& audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			audioTrack.pause();
			audioTrack.stop();
		}
	}
}
