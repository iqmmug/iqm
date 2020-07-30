package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: SVSImageExtractor.java
 * 
 * $Id: SVSImageExtractor.java 678 2018-09-09 20:19:48Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-api/src/main/java/at/mug/iqm/commons/util/image/SVSImageExtractor.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.exception.UnreadableECGFileException;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.commons.io.TableFileWriter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.osea4java.OSEAFactory;
import at.mug.iqm.commons.util.osea4java.classification.BeatDetectionAndClassification;
import at.mug.iqm.commons.util.osea4java.classification.ECGCODES;
import at.mug.iqm.commons.util.osea4java.classification.BeatDetectionAndClassification.BeatDetectAndClassifyResult;
import at.mug.iqm.commons.util.osea4java.detection.QRSDetector;
import at.mug.iqm.commons.util.osea4java.detection.QRSDetector2;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.config.ConfigManager;

/**
 * @author HA QRSPeaks extractor on the fly using osea4java 
 * @since  2018-11
 * @update 
 */
public class QRSPeaksExtractor extends SwingWorker<Boolean, Void> {
	// Logging variables
	private static Class<?> caller = QRSPeaksExtractor.class;
	private static final Logger logger = LogManager.getLogger(QRSPeaksExtractor.class);

	/**
	 * Default parameters for the QRSPeaks extraction: offset, sample rate, oseaMethod,  output option.
	 */
	private int[] params = new int[] {0, 125, 1, 1};

	/**
	 * 
	 */
	private File[] files = new File[0];

	/**
	 * 
	 */
	private Vector<File> extractedFiles = new Vector<File>();

	private int offset       = 0;   // starting offset
	private int sampleRate   = 180; // sample rate 180 for Herbert's fiels 125 for Helena's files
	private int oseaMethod   = 1;   // 0...QRSDetect, 1..QRSDetsct2, 2...BeatDetectionAndClassify
	private int outputOption = 1;   // 0...xy coordinates,  1...intervals
	
	
	int numbOfReadBytes;
	int val;
	double timeStamp1 = 0.0;
	double timeStamp2 = 0.0;
	List<Integer> valueBuffer;	
	int bufferLength = 0; // seconds*sampleRate = number of buffered data values
	int delay;
	
	//QRS-Detection
	int indexOfValue;
	int numberOfFoundPoints = 0;
	double meanInterval = 0.0;
	
	int numberOfNormalPeaks   = 0;
	int numberOfPVCPeaks      = 0;
	int numberOfUnknownPeaks  = 0;
	

	/**
	 * A simple string with tab delimited values for saving as a file
	 */
	String stringTable = null;
	
	
	public QRSPeaksExtractor() {
		
	}
		

	

	/**
	 * This method reads out the plot information from a specified ECG plot
	 * <code>file</code>.
	 * 
	 * @param file - the specified file to be read
	 * @return a 2D Object array
	 */
	public Object[][] readECGMetaData(File file) throws UnreadableECGFileException {
		StringBuffer sb = new StringBuffer();
		Object[][] data = null;
		try {
			logger.debug("Reading Meta Data of ECG plot...");

			//fis = FileInputStream(file);	
			//fis.close();
			
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			throw new UnreadableECGFileException();
		}
		return data;
	}

	/**
	 * This method extracts the files with the given params.
	 * 
	 * @param files
	 *            - the file list
	 * @param params
	 *            - parameters for processing the file list:
	 *            <ul>
	 *            <li>[0]... starting offset</li>
	 *            <li>[1]... osea method</li>
	 *            <li>[2]... sample rate</li>
	 *            <li>[3]... output option</li>
	 *            </ul>
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean extract(File[] files, int[] params) {
		if (params == null)
			return false;

//		if ((params[0] < 0) || (params[1] < 1)) {
//			// use default values
//			params = this.params;
//		}

		logger.debug("Extracting QRS peaks from the specified ECG files.");
		
		File currImgDir = ConfigManager.getCurrentInstance().getImagePath();

		offset       = params[0];
		sampleRate   = params[1];
		oseaMethod   = params[2];
		outputOption = params[3];
		
		int numUnknownBeats = 0;
		int numNormalBeats = 0;
		int numPVCBeats = 0;
		
		bufferLength = 20*sampleRate; // seconds*sampleRate = number of buffered data values
	
	
		for (int f = 0; f < files.length; f++) {
			long startTime = System.currentTimeMillis();
			BoardPanel.appendTextln(" ");
			BoardPanel.appendTextln("Processing file " + (f + 1) + "/" + files.length);
	
			try { //extract QRS peaks from a file
				BoardPanel.appendTextln("Extracting QRS peaks from: " + files[f]);
				// Open the 16bit file				
		        FileInputStream fis = new FileInputStream(files[f]);
		        //int numBytes = fis.available();
				//byte[] bytes = fis.readNBytes(100000);
		        
				//Set/Reset some variables such as Result table
		    	indexOfValue = 0;
				stringTable = "";
				valueBuffer = new ArrayList<Integer>();	
						
				//define buffer for maximal QRSDetect delay
				for (int i = 1; i <= bufferLength; i++){ //create a shift register (buffer) with a number of values
					valueBuffer.add(0);	
				}
				
				//prepare output string table
				if (outputOption == 0) { //coordinates	
					String domainHeader = "QRS event time";
					String domainUnit = " [s]";	
					String dataHeader = "QRS event value";
					String dataUnit = "";
						
					stringTable += domainHeader + domainUnit + "\t" + dataHeader + dataUnit + "\n"; // "\t" Tabulator		
					//stringTable += domainX.get(i).toString() + "\t" + coordinateY.get(i).toString() + "\n";	
				}	
				if (outputOption == 1) { //intervals
					String domainHeader = "QRS event time";
					String domainUnit = " [s]";	
					String dataHeader = "RR interval";
					String dataUnit = " [s]";

					stringTable += domainHeader + domainUnit + "\t" + dataHeader + dataUnit + "\n"; // "\t" Tabulator
					//stringTable += domainX.get(i).toString() + "\t" + intervals.get(i).toString() + "\n";
				}	
				
				
		    	if (oseaMethod == 0){//QRSDetec1------------------------------------------------------------
		    		QRSDetector qrsDetector = OSEAFactory.createQRSDetector(sampleRate);	
		    	
		    		byte[] bytes = new byte[2];
		    		
		    		//scroll through offset
		    		int s = 0;
		    		while (s <= offset) {
		    			numbOfReadBytes = fis.read(bytes); 
		    			indexOfValue += 1;
		    			s = s+1;
		    		}
		    		BoardPanel.appendTextln("Skipped " + offset + " initial data points.");
				  
		    		numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {
						
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						//int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);  unsigned short
						val = ((bytes[1] << 8) | (bytes[0] & 0xFF)); //signed short
					
						indexOfValue = indexOfValue + 1;
						//System.out.println("Found value " + val + " at index " + indexOfValue);
						
						valueBuffer.add(0, val);          //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength); //remove oldest value on the right
						delay = qrsDetector.QRSDet(val); //gives back the delay of preceding QRS peak;
							
						if (delay != 0) {
							numberOfFoundPoints = numberOfFoundPoints + 1;
							timeStamp2 = indexOfValue - delay; //-1?
							//System.out.println("A QRS-Complex was detected at sample: " + (timeStamp2));
							if (outputOption == 0) { //XY coordinates
								//domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								//coordinateY.add((double) valueBuffer.get(delay));
								stringTable += String.valueOf(timeStamp2) + "\t" + valueBuffer.get(delay).toString() + "\n";	
							}
							if ((outputOption == 1) && (numberOfFoundPoints > 1)) { //intervals //skipped first value because it is too long 
								////domainX.add((double) numberOfFoundPoints); //times of beats
								//domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								//intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
								stringTable += String.valueOf(timeStamp2/sampleRate) + "\t" + String.valueOf((timeStamp2 -timeStamp1)/sampleRate) + "\n";	
								meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
							}					
							timeStamp1 = timeStamp2;
							timeStamp2 = 0.0;				
						}
						
						//this.fireProgressChanged((int) (i) * 95 / (signal.size()));	
						numbOfReadBytes = fis.read(bytes);	
					}	
					BoardPanel.appendTextln("Number of detected QRS complexes using QRSDetect: "+ numberOfFoundPoints);
					if (outputOption == 1) {
						meanInterval = meanInterval/numberOfFoundPoints;
						BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
						BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
					}
					this.setProgress(90);
					fis.close();
		    	}//oseaMethod 1----------------------------------------------------------------------------------
		    	
		    	if (oseaMethod == 1){//QRSDetec2------------------------------------------------------------
		    		QRSDetector2 qrsDetector = OSEAFactory.createQRSDetector2(sampleRate);	
		    	
		    		byte[] bytes = new byte[2];
		    		
		    		//scroll through offset
		    		int s = 0;
		    		while (s <= offset) {
		    			numbOfReadBytes = fis.read(bytes); 
		    			indexOfValue += 1;
		    			s = s+1;
		    		}
		    		BoardPanel.appendTextln("Skipped " + offset + " initial data points.");
		    		
				    numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {
						
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						//int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);  unsigned short
						val = ((bytes[1] << 8) | (bytes[0] & 0xFF)); //signed short
									
						indexOfValue = indexOfValue + 1;
										
						//System.out.println("Found value " + val + " at index " + indexOfValue);
						
						valueBuffer.add(0, val);  //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength); //remove oldest value on the right
				
						delay = qrsDetector.QRSDet(val); //gives back the delay of preceding QRS peak;
							
						if (delay != 0) {
							numberOfFoundPoints = numberOfFoundPoints + 1;
							timeStamp2 = indexOfValue - delay; //-1?
							//System.out.println("A QRS-Complex was detected at sample: " + (timeStamp2));
							if (outputOption == 0) { //XY coordinates
								//domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								//coordinateY.add((double) valueBuffer.get(delay));
								stringTable += String.valueOf(timeStamp2) + "\t" + valueBuffer.get(delay).toString() + "\n";	
							}
							if ((outputOption == 1) && (numberOfFoundPoints > 1)) { //intervals //skipped first value because it is too long 
								////domainX.add((double) numberOfFoundPoints); //times of beats
								//domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								//intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
								stringTable += String.valueOf(timeStamp2/sampleRate) + "\t" + String.valueOf((timeStamp2 -timeStamp1)/sampleRate) + "\n";
								meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
							}					
							timeStamp1 = timeStamp2;
							timeStamp2 = 0.0;				
						}
						
						//this.fireProgressChanged((int) (i) * 95 / (signal.size()));	
						numbOfReadBytes = fis.read(bytes);	
					}	
					BoardPanel.appendTextln("Number of detected QRS complexes using QRSDetect2: "+ numberOfFoundPoints);
					if (outputOption == 1) {
						meanInterval = meanInterval/numberOfFoundPoints;
						BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
						BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
					}
					this.setProgress(90);
					fis.close();
		    	}//oseaMethod 2----------------------------------------------------------------------------------

				//Method3-detection and classification--------------------------------------------------------------------
				//QRSDetector2 for detection
				if (oseaMethod == 2){
					numberOfNormalPeaks   = 0;
					numberOfPVCPeaks      = 0;
					numberOfUnknownPeaks  = 0;
					meanInterval          = 0;
					
					BeatDetectionAndClassification bdac = OSEAFactory.createBDAC(sampleRate, sampleRate/2);		
				  	
		    		byte[] bytes = new byte[2];
		    		
		    		//scroll through offset
		    		int s = 0;
		    		while (s <= offset) {
		    			numbOfReadBytes = fis.read(bytes); 
		    			indexOfValue += 1;
		    			s = s+1;
		    		}
		    		BoardPanel.appendTextln("Skipped " + offset + " initial data points.");
		    		
				    numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {	
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						//int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);  unsigned short
						val = ((bytes[1] << 8) | (bytes[0] & 0xFF)); //signed short
							
						indexOfValue = indexOfValue + 1;
						//System.out.println("Found value " + val + " at index " + indexOfValue);
						
						valueBuffer.add(0, val);  //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength-1); //remove oldest value on the right
						
						BeatDetectAndClassifyResult result = bdac.BeatDetectAndClassify(val);
						delay = result.samplesSinceRWaveIfSuccess;	
						
						if (delay != 0) {
							int qrsPosition =  delay;
		
							if (result.beatType == ECGCODES.NORMAL) {
								//BoardPanel.appendTextln("A normal beat type was detected at sample: " + qrsPosition);
								numberOfNormalPeaks += 1; 
							} else if (result.beatType == ECGCODES.PVC) {
								//BoardPanel.appendTextln("A premature ventricular contraction was detected at sample: " + qrsPosition);
								numberOfPVCPeaks += 1;
							} else if (result.beatType == ECGCODES.UNKNOWN) {
								//BoardPanel.appendTextln("An unknown beat type was detected at sample: " + qrsPosition);
								numberOfUnknownPeaks +=1;
							}
							numberOfFoundPoints = numberOfFoundPoints + 1;
							timeStamp2 = indexOfValue-delay;
//							System.out.println("qrsPosition: " +qrsPosition);
//							System.out.println("timeStamp2: "  +timeStamp2);
//							System.out.println("timeStamp1: "  +timeStamp1);
							if (outputOption == 0) { //XY coordinates
								//domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								//coordinateY.add((double) valueBuffer.get(delay));
								stringTable += String.valueOf(timeStamp2) + "\t" + valueBuffer.get(delay).toString() + "\n";	
							}
							if ((outputOption == 1) && (numberOfFoundPoints > 1)) { //intervals //skipped first value because it is too long 
								////domainX.add((double) numberOfFoundPoints); //times of beats
								//domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								//intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
								stringTable += String.valueOf(timeStamp2/sampleRate) + "\t" + String.valueOf((timeStamp2 -timeStamp1)/sampleRate) + "\n";
								meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
							}								
							timeStamp1 = timeStamp2;
							timeStamp2 = 0.0;
						}			
						//this.fireProgressChanged((int) (i) * 95 / (signal.size()));	
						numbOfReadBytes = fis.read(bytes);	
					}	
					BoardPanel.appendTextln("Number of detected QRS complexes using BeatDetectAndClassify: "+ numberOfFoundPoints);
					BoardPanel.appendTextln("Number of normal QRS peaks: "+ numberOfNormalPeaks);
					BoardPanel.appendTextln("Number of premature ventricular contractions: "+ numberOfPVCPeaks);
					BoardPanel.appendTextln("Number of unknown QRS peaks: "+ numberOfUnknownPeaks);
					if (outputOption == 1) {
						meanInterval = meanInterval/numberOfFoundPoints;
						BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
						BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
					}
					this.setProgress(90);
					fis.close();
				}//Method 3-------------------------------------------------------------------------------------
		    	
			} catch (IOException e) { //
				logger.error("An error occurred: ", e);
			}
			
//			if (!coordinateY.isEmpty()) {
//				//domainX.remove(0);
//				//coordinateY.remove(0);
//			}
//			if (!intervals.isEmpty()) {// eliminate first element because it is too long (osea skips first 8 beats)
//				domainX.remove(0);
//				intervals.remove(0);		
//			}
					
			boolean saveToFile = true; //for debugging
			if (saveToFile) {
				try {
					//save QRS peaks file 
					logger.debug("QRS peaks have been detected, now storing to disk.");
					BoardPanel.appendTextln("QRS peaks have been detected, now storing to disk.");
					// construct the filename
					String newFileName = null;
					if (outputOption == 0) {
						newFileName = files[f].toString() + "_osea" + String.valueOf(oseaMethod + 1) + "_XYcoordinates." + IQMConstants.TXT_EXTENSION;
					}
					if (outputOption == 1) {
						newFileName = files[f].toString() + "_osea" + String.valueOf(oseaMethod + 1) + "_intervals." + IQMConstants.TXT_EXTENSION;
					}
					
					Files.write(Paths.get(newFileName), stringTable.getBytes(StandardCharsets.UTF_8));
					
					logger.debug(newFileName +" has been stored to disk.");
	
					//This is for displaying files in IQM
					//extractedFiles.add(newFile);
	
					// Print results
					BoardPanel.appendTextln("Saved result file: " + (f + 1) + "/" + files.length);
					
					
					
				} catch (Exception e) {
					DialogUtil.getInstance().showErrorMessage(
							"Cannot store the extracted QRS peaks",e , true);
					return false;
				}
			} 
			
			long duration = System.currentTimeMillis() - startTime;
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("HHH:mm:ss:SSS");
			BoardPanel.appendTextln(this.getClass().getName() + ": Elapsed time: "+ sdf.format(duration));

		}// f files[] loop

		this.setProgress(90);
		return true;
	}

	/**
	 * This method appends String(num) to the image name
	 */
	private File appendTextToFileName(File file, String strAppend) {
		String str = file.toString();
		int dotPos = str.lastIndexOf(".");
		String name = str.substring(0, dotPos);
		String ext = str.substring(dotPos + 1); // "b16"
		
		ext = IQMConstants.TXT_EXTENSION;
	
		str = name + strAppend + "." + ext;
		return new File(str);
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		this.firePropertyChange("singleTaskRunning", 0, 1);
		boolean success = this.extract(this.files, this.params);
		this.firePropertyChange("singleTaskRunning", 1, 0);
		logger.info("The extraction was " + (success ? "" : "NOT")
				+ " successful, returning [" + (success ? "true" : "false")
				+ "].");
		return success;
	}

	@Override
	protected void done() {
		try {
			boolean success = this.get();
			if (success) {
				String message = "Extraction finsihed";
			
				//TO DO Loading into IQM
//				int selection = DialogUtil
//						.getInstance()
//						.showDefaultQuestionMessage(
//								message
//										+ "\nDo you want to load the extracted files now?");
//				if (selection == IDialogUtil.YES_OPTION) {
//					logger.debug("Loading of files selected");
//					logger.debug("Loading of files not implemented yet");
//					BoardPanel.appendTextln("QRSPeakExtractor: Loading of files not implemented yet");
////					File[] newFiles = new File[this.extractedFiles.size()];
////					for (int i = 0; i < newFiles.length; i++) {
////						newFiles[i] = this.extractedFiles.get(i);
////					}
////					Application.getTank().loadImagesFromHD(newFiles);
//					
//					//Opening should go over IqmDataBoxes?
////					List<IqmDataBox> itemList =  new ArrayList<IqmDataBox>();
////					Application.getTank().addNewItems(itemList);;
//				}
//				if (selection == IDialogUtil.NO_OPTION) {
//					logger.debug("No loading of files selected");
//				}
//				this.setProgress(0);
			}
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} finally {
			System.gc();
		}

	}

	/**
	 * @return the params
	 */
	public int[] getParams() {
		return params;
	}




	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(int[] params) {
		this.params = params;
	}


	/**
	 * @return the files
	 */
	public File[] getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}

	/**
	 * @return the extractedFiles
	 */
	public Vector<File> getExtractedFiles() {
		return extractedFiles;
	}

	/**
	 * @param extractedFiles
	 *            the extractedFiles to set
	 */
	public void setExtractedFiles(Vector<File> extractedFiles) {
		this.extractedFiles = extractedFiles;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOseaMethod() {
		return oseaMethod;
	}

	public void setOseaMethod(int oseaMethod) {
		this.oseaMethod = oseaMethod;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public int getOutputOption() {
		return outputOption;
	}

	public void setOutputOption(int outputOption) {
		this.outputOption = outputOption;
	}

}
