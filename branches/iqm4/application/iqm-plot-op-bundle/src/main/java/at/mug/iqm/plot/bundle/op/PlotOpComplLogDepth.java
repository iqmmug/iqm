package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpComplLogDepth.java
 * 
 * $Id: PlotOpComplLogDepth.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/op/PlotOpComplLogDepth.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpComplLogDepthDescriptor;

/**
 *  <li>2015 01  according to image implementation (Zenil et al)
 * 
 * @author Ahammer
 * @since  2015 01
 * 
 */
public class PlotOpComplLogDepth extends AbstractOperator {

	public PlotOpComplLogDepth() {
	}

	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D
	 * @return Double Mean
	 */
	private Double calcMean(Vector<Double> data1D) {
		double sum = 0;
		for (double d : data1D) {
			sum += d;
		}
		return sum / data1D.size();
	}
	
	/**
	 * This method calculates and returns compressed signal
	 * @param signal 1D vector
	 * @return byte[] compressed signal
	 */
	private byte[] calcCompressedSignal_ZLIB(Vector<Double> signal) {
		
		 byte[] data = new byte[signal.size() * 8];
		 for (int i = 0; i < signal.size(); i++){
			 long v = Double.doubleToLongBits(signal.get(i));
			 data[i*8+7] = (byte)(v);
			 data[i*8+6] = (byte)(v>>>8);
			 data[i*8+5] = (byte)(v>>>16);
			 data[i*8+4] = (byte)(v>>>24);
			 data[i*8+3] = (byte)(v>>>32);
			 data[i*8+2] = (byte)(v>>>40);
			 data[i*8+1] = (byte)(v>>>48);
			 data[i*8]   = (byte)(v>>>56);
		 }
		 Deflater deflater = new Deflater(); 
		 deflater.setLevel(Deflater.BEST_COMPRESSION);
	     deflater.setInput(data); 
	     ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  

	     deflater.finish(); 
	     byte[] buffer = new byte[1048];  
	     while (!deflater.finished()) { 
	    	 int count = deflater.deflate(buffer); 
	    	 //System.out.println("PlotOpComplLogDepth  Count: " +count);
	         outputStream.write(buffer, 0, count);  
	     } 
	     try {
			 outputStream.close();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } 
	     byte[] output = outputStream.toByteArray(); 
	     deflater.end();
	     //System.out.println("PlotOpComplLogDepth Original: " + data.length  ); 
	     //System.out.println("PlotOpComplLogDepth ZLIB Compressed: " + output.length ); 
	     return output;
	}
	
	/**
	 * This method calculates and returns compressed signal
	 * @param signal 1D vector
	 * @return byte[] compressed signal
	 */
	private byte[] calcCompressedSignal_GZIB(Vector<Double> signal) {
		
		 byte[] data = new byte[signal.size() * 8];
		 for (int i = 0; i < signal.size(); i++){
			 long v = Double.doubleToLongBits(signal.get(i));
			 data[i*8+7] = (byte)(v);
			 data[i*8+6] = (byte)(v>>>8);
			 data[i*8+5] = (byte)(v>>>16);
			 data[i*8+4] = (byte)(v>>>24);
			 data[i*8+3] = (byte)(v>>>32);
			 data[i*8+2] = (byte)(v>>>40);
			 data[i*8+1] = (byte)(v>>>48);
			 data[i*8]   = (byte)(v>>>56);
		 }
		  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        try{
	            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
	            gzipOutputStream.write(data);
	            gzipOutputStream.close();
	        } catch(IOException e){
	            throw new RuntimeException(e);
	        }
	     byte[] output = outputStream.toByteArray(); 
	     try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //System.out.println("PlotOpComplLogDepth Original: " + data.length  ); 
	    //System.out.println("PlotOpComplLogDepth GZIB Compressed: " + output.length ); 
	    return output;
	}
	
	/**
	 * This method decompresses byte array
	 * @param  byte[] array  compressed
	 * @return byte[] array  decompressed
	 */
	private byte[] calcDecompressedSignal_ZLIB(byte[] array) {
		Inflater inflater = new Inflater();   
		inflater.setInput(array);  
		   
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
		byte[] buffer = new byte[1024];  
		while (!inflater.finished()) {  
		    int count = 0;
			try {
				count = inflater.inflate(buffer);
			} catch (DataFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		    outputStream.write(buffer, 0, count);  
		}  
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		byte[] output = outputStream.toByteArray();  		   
		inflater.end();	
	    //System.out.println("PlotOpComplLogDepth ZLIB Input: " + array.length  ); 
	    //System.out.println("PlotOpComplLogDepth Decompressed: " + output.length ); 
	    return output;
	}
	/**
	 * This method decompresses byte array
	 * @param  byte[] array  compressed
	 * @return byte[] array  decompressed
	 */
	private byte[] calcDecompressedSignal_GZIB(byte[] array) {
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
		InputStream in = null;
		try {
			in = new GZIPInputStream(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    byte[] bbuf = new byte[256];
	    while (true) {
	        int r = 0;
			try {
				r = in.read(bbuf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if (r < 0) {
	          break;
	        }
	        buffer.write(bbuf, 0, r);
	    }
		byte[] output = buffer.toByteArray();  		   
		try {
			buffer.close();
			inputStream.close();
			in.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //System.out.println("PlotOpComplLogDepth GZIB Input: " + array.length  ); 
	    //System.out.println("PlotOpComplLogDepth Decompressed: " + output.length ); 
	    return output;
	}
    //---------------------------------------------------------------------------------------------------------------
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel  = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method           = pb.getIntParameter("method");
		int boxLength        = pb.getIntParameter("boxLength");
		int compression      = pb.getIntParameter("compression");
		int iterations       = pb.getIntParameter("iterations");
		int typeSurr         = pb.getIntParameter("typeSurr");
		int nSurr            = pb.getIntParameter("nSurr");

		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());

	    int numValues = 1;
		
		//number of  values	
		if (method == 0)  numValues = 1; // Single value
		if (method == 1)  numValues = signal.size() - boxLength + 1;	//Gliding Box
		
		if ((method == 1) && (numValues < 1)) { // check for gliding method
			DialogUtil.getInstance().showDefaultErrorMessage("Entropies cannot be calculated because gliding box length is too large");
			boxLength = signal.size()/2;
			numValues = signal.size() - boxLength + 1;
			DialogUtil.getInstance().showDefaultErrorMessage("Box length set to: " + boxLength);
			//return null;
		}

		double[] ld = new double[numValues]; // for single value only one element is used
		double[] kc = new double[numValues];
		Vector<Double> ldSurr = new Vector<Double>(); //May be used to store individual surrogate values
		Vector<Double> kcSurr = new Vector<Double>(); //May be used to store individual surrogate values
		double kcSurrMean = 0.0;
		double ldSurrMean = 0.0;
		
		
	    double signalSize   = 8.0 * signal.size(); //Bytes
	    double originalSize = 9999999.9;   //kB
	    
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		if (method == 0) { // single value
			originalSize = signalSize/1024;   //kB
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
					
				//calculate Kolmogorov complexity
				byte[] compressedSignal =  calcCompressedSignal_ZLIB(signal);
				kc[0] =  (double)compressedSignal.length/1024; //[kB]	
			
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (int i = 0; i < iterations; i++){
				    int proz = (int) (i + 1) * 90 / iterations;		
				    fireProgressChanged(proz);    
				    if (isCancelled(getParentTask())){
				    	return null;
				    }  
					long startTime = System.nanoTime();
					byte[] decompressedSignal = calcDecompressedSignal_ZLIB(compressedSignal);
					long time = System.nanoTime();
					stats.addValue((double)(time - startTime));
				}
				//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
				double durationNano = stats.getPercentile(50); //Median			
				ld[0] = durationNano/1000000; //[ms]
	
				fireProgressChanged(95);
				if (isCancelled(getParentTask())) return null;
			}
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
				
				//calculate Kolmogorov complexity
				byte[] compressedSignal =  calcCompressedSignal_GZIB(signal);
				kc[0] =  (double)compressedSignal.length/1024; //[kB]	
			
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (int i = 0; i < iterations; i++){
				    int proz = (int) (i + 1) * 90 / iterations;		
				    fireProgressChanged(proz);    
				    if (isCancelled(getParentTask())){
				    	return null;
				    }  
					long startTime = System.nanoTime();
					byte[] decompressedSignal = calcDecompressedSignal_GZIB(compressedSignal);
					long time = System.nanoTime();
					stats.addValue((double)(time - startTime));
				}
				//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
				double durationNano = stats.getPercentile(50); //Median			
				ld[0] = durationNano/1000000; //[ms]
	
				fireProgressChanged(95);
				if (isCancelled(getParentTask())) return null;
			}
			fireProgressChanged(80);
			if (isCancelled(getParentTask())) return null;
		
			if (typeSurr >= 0) { //Surrogates 	
				for (int n= 1; n <= nSurr; n++) {	
					
					//create a surrogate signal
					Surrogate surrogate = new Surrogate(this);
					Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(signal, typeSurr, 1);
					Vector<Double> signalSurr = plots.get(0);
					
					originalSize = signalSize/1024;   //kB
					if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
							
						//calculate Kolmogorov complexity
						byte[] compressedSignal =  calcCompressedSignal_ZLIB(signalSurr);
						kcSurr.add((double)compressedSignal.length/1024); //[kB]
					
						DescriptiveStatistics stats = new DescriptiveStatistics();
						for (int i = 0; i < iterations; i++){
						    int proz = (int) (i + 1) * 90 / iterations;		
						    fireProgressChanged(proz);    
						    if (isCancelled(getParentTask())){
						    	return null;
						    }  
							long startTime = System.nanoTime();
							byte[] decompressedSignal = calcDecompressedSignal_ZLIB(compressedSignal);
							long time = System.nanoTime();
							stats.addValue((double)(time - startTime));
						}
						//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
						double durationNano = stats.getPercentile(50); //Median
						ldSurr.add(durationNano/1000000); //[ms]
				
						fireProgressChanged(95);
						if (isCancelled(getParentTask())) return null;
					}
					if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
						
							//calculate Kolmogorov complexity
							byte[] compressedSignal =  calcCompressedSignal_GZIB(signalSurr);
							kcSurr.add((double)compressedSignal.length/1024); //[kB]
							
							DescriptiveStatistics stats = new DescriptiveStatistics();
							for (int i = 0; i < iterations; i++){
							    int proz = (int) (i + 1) * 90 / iterations;		
							    fireProgressChanged(proz);    
							    if (isCancelled(getParentTask())){
							    	return null;
							    }  
								long startTime = System.nanoTime();
								byte[] decompressedSignal = calcDecompressedSignal_GZIB(compressedSignal);
								long time = System.nanoTime();
								stats.addValue((double)(time - startTime));
							}
							//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
							double durationNano = stats.getPercentile(50); //Median			
							ldSurr.add(durationNano/1000000); //[ms]
						
							fireProgressChanged(95);
							if (isCancelled(getParentTask())) return null;
					}
		
							fireProgressChanged(80);
							if (isCancelled(getParentTask())) return null;
				} //for loop surrogates
				kcSurrMean = this.calcMean(kcSurr);
				ldSurrMean = this.calcMean(ldSurr);
			}//Surrogates
		}//method == 0 single value
		

		
		if (method == 1) { // gliding values
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numValues; i++) {
					int proz = (int) (i + 1) * 90 / numValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
					}
					originalSize = (double)subSignal.size()/1024;   //kB
					if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
						//calculate Kolmogorov complexity
						byte[] compressedSignal =  calcCompressedSignal_ZLIB(subSignal);
						kc[i] =  (double)compressedSignal.length/1024; //[kB]	
					
						DescriptiveStatistics stats = new DescriptiveStatistics();
						for (int n = 0; n < iterations; n++){    
							long startTime = System.nanoTime();
							byte[] decompressedSignal = calcDecompressedSignal_ZLIB(compressedSignal);
							long time = System.nanoTime();
							stats.addValue((double)(time - startTime));
						}
						//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
						double durationNano = stats.getPercentile(50); //Median			
						ld[i] = durationNano/1000000; //[ms]		
					}
					if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
						//calculate Kolmogorov complexity
						byte[] compressedSignal =  calcCompressedSignal_GZIB(subSignal);
						kc[i] =  (double)compressedSignal.length/1024; //[kB]	
					
						DescriptiveStatistics stats = new DescriptiveStatistics();
						for (int n = 0; n < iterations; n++){    
							long startTime = System.nanoTime();
							byte[] decompressedSignal = calcDecompressedSignal_GZIB(compressedSignal);
							long time = System.nanoTime();
							stats.addValue((double)(time - startTime));
						}
						//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
						double durationNano = stats.getPercentile(50); //Median			
						ld[i] = durationNano/1000000; //[ms]					
					}
				}
			} //no surrogate
			if (typeSurr >= 1) {  //surrogate
				for (int i = 0; i < numValues; i++) {
					int proz = (int) (i + 1) * 90 / numValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
					}
					for (int s= 1; s <= nSurr; s++) {	
						
						//create a surrogate signal
						Surrogate surrogate = new Surrogate(this);
						Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(subSignal, typeSurr, 1);
						Vector<Double> subSignalSurr = plots.get(0);
						
						originalSize = (double)subSignal.size()/1024;   //kB
						if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
							//calculate Kolmogorov complexity
							byte[] compressedSignal =  calcCompressedSignal_ZLIB(subSignalSurr);
							kc[i] +=  (double)compressedSignal.length/1024; //[kB]	
						
							DescriptiveStatistics stats = new DescriptiveStatistics();
							for (int n = 0; n < iterations; n++){    
								long startTime = System.nanoTime();
								byte[] decompressedSignal = calcDecompressedSignal_ZLIB(compressedSignal);
								long time = System.nanoTime();
								stats.addValue((double)(time - startTime));
							}
							//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
							double durationNano = stats.getPercentile(50); //Median			
							ld[i] += durationNano/1000000; //[ms]		
						}
						if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
							//calculate Kolmogorov complexity
							byte[] compressedSignal =  calcCompressedSignal_GZIB(subSignalSurr);
							kc[i] +=  (double)compressedSignal.length/1024; //[kB]	
						
							DescriptiveStatistics stats = new DescriptiveStatistics();
							for (int n = 0; n < iterations; n++){    
								long startTime = System.nanoTime();
								byte[] decompressedSignal = calcDecompressedSignal_GZIB(compressedSignal);
								long time = System.nanoTime();
								stats.addValue((double)(time - startTime));
							}
							//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
							double durationNano = stats.getPercentile(50); //Median			
							ld[i] += durationNano/1000000; //[ms]					
						}
					}
					kc[i] = kc[i]/nSurr;
					ld[i] = ld[i]/nSurr;
					
				}
			} //surrogate
			
		} //method == 1 gliding values
		
		fireProgressChanged(85);
		if (isCancelled(getParentTask())) return null;
		// create model and Table
		TableModel model = new TableModel("ComplLogDepth" + (plotModel.getModelName().equals("") ? "" : " of '" + plotModel.getModelName() + "'"));

		if (method == 0) { // single value
			model.addColumn("Plot name");
			model.addColumn("Surrogate");
			// model.addRow(new String[] {plotModelName, String.valueOf(numK),
			// String.valueOf(regStart), String.valueOf(regEnd) });
			String surrogate = "";
			if (typeSurr == -1) surrogate = "No"; 
			if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
			if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
			if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
			if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
		
			model.addRow(new String[] {plotModelName, surrogate});
		}
		if (method == 1) { // gliding values
			model.addColumn("Plot name (BoxSize=" + boxLength + ")");
			model.addColumn("Surrogate");
			for (int i = 0; i < numValues; i++) {
				// model.addRow(new String[] {"#:"+(i+1), String.valueOf(numK),
				// String.valueOf(regStart), String.valueOf(regEnd) });
				String surrogate = "";
				if (typeSurr == -1) surrogate = "No"; 
				if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
				if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
				if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
				if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
				model.addRow(new String[] { "#:" + (i + 1) , surrogate});
			}
		}
		if (method == 0) { // single value
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
				int numColumns = model.getColumnCount();
				model.addColumn("LogDepth_ZLIB[ms]");
				model.addColumn("KC_ZLIB[kB]");
				model.addColumn("SignalSize[kB]");
				model.setValueAt(ld[0], 0, numColumns);
				model.setValueAt(kc[0], 0, numColumns+1);
				model.setValueAt(originalSize, 0, numColumns+2);
			}
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
				int numColumns = model.getColumnCount();
				model.addColumn("LogDepth_GZIB[ms]");
				model.addColumn("KC_GZIB[kB]");
				model.addColumn("SignalSize[kB]");
				model.setValueAt(ld[0], 0, numColumns);
				model.setValueAt(kc[0], 0, numColumns+1);
				model.setValueAt(originalSize, 0, numColumns+2);
			}
			
			if (typeSurr >= 0) {// KC Mean , LD Mean and for each surrogate 
						
				int numColumns = model.getColumnCount();
				model.addColumn("LD-Surr");
				model.addColumn("KC-Surr");	
				model.setValueAt(ldSurrMean, 0, numColumns);
				model.setValueAt(kcSurrMean, 0, numColumns+1);	
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("LD-Surr"+(x+1));
					model.setValueAt(ldSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("KC-Surr"+(x+1));
					model.setValueAt(kcSurr.get(x),  0, numColumns + x);
				}
			}
		}
		if (method == 1) { // gliding values
			
			
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) {
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) {
					model.addColumn("LogDepth_ZLIB[ms]");
					model.addColumn("KC_ZLIB[kB]");
					model.addColumn("SubsignalSize[kB]");
				}
				else {
					model.addColumn("LogDepth_ZLIB-Surr[ms]");
					model.addColumn("KC_ZLIB-Surr[kB]");
					model.addColumn("SubsignalSize[kB]");
				}		
				for (int i = 0; i < numValues; i++) {
					model.setValueAt(ld[i], i, numColumns);
					model.setValueAt(kc[i], i, numColumns+1);
					model.setValueAt(originalSize, i, numColumns+2);
				}
			}
			if (compression == PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) {
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) {
					model.addColumn("LogDepth_GZIB[ms]");
					model.addColumn("KC_GZIB[kB]");
					model.addColumn("SubsignalSize[kB]");
				}
				else {
					model.addColumn("LogDepth_GZIB-Surr[ms]");
					model.addColumn("KC_GZIB-SurrkB]");
					model.addColumn("SubsignalSize[kB]");
				}
				for (int i = 0; i < numValues; i++) {
					model.setValueAt(ld[i], i, numColumns);
					model.setValueAt(kc[i], i, numColumns+1);
					model.setValueAt(originalSize, i, numColumns+2);
				}
			}
		
		}
		model.fireTableStructureChanged(); // this is mandatory because it
											// updates the table
//		// format column widths
//		int numColumns = model.getColumnCount();
//		for (int i = 0; i < numColumns; i++) {
//			if ((model.getColumnName(i) == "m_ApEn")
//					|| (model.getColumnName(i) == "m_SampEn")
//					|| (model.getColumnName(i) == "n_PEn")
//					|| (model.getColumnName(i) == "r_ApEn")
//					|| (model.getColumnName(i) == "r_SampEn")
//					|| (model.getColumnName(i) == "r_PEn")
//					|| (model.getColumnName(i) == "d_ApEn")
//					|| (model.getColumnName(i) == "d_SampEn")
//					|| (model.getColumnName(i) == "d_PEn")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(10);
//			}
//			if ((model.getColumnName(i) == "ApEn")
//					|| (model.getColumnName(i) == "SampEn")
//					|| (model.getColumnName(i) == "QSE")
//					|| (model.getColumnName(i) == "COSEn")
//					|| (model.getColumnName(i) == "PEn")
//					|| (model.getColumnName(i) == "PEn/log(n!)")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(80);
//			}
//		}

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpComplLogDepthDescriptor().getName();
		}
		return name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpComplLogDepthDescriptor.TYPE;
	}

}
