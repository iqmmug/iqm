package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: WavFileWriteExample.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


/** Wav file WriteExample class
* A.Greensted
* http://www.labbookpages.co.uk
*
* File format is based on the information from
* http://www.sonicspot.com/guide/wavefiles.html
* http://www.blitter.com/~russtopia/MIDI/~jglatt/tech/wave.htm
*
* Version 1.0
*
**/

import java.io.*;


public class WavFileWriteExample
{
	public static void main(String[] args)
	{
		try
		{
			int sampleRate = 44100;		// Samples per second
			double duration = 5.0;		// Seconds

			// Calculate the number of frames required for specified duration
			long numFrames = (long)(duration * sampleRate);

			// Create a wav file with the name specified as the first argument
			WavFile wavFile = WavFile.newWavFile(new File(args[0]), 2, numFrames, 16, sampleRate);

			// Create a buffer of 100 frames
			double[][] buffer = new double[2][100];

			// Initialise a local frame counter
			long frameCounter = 0;

			// Loop until all frames written
			while (frameCounter < numFrames)
			{
				// Determine how many frames to write, up to a maximum of the buffer size
				long remaining = wavFile.getFramesRemaining();
				int toWrite = (remaining > 100) ? 100 : (int) remaining;

				// Fill the buffer, one tone per channel
				for (int s=0 ; s<toWrite ; s++, frameCounter++)
				{
					buffer[0][s] = Math.sin(2.0 * Math.PI * 400 * frameCounter / sampleRate);
					buffer[1][s] = Math.sin(2.0 * Math.PI * 500 * frameCounter / sampleRate);
				}

				// Write the buffer
				wavFile.writeFrames(buffer, toWrite);
			}

			// Close the wavFile
			wavFile.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
}
