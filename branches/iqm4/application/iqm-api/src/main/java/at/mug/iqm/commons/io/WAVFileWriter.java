package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: PlainTextFileWriter.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.commons.util.plot.WavFile;

/**
 * This helper class writes a WAV (default) encoded file.
 * Encoding can be set in a custom constructor {@link #PlainTextFileWriter(File, String, String)}.
 * 
 * @author 2018-08 HA
 * 
 */
public class WAVFileWriter implements Runnable {
	
	private static final Logger logger = Logger.getLogger(PlainTextFileWriter.class);
			
	private File file;
	private DefaultTableModel content;
	private String encoding = "LPCM";

	/**
	 * Create a new writer for a given file with the specified content.
	 * 
	 * @param file
	 * @param content
	 */
	public WAVFileWriter(File file, DefaultTableModel content) {
		this.file = file;
		this.content = content;
	}

	/**
	 * Create a new writer for a given file with the specified content and
	 * encoding.
	 * 
	 * @param file
	 *            the destination file
	 * @param content
	 *            the content
	 * @param encoding
	 *            specify the encoding string, if <code>null</code> is passed,
	 *           LPCM is assumed
	 */
	public WAVFileWriter(File file, DefaultTableModel content, String encoding) {
		this(file, content);
		if (encoding != null) {
			this.encoding = encoding;
		}
	}

	/**
	 * Performs the actual writing of the content.
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
	
		try
		{
			int sampleRate = 44100;		// Dummy Samples per second
			int numBits    = 16;
			//double duration = 5.0;	//Seconds

			// Calculate the number of frames required for specified duration
			//long numFrames = (long)(duration * sampleRate);
			long numFrames = content.getRowCount();
			
			//get number of channels
			int numChannels = content.getColumnCount()-1; //-1 because first column is only number of frames
			
			//get minimal and maximal value(s) to normalize data later on
			double minVal = Double.MAX_VALUE;
			double maxVal = -Double.MAX_VALUE;
			double val    = 0.0;
			for (int cc = 1; cc<=numChannels; cc++) { //first column is not a dta column
				for (int rr =0; rr<numFrames; rr++) {
					val = Double.valueOf((String) content.getValueAt(rr, cc)).doubleValue();
					if (val > maxVal) maxVal = val;
					if (val < minVal) minVal = val;
				}
			}
			System.out.println("Minimal value of all columns:" + minVal);
			System.out.println("Maximal value of all columns:" + maxVal);
					
			double normFactor = (2.0/(maxVal-minVal));   //Normalized value = normFactor*(oldValue -minVal)-1;
			//wav file is between - 1 and +1
			

			// Create a wav file with the name specified as the first argument
			WavFile wavFile = WavFile.newWavFile(file, numChannels, numFrames, numBits, sampleRate);

			// Create a buffer of 100 frames
			int bufferSize = 100;
			//if (numFrames < bufferSize) bufferSize = (int)numFrames;
			double[][] buffer = new double[numChannels][bufferSize];

			// Initialise a local frame counter
			int frameCounter = 0;

			// Loop until all frames written
			long startTime = System.currentTimeMillis();
			while (frameCounter < numFrames)
			{
				// Determine how many frames to write, up to a maximum of the buffer size
				long remaining = wavFile.getFramesRemaining();
				int toWrite = (remaining > 100) ? 100 : (int) remaining;

				// Fill the buffer, one tone per channel
				for (int s=0 ; s<toWrite ; s++, frameCounter++)
				{
					for (int c=0; c<numChannels; c++) { 
//						buffer[c][s] = Double.parseDouble((String) content.getValueAt(frameCounter, c+1)); //c=1 because first column is only number of frames	
//						double value1 = Double.parseDouble((String) content.getValueAt(frameCounter, c+1));
//						double value2 = Double.valueOf((String) content.getValueAt(frameCounter, c+1)).doubleValue();
//						System.out.println(" " + content.getValueAt(frameCounter, c+1) +"  " + value1 + "  "+value2);
						
						//Normalized values between - and +1
						buffer[c][s] = normFactor*(Double.valueOf((String) content.getValueAt(frameCounter, c+1)).doubleValue()-minVal)-1.0; //c=1 because first column is only number of frames
					}
				}
				// Write the buffer
				wavFile.writeFrames(buffer, toWrite);
			}
			// Close the wavFile
			wavFile.close();
			long duration = System.currentTimeMillis() - startTime;
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("HHH:mm:ss:SSS");
			BoardPanel.appendTextln("WAV file writing finished, elapsed time: "+ sdf.format(duration));
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		
	
	}

	@Override
	public void run() {
		try {
			write();
		} catch (IOException e) {
			logger.error("Error writing file!", e);
		}
	}
}
