package de.dfki.experiment;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/*
 * Undocumented, intern class. Should not be used independently.
 * If you want to use the MR, then use the class MicrophoneRecorder.
 */

public class Mic {

	private static AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
	private static float sampleRate = 44100.0F;
	private static int sampleSizeInBits = 16;
	private static int channels = 2;
	private static int frameSize = 4;
	private static float frameRate = 44100.0F;
	private static boolean bigEndian = false;
	private TargetDataLine targetDataLine = null;
	private AudioInputStream inputStream = null;
	private AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
	private int mode = 0;
	private File file = null;
	
	private Runnable recRunnable = new Runnable () {
		@Override
		public void run () {
			while (mode != -1) {
				try {
					Thread.sleep(25);
				} catch (Exception e) {					
				}
				if (mode == 2) {
					mode = 0;
					try {
						targetDataLine.open();
					} catch (LineUnavailableException e) {
						
					}
					targetDataLine.flush();
					targetDataLine.start();
					try {
						AudioSystem.write (inputStream, targetType, file);
					} catch (IOException e) {
						System.out.println ("IOException @ rec");
						System.exit (-1);
					}
				}
			}
			targetDataLine.close();
		}
	};
	
	public Mic () {
		// Create the AudioFormat specified by given parameters
		AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
		// Get the microphone and open (access) it
		try {
		   	targetDataLine = AudioSystem.getTargetDataLine (audioFormat);
		   	targetDataLine.open (audioFormat);
		} catch (LineUnavailableException e) {
		  	System.out.println ("Microphone not found.");
		  	System.exit (-1);
		}
		// Set the inputStream
		inputStream = new AudioInputStream (targetDataLine);
		// start the rec as thread
		Thread recThread = new Thread (recRunnable);
		recThread.start();
	}
	
	public void rec (File file) {
		this.file = file;
		this.mode = 2;
	}
	
	public void stop () {
		// wait a short time before stopping, because people tend to release the button too early
		try {
			Thread.sleep(400);
		} catch (Exception e) {
			
		}
		targetDataLine.stop();
	}
	
	public void close () {
		this.mode = -1;
	}
	
}
