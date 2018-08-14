package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: PEntropy.java
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


import java.util.Vector;

import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

import at.mug.iqm.api.gui.BoardPanel;

/**
 * This class calulates the Permutation entropy according to:
 * <br>
 * Bandt C and Pompe B. Permutation Entropy: A Natural Complexity Measure for Time Series. Phys Rev Lett Vol88(17) 2002
 * 
 * @author Helmut Ahammer
 * @since   2013 02
 *
 */
public class PEntropy {

	private int numbDataPoints = 0; //length of 1D data series

	public PEntropy(){}
	
	/**
	 * This method calculates the permutation entropy
	 * @param data1D 1D data vector
	 * @param m order of permutation entropy;  
	 *        m should not be greater than N/3 (N number of data points)!
	 * @param d delay
	 * @return PEntropy  (sole double value)
	 * 
	 */
	public double calcPEntropy(Vector<Double> data1D, int m, int d){
		numbDataPoints = data1D.size();
		if (m > numbDataPoints/3){
			m = numbDataPoints/3;
			BoardPanel.appendTextln("parameter m too large, automatically set to data length/3");
		}
		if (m < 1){
			BoardPanel.appendTextln("parameter m too small, Sample entropy cannot be calulated");
			return 99999999d;
		}
		if (d < 0){
			BoardPanel.appendTextln("delay too small, Sample entropy cannot be calulated");
			return 999999999d;
		}
	
		//generate permutation patterns
		char[] buffer = new char[m];
		for (int i = 0; i < buffer.length; i++){
			buffer[i] = (char)(i+1);
		}	  
		int[][] permArray = Permutation.allPermutations(m);
			
		double[] counts = new double[permArray.length];
		
//		long  factorial = ArithmeticUtils.factorial(m);
		//System.out.println("PEntropy: permArray.length: "+ permArray.length+ "     factorial: " + factorial);
		
		
		double pEntropy = 0d;
		
		for(int j = 0; j < data1D.size()-d*(m-1); j++){ 			
		
			double[] sample = new double[m];
			for (int s = 0; s <  m; s=s+d){
				sample[s] = data1D.get(j+s);
			}
			NaturalRanking ranking = new NaturalRanking(NaNStrategy.REMOVED, TiesStrategy.SEQUENTIAL);
	        double[] rankOfSample = ranking.rank(sample);
	   
       	
	        //look for matches
	        for(int i = 0; i < permArray.length; i++){
	        	double diff = 0.0;
	        	for (int s =0; s < m; s++){
	        		diff = diff + Math.abs(permArray[i][s]- rankOfSample[s]);
	        	}    		
	        	if (diff == 0){
	        		counts[i] = counts[i] + 1 ;
	        	}
	        }
		}
		
		double sum = 0.0d;
		for(int i = 0; i <counts.length; i++){
			sum = sum + counts[i];
		}
		for(int i = 0; i <counts.length; i++){
			counts[i] = counts[i] / sum;
		}
		sum = 0.0d;
		for(int i = 0; i <counts.length; i++){
			if (counts[i] != 0){
				sum = sum + counts[i] * Math.log(counts[i]);
			}
		}
		pEntropy = -sum;
		return pEntropy;
	}

}
