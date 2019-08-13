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
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import java.util.ArrayList;
import java.util.List;
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
	private int sampleRate   = 125; // sample rate
	private int oseaMethod   = 1;   // 0...QRSDetect, 1..QRSDetsct2, 2...BeatDetectionAndClassify
	private int outputOption = 1;   // 0...xy coordinates,  1...intervals 

	/**
	 * Output variables
	 */
	Vector<Double> domainX       = new Vector<Double>();
	Vector<Double> coordinateY   = new Vector<Double>();
	Vector<Double> intervals     = new Vector<Double>();
	
	private JTable table = null;
	private List<PlotModel> plotModels = null;
	private DefaultTableModel tableModel = null;

	public QRSPeaksExtractor() {
		
	}
		
	/**
	 * This is a copied and adaped method from the class PlotPanel();
	 * Set the table data from a plot model to the panel.
	 */
	protected void setTableData() {
		// Prepare table
		int numPlotModels = plotModels.size();
		int numColumns = numPlotModels;
		int numRows = 0;
		Vector<?>[] data = new Vector<?>[numPlotModels];
		Vector<?>[] domain = new Vector<?>[numPlotModels];
		String[] dataHeaders = new String[numPlotModels];
		String[] dataUnits = new String[numPlotModels];
		String[] domainHeaders = new String[numPlotModels];
		String[] domainUnits = new String[numPlotModels];

		// Prepare table
		tableModel = new DefaultTableModel();
		table = new JTable(tableModel);

		// adding a lot of columns would be very slow due to active modellistener
		tableModel.removeTableModelListener(table);

		for (int c = 0; c < numColumns; c++) { // read content from plotModels
			data[c] = plotModels.get(c).getData();
			domain[c] = plotModels.get(c).getDomain();
			dataHeaders[c] = plotModels.get(c).getDataHeader();
			dataUnits[c] = plotModels.get(c).getDataUnit();
			domainHeaders[c] = plotModels.get(c).getDomainHeader();
			domainUnits[c] = plotModels.get(c).getDomainUnit();
		}

		for (int c = 0; c <= numColumns; c++) { // first column should be the
												// domain interval
			String stringCol = "";
			if (c == 0) {
				if (domainHeaders[0] == null && domainUnits[0] == null) {
					stringCol = "#";
				} else if (domainHeaders[0] != null) {
					stringCol = String.valueOf(domainHeaders[0]);
				} else {
					stringCol = " [" + String.valueOf(domainUnits[0]) + "]";
				}

			} else {
				if (!(dataHeaders[c - 1] == null)
						&& !dataHeaders[c - 1].isEmpty()) {
					stringCol = String.valueOf(dataHeaders[c - 1]);
				}
				if (!(dataUnits[c - 1] == null) && !dataUnits[c - 1].isEmpty()) {
					stringCol = stringCol + " ["
							+ String.valueOf(dataUnits[c - 1]) + "]";
				}
			}

			tableModel.addColumn(stringCol);
		}

		//get maximal number of rows (maximal length of plots)
		numRows = 0;
		for (int c = 0; c < numColumns; c++){
			if (data[c].size() > numRows) numRows = data[c].size();
		}
		
		Vector<String> vectorTemp = new Vector<String>();

		for (int i = 0; i < numRows; i++) {
			vectorTemp.clear();
			
			// if more than one signal is selected: x-axis are ascending integers:
			if(numColumns>1){
				vectorTemp.add(String.valueOf(i+1));
			}
			else{
				vectorTemp.add(String.valueOf(domain[0].get(i))); // x-axis
			}
						
			for (int ii = 0; ii < numColumns; ii++) { // y-axis
				if (i < data[ii].size()){  //some plots are shorter and have no more data points
					vectorTemp.add(String.valueOf(data[ii].get(i)));
				}
				else {
					vectorTemp.add(null);
				}
			}
			tableModel.addRow(new Vector<String>(vectorTemp));
		}
		vectorTemp = null;

		//this.scrollPaneTable.setViewportView(this.table); //these lines are disabled

		tableModel.addTableModelListener(table);
		tableModel.fireTableStructureChanged();
		tableModel.fireTableDataChanged();
		table.setAutoResizeMode(0);
	}
	

	/**
	 * This method reads out the plot information from a specified ECG plot
	 * <code>file</code>.
	 * 
	 * @param file - the specified file to be read
	 * @return a 2D Object array
	 */
	public Object[][] readPlotMetaData(File file) throws UnreadableECGFileException {
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
	
		for (int f = 0; f < files.length; f++) {
			try { //extract QRS peaks from a file
				BoardPanel.appendTextln(" ");
				BoardPanel.appendTextln("Extracting QRS peaks from: " + files[f]);
				// Open the 16bit file				
		        FileInputStream fis = new FileInputStream(files[f]);
		        int numBytes = fis.available();
				//byte[] bytes = fis.readNBytes(100000);
		        
		    	double timeStamp1 = 0.0;
				double timeStamp2 = 0.0;
					
				//QRS-Detection
				int indexOfValue = 0;
				int numberOfFoundPoints = 0;
				double meanInterval = 0.0;
				
				List<Integer> valueBuffer = new ArrayList<Integer>();
				//define buffer for maximal QRSDetect delay
				int bufferLength = 20*sampleRate; // seconds*sampleRate = number of buffered data values
				for (int i = 1; i <= bufferLength; i++){ //create a shift register (buffer) with a number of values
					valueBuffer.add(0);	
				}
					    		
		    	if (oseaMethod == 0){//QRSDetec1------------------------------------------------------------
		    		QRSDetector qrsDetector = OSEAFactory.createQRSDetector(sampleRate);	
		    	
		    		byte[] bytes = new byte[2];
				    int numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {
						
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
						indexOfValue = indexOfValue + 1;
						
						valueBuffer.add(0, val);          //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength); //remove oldest value on the right
						int delay = qrsDetector.QRSDet(val); //gives back the delay of preceding QRS peak;
							
						if (delay != 0) {
							numberOfFoundPoints = numberOfFoundPoints +1;
							timeStamp2 = indexOfValue - delay; //-1?
							//System.out.println("A QRS-Complex was detected at sample: " + (timeStamp2));
							if (outputOption == 0) { //XY coordinates
								domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								coordinateY.add((double) valueBuffer.get(delay));
							}
							if (outputOption == 1) { //intervals
								//domainX.add((double) numberOfFoundPoints); //times of beats
								domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
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
				    int numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {
						
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
						indexOfValue = indexOfValue + 1;
						
						valueBuffer.add(0, val);  //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength); //remove oldest value on the right
				
						int delay = qrsDetector.QRSDet(val); //gives back the delay of preceding QRS peak;
							
						if (delay != 0) {
							numberOfFoundPoints = numberOfFoundPoints +1;
							timeStamp2 = indexOfValue - delay; //-1?
							//System.out.println("A QRS-Complex was detected at sample: " + (timeStamp2));
							if (outputOption == 0) { //XY coordinates
								domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								coordinateY.add((double) valueBuffer.get(delay));
							}
							if (outputOption == 1) { //intervals
								//domainX.add((double) numberOfFoundPoints); //times of beats
								domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
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
					int numberOfNormalPeaks   = 0;
					int numberOfPVCPeaks      = 0;
					int numberOfUnknownPeaks  = 0;
					
					BeatDetectionAndClassification bdac = OSEAFactory.createBDAC(sampleRate, sampleRate/2);		
				  	
		    		byte[] bytes = new byte[2];
				    int numbOfReadBytes = fis.read(bytes); 
				    
					while (numbOfReadBytes != -1) {	
						//System.out.println("Plotparser: 16bit data: byte #: " + i + "    :" + bytes[i] );
						int val = ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
						indexOfValue = indexOfValue + 1;
						
						valueBuffer.add(0, val);  //insert new value on the left, this increases the size of buffer +1
						valueBuffer.remove(bufferLength-1); //remove oldest value on the right
						
						BeatDetectAndClassifyResult result = bdac.BeatDetectAndClassify(val);
						int delay = result.samplesSinceRWaveIfSuccess;	
						
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
							numberOfFoundPoints = numberOfFoundPoints +1;
							timeStamp2 = indexOfValue-delay;
//							System.out.println("qrsPosition: " +qrsPosition);
//							System.out.println("timeStamp2: "  +timeStamp2);
//							System.out.println("timeStamp1: "  +timeStamp1);
							if (outputOption == 0) { //XY coordinates
								domainX.add(timeStamp2); //eventually divide by sample rate to get absolute times in s
								coordinateY.add((double) valueBuffer.get(delay));
							}
							if (outputOption == 1) { //intervals
								//domainX.add((double) numberOfFoundPoints); //times of beats
								domainX.add(timeStamp2/sampleRate); //eventually divide by sample rate to get absolute times in s
								intervals.add((timeStamp2 -timeStamp1)/sampleRate); //correction with sample rate to get absolute times in s
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
			
			if (!coordinateY.isEmpty()) {
				//domainX.remove(0);
				//coordinateY.remove(0);
			}
			if (!intervals.isEmpty()) {// eliminate first element because it is too long (osea skips first 8 beats)
				domainX.remove(domainX.size()-1);
				intervals.remove(0);		
			}
			
			// construct the filename
			File newFile = null;
			if (outputOption == 0) {
				newFile = appendTextToPlotName(files[f],"_osea" + String.valueOf(oseaMethod + 1) + "_XYcoordinates");
			}
			if (outputOption == 1) {
				newFile = appendTextToPlotName(files[f],"_osea" + String.valueOf(oseaMethod + 1) + "_intervals");
			}
	
			PlotModel plotModelNew = null;
			if (outputOption == 0) { //coordinates
				if (coordinateY == null || coordinateY.isEmpty()){
					BoardPanel.appendTextln("No coordinates found! Maybe that threshold is not well set");
					DialogUtil.getInstance().showDefaultErrorMessage("No coordinates found! Maybe that threshold is not well set");			
				}
				//PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
				// it is necessary to generate a new instance of PlotModel
				// it is necessary to clone output data for stack processing
				
				String domainHeader = "QRS event time";
				String domainUnit = "";	
				String dataHeader = "QRS event value";
				String dataUnit = "";
				String plotModelName = newFile.toString();
					
				plotModelNew = new PlotModel(domainHeader, domainUnit, dataHeader, dataUnit, 
														(Vector<Double>) domainX.clone(), (Vector<Double>) coordinateY.clone());
				plotModelNew.setModelName(plotModelName);
//				if (this.isCancelled(this.getParentTask()))
//					return null;
//				return new Result(plotModelNew);
				
			}	
			if (outputOption == 1) { //intervals
				if (intervals == null || intervals.isEmpty()){
					BoardPanel.appendTextln("No intervals found! Maybe that threshold is not well set");
					DialogUtil.getInstance().showDefaultErrorMessage("No intervals found! Maybe that threshold is not well set");	
				}
				//PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
				// it is necessary to generate a new instance of PlotModel
				// it is necessary to clone output data for stack processing
				String domainHeader = "QRS event time";
				String domainUnit = "s";	
				String dataHeader = "RR interval";
				String dataUnit = "s";
				String plotModelName = newFile.toString();
					
				plotModelNew = new PlotModel(domainHeader, domainUnit, dataHeader, dataUnit, 
														(Vector<Double>) domainX.clone(), (Vector<Double>) intervals.clone());
				plotModelNew.setModelName(plotModelName);
//				if (this.isCancelled(this.getParentTask()))
//					return null;
//				return new Result(plotModelNew);
			}	

			//saving of a plot goes over a table which is gained from the plot
			plotModels = new ArrayList<PlotModel>();
			plotModels.add(plotModelNew);
			
			this.setTableData(); //sets the table, Umweg über eine table
	
			logger.debug("QRS peaks ["+ newFile.getName()+ "] have been detected, now storing to disk.");

			try {
				//save QRS peaks file 
					
				boolean exportModel = false; //I don't know what an export table should be 
				Object outputObject = null;
					
				if (exportModel) {
					outputObject = TableTools.convertToTabDelimited(table);
				} else {
					outputObject = TableTools.convertToTabDelimited(table);
				}
				
				// write the file according to the content
				String extension = IQMConstants.TXT_EXTENSION;
						
				File destination = newFile;
				TableFileWriter tfw = new TableFileWriter(destination, outputObject, extension);
				tfw.run();
				
				logger.debug("QRS peak times of file ["+ newFile.getName()+ "] have been stored to disk.");

				extractedFiles.add(newFile);

				// Print results
				BoardPanel.appendTextln("File: " + (f + 1) + "/" + files.length);
				//BoardPanel.appendTextln("QRS peaks extracted to: " + newFile);
				if (oseaMethod == 3){
					BoardPanel.appendTextln("Number of normal beats: "+ numNormalBeats); 
					BoardPanel.appendTextln("Number of premature ventricular contraction beats: "+ numPVCBeats); 
					BoardPanel.appendTextln("Number of unknown beats: "+ numUnknownBeats); 
				}
			} catch (Exception e) {
				DialogUtil.getInstance().showErrorMessage(
						"Cannot store the extracted QRS peaks",e, true);
				return false;
			}

		}// files[] loop

		this.setProgress(90);
		return true;
	}

	/**
	 * This method appends String(num) to the image name
	 */
	private File appendTextToPlotName(File file, String strAppend) {
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
			
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								message
										+ "\nDo you want to load the extracted files now?");
				if (selection == IDialogUtil.YES_OPTION) {
					logger.debug("Loading of files selected");
					logger.debug("Loading of files not implemented yet");
					BoardPanel.appendTextln("QRSPeakExtractor: Loading of files not implemented yet");
//					File[] newFiles = new File[this.extractedFiles.size()];
//					for (int i = 0; i < newFiles.length; i++) {
//						newFiles[i] = this.extractedFiles.get(i);
//					}
//					Application.getTank().loadImagesFromHD(newFiles);
					
					//Opening should go over IqmDataBoxes?
//					List<IqmDataBox> itemList =  new ArrayList<IqmDataBox>();
//					Application.getTank().addNewItems(itemList);;
				}
				if (selection == IDialogUtil.NO_OPTION) {
					logger.debug("No loading of files selected");
				}
				this.setProgress(0);
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
