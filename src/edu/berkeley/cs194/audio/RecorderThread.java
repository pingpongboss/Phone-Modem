package edu.berkeley.cs194.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class RecorderThread extends Thread {
	public final static int THRESHOLD = 200;
	
	FrequencyReceiver receiver;
	// variable to start or stop recording
	boolean recording;
	// the public variable that contains the frequency value "heard", it is
	// updated continually while the thread is running.
	public int frequency;

	public RecorderThread(FrequencyReceiver receiver) {
		this.receiver = receiver;
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		start();
	}

	@Override
	public void run() {
		AudioRecord recorder;
		int numCrossing, p;
		short audioData[];
		int bufferSize;

		bufferSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				// get the buffer size to use with this audio record
				AudioFormat.ENCODING_PCM_16BIT) * 3;

		// instantiate the AudioRecorder
		recorder = new AudioRecord(AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		// variable to use start or stop recording
		recording = true;
		// short array that pcm data is put into.
		audioData = new short[bufferSize]; // short array that pcm data is put
											// into.

		// loop while recording is needed
		while (recording) {
			// check to see if the recorder has initialized yet.
			if (recorder.getState() == android.media.AudioRecord.STATE_INITIALIZED)
				// check to see if the Recorder has stopped or is not recording,
				// and make it record.
				if (recorder.getRecordingState() == android.media.AudioRecord.RECORDSTATE_STOPPED)
					recorder.startRecording();

				else {
					// read the PCM audio data into the audioData array
					recorder.read(audioData, 0, bufferSize);

					// Now we need to decode the PCM data using the Zero
					// Crossings Method

					// initialize your number of zero crossings to 0
					numCrossing = 0;
					for (p = 0; p < bufferSize / 4; p += 4) {
						if (audioData[p] > 0 && audioData[p + 1] <= 0)
							numCrossing++;
						if (audioData[p] < 0 && audioData[p + 1] >= 0)
							numCrossing++;
						if (audioData[p + 1] > 0 && audioData[p + 2] <= 0)
							numCrossing++;
						if (audioData[p + 1] < 0 && audioData[p + 2] >= 0)
							numCrossing++;
						if (audioData[p + 2] > 0 && audioData[p + 3] <= 0)
							numCrossing++;
						if (audioData[p + 2] < 0 && audioData[p + 3] >= 0)
							numCrossing++;
						if (audioData[p + 3] > 0 && audioData[p + 4] <= 0)
							numCrossing++;
						if (audioData[p + 3] < 0 && audioData[p + 4] >= 0)
							numCrossing++;
					}// for p

					for (p = (bufferSize / 4) * 4; p < bufferSize - 1; p++) {
						if (audioData[p] > 0 && audioData[p + 1] <= 0)
							numCrossing++;
						if (audioData[p] < 0 && audioData[p + 1] >= 0)
							numCrossing++;
					}

					// Set the audio Frequency to half the number of zero
					// crossings, times the number of samples our buffersize is
					// per second.
					frequency = (8000 / bufferSize) * (numCrossing / 2);
					int amplitude = numCrossing * bufferSize;
					receiver.updateFrequency(frequency, amplitude);

				}// else recorder started

		} // while recording

		// stop the recorder before ending the thread
		if (recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING)
			recorder.stop();
		// release the recorders resources
		recorder.release();
		// set the recorder to be garbage collected.
		recorder = null;

	}// run

	public void end() {
		recording = false;
	}

	public interface FrequencyReceiver {
		void updateFrequency(int frequency, int amplitude);
	}

}// recorderThread