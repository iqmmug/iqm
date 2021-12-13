package at.mug.iqm.plot.bundle.op;

import java.util.ArrayList;
import java.util.Collections;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpPointFinder.java
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

import java.util.Vector;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.osea4java.OSEAFactory;
import at.mug.iqm.commons.util.osea4java.classification.BeatDetectionAndClassification;
import at.mug.iqm.commons.util.osea4java.classification.ECGCODES;
import at.mug.iqm.commons.util.osea4java.classification.BeatDetectionAndClassification.BeatDetectAndClassifyResult;
import at.mug.iqm.commons.util.osea4java.detection.QRSDetector;
import at.mug.iqm.commons.util.osea4java.detection.QRSDetector2;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.plot.bundle.descriptors.PlotOpPointFinderDescriptor;

/**
 * @author Ahammer
 * @since 2012 11
 * @update 2017 Adam Dolgos added several options 
 * @update 2018-10 HA added QRS peak detections
 */
public class PlotOpPointFinder extends AbstractOperator {

	public PlotOpPointFinder() {
	
	}
	
	//Output variables
	Vector<Double> rangeNew      = new Vector<Double>();
	Vector<Double> coordinateY   = new Vector<Double>();
	Vector<Double> intervals     = new Vector<Double>();
	Vector<Double> heights       = new Vector<Double>();
	Vector<Double> deltaHeights  = new Vector<Double>();
	Vector<Double> energies      = new Vector<Double>();
	Vector<Double> dataX2        = new Vector<Double>(); //for displaying points in extra frame
	Vector<Double> dataY2        = new Vector<Double>(); //for displaying points in extra frame
	
	private void lookingForSlopePointsUsingThresholding(float threshold, int slope, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ){
		
		boolean lookingForThreshold = true;
		int     numberOfFoundPoints = 0;
		double  timeStamp1 = 0.0;
		double  timeStamp2 = 0.0;
		
		if (slope == PlotOpPointFinderDescriptor.SLOPE_POSITIVE) {
			for (int i = 1; i < signal.size() - 1; i++) {
				double value = signal.get(i);

				if (value < threshold) {
					lookingForThreshold = true;
				}
				if (lookingForThreshold) {
					if (value >= threshold) {
						if (signal.get(i + 1) > signal.get(i - 1)) {
							numberOfFoundPoints = numberOfFoundPoints + 1;
							timeStamp2 = rangeOld.get(i);
							if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
								rangeNew.add(timeStamp2);
								coordinateY.add(value);
							}
							if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
								rangeNew.add((double) numberOfFoundPoints);		
								intervals.add(timeStamp2 - timeStamp1);						
							}
							dataX2.add(timeStamp2);
							dataY2.add(value);
							timeStamp1 = timeStamp2;
							timeStamp2 = 0;
						}
						// Threshold found
						lookingForThreshold = false;
					}
				}
				this.fireProgressChanged((int) (i) * 95 / (signal.size()));
				if (this.isCancelled(this.getParentTask())){
					coordinateY = null;
					intervals = null;
			        rangeNew  = null;
					break;
				}
			}
			// eliminate first element because it is almost always too short
			if (!coordinateY.isEmpty()){
				//rangeNew.remove(0);
				//coordinateY.remove(0);
			}
			if (!intervals.isEmpty()){
				rangeNew.remove(rangeNew.size()-1);
				intervals.remove(0);
			}
		}

		if (slope == PlotOpPointFinderDescriptor.SLOPE_NEGATIVE) {
			for (int i = 1; i < signal.size() - 1; i++) {
				double value = signal.get(i);

				if (value > threshold) {
					lookingForThreshold = true;
				}
				if (lookingForThreshold) {
					if (value <= threshold) {
						if (signal.get(i + 1) < signal.get(i - 1)) {
							numberOfFoundPoints = numberOfFoundPoints + 1;
							timeStamp2 = rangeOld.get(i);
							if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
								rangeNew.add(timeStamp2);
								coordinateY.add(value);
							}
							if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
								rangeNew.add((double) numberOfFoundPoints);		
								intervals.add(timeStamp2 - timeStamp1);						
							}
							dataX2.add(timeStamp2);
							dataY2.add(value);
							timeStamp1 = timeStamp2;
							timeStamp2 = 0;
						}
						// Threshold found
						lookingForThreshold = false;
					}
				}
				this.fireProgressChanged((int) (i) * 95 / (signal.size()));
				if (this.isCancelled(this.getParentTask())){
					coordinateY =null;
					intervals   = null;
				    rangeNew    = null;
					break;
				}
			}
			// eliminate first element because it is almost always too short
			if (!coordinateY.isEmpty()){
				//rangeNew.remove(0);
				//coordinateY.remove(0);
			}
			if (!intervals.isEmpty()){
				rangeNew.remove(rangeNew.size()-1);
				intervals.remove(0);
			}
		}
	}
	//-----------------------------------------------------------------------------------------
	private void lookingForSlopePointsUsingMAC(float threshold, int tau, float offset, int intersections, int slope, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ) throws Exception{
		// calculate MAC:
		// int T = 20; // range which is taken for MAC: 2T+1;
		int T = tau; // range which is taken for MAC: 2T+1;

		int L = signal.size();
		double temp = 0, temp2 = 0;
		double[] MAC = new double[signal.size()];

		// if signal is too short for the application of the algorithm
		if ((2 * T) > signal.size())
			throw new Exception("The signal is too short for this algorithm!");

		// equation (1)
		for (int i = 0; i < 2 * T; i++) {
			temp = temp + signal.get(i); // begin (first row (1))
			temp2 = temp2 + signal.get(L - i - 1); // end (last row (1))
		}
		for (int i = 0; i < T; i++) { // mean
			MAC[i] = temp / (2 * T) + offset;
			MAC[L - i - 1] = temp2 / (2 * T) + offset;
		}

		// equation (1) middle part
		MAC[T] = MAC[T - 1] * 2 * T + signal.get(T + T - 1);
		for (int i = T + 1; i < (L - T); i++) {
			MAC[i] = MAC[i - 1] + signal.get(i + T) - signal.get(i - T - 1);
		}
		for (int i = T; i < (L - T); i++) { // mean
			MAC[i] = MAC[i] / (2 * T + 1) + offset;
		}

		// ------------------------------------------------------------------------------
		// Find Intersections:
		Vector<Integer> intersect_up = new Vector<Integer>();
		Vector<Integer> intersect_down = new Vector<Integer>();
		Vector<Double> maxVec = new Vector<Double>();
		Vector<Double> minVec = new Vector<Double>();

		int last_intersect = 0;
		int numberOfFoundIntervals = 0;

		for (int i = 0; i < signal.size() - 1; i++) {
			double value_t = signal.get(i);
			double value_t1 = signal.get(i + 1);

			if ((value_t <= MAC[i]) && (value_t1 >= MAC[i + 1])) {
				if (last_intersect == 1) { // if there are two+ consecutive up intersects ... overwrite the first one
					intersect_up.set(intersect_up.size() - 1, i);
				} // end if
				else {
					intersect_up.add(i);
				} // end else
				last_intersect = 1;
			} else if ((value_t >= MAC[i]) && (value_t1 <= MAC[i + 1])) {
				if (last_intersect == -1) { // if there are two+ consecutive up intersects ... overwrite the first one
					intersect_down.set(intersect_down.size() - 1, i);
				} // end if
				else {
					intersect_down.add(i);
				} // end else
				last_intersect = -1;
			}

			this.fireProgressChanged((int) (i) * 95 / (signal.size()));
			if (this.isCancelled(this.getParentTask()))
				break;
		} // end for

		// ------------------------------------------------------------------------------
		// Find Maxima/Minima between intersections:
		int fromIndex, toIndex;
		// ------------------------------------------------------------------------------
		// find Maxima:
		for (int i = 0; i < intersect_up.size(); i++) {
			if (i < intersect_down.size()) {
				fromIndex = intersect_up.get(i);
				toIndex = intersect_down.get(i);
				double max = -Double.MAX_VALUE;
				maxVec.add(rangeOld.get(fromIndex));
				for (int j = fromIndex; j < toIndex; j++) {
					if (signal.get(j) > max) {
						maxVec.set(i, rangeOld.get(j));
						max = signal.get(j);
					} // end if
				} // end for
			} // end if
		} // end for
			// ------------------------------------------------------------------------------
			// find Minima:
		for (int i = 0; i < intersect_down.size(); i++) {
			if (i < intersect_up.size()) {
				fromIndex = intersect_down.get(i);
				toIndex = intersect_up.get(i);
				double min = Double.MAX_VALUE;
				minVec.add(rangeOld.get(fromIndex));
				for (int j = fromIndex; j < toIndex; j++) {
					if (signal.get(j) < min) {
						minVec.set(i, rangeOld.get(j));
						min = signal.get(j);
					} // end if
				} // end for
			} // end if
		} // end for

		// intervals between maxima/minima:
		if (intersections == 0) {
			// ------------------------------------------------------------------------------
			// Find Intervals (distance between Maxima/Minima):
			if (slope == PlotOpPointFinderDescriptor.SLOPE_POSITIVE) { // Intervals by peaks find intervals:
				numberOfFoundIntervals = maxVec.size() - 1;
				for (int i = 0; i < numberOfFoundIntervals; i++) {
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
						rangeNew.add(maxVec.get(i + 1));
						coordinateY.add(signal.get(i + 1));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
						rangeNew.add((double) i + 1);				
						intervals.add(maxVec.get(i + 1) - maxVec.get(i));
					}
					dataX2.add(maxVec.get(i + 1));
					dataY2.add(signal.get(i + 1));
				} // end for
			} // end if
			else if (slope == PlotOpPointFinderDescriptor.SLOPE_NEGATIVE) { // Intervals by valleys find intervals:
				numberOfFoundIntervals = minVec.size() - 1;
				for (int i = 0; i < numberOfFoundIntervals; i++) {	
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
						rangeNew.add(minVec.get(i + 1));
						coordinateY.add(signal.get(i + 1));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {					
						rangeNew.add((double) i + 1);		
						intervals.add(minVec.get(i + 1) - minVec.get(i));
					}			
					dataX2.add(minVec.get(i + 1));
					dataY2.add(signal.get(i + 1));
				} // end for
			} // end else if
		} // end if
			// intervals between intersections of signal and MAC:
		else if (intersections == 1) {
			// ------------------------------------------------------------------------------
			// Find Intervals (distance between Intersections):
			if (slope == PlotOpPointFinderDescriptor.SLOPE_POSITIVE) { // Intervals between up-intersection find intervals:
				numberOfFoundIntervals = intersect_up.size() - 1;
				for (int i = 0; i < numberOfFoundIntervals; i++) {
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
						rangeNew.add(rangeOld.get(intersect_up.get(i + 1)));
						coordinateY.add(signal.get(intersect_up.get(i + 1)));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {	
						rangeNew.add((double) i + 1);
						intervals.add(rangeOld.get(intersect_up.get(i + 1)) - rangeOld.get(intersect_up.get(i)));					
					}			
					dataX2.add(rangeOld.get(intersect_up.get(i + 1)));
					//dataY2.add(signal.get(intersect_up.get(i + 1)) - signal.get(intersect_up.get(i)));
					dataY2.add(signal.get(intersect_up.get(i + 1)));
								} // end for
			} // end if
			else if (slope == PlotOpPointFinderDescriptor.SLOPE_NEGATIVE) { // Intervals between down-intersections find intervals:
				numberOfFoundIntervals = intersect_down.size() - 1;
				for (int i = 0; i < numberOfFoundIntervals; i++) {			
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {						
						rangeNew.add(rangeOld.get(intersect_down.get(i + 1)));
						coordinateY.add(signal.get(intersect_down.get(i + 1)));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {					
						rangeNew.add((double) i + 1);
						intervals.add(rangeOld.get(intersect_down.get(i + 1)) - rangeOld.get(intersect_down.get(i)));					
					}								
					dataX2.add(rangeOld.get(intersect_down.get(i + 1)));
					//dataY2.add(signal.get(intersect_down.get(i + 1)) - signal.get(intersect_down.get(i)));
					dataY2.add(signal.get(intersect_down.get(i + 1)));
				} // end for
			} // end else if
		} // end else if

	} // end if method MAC
	// ------------------------------------------------------------------------------
	/**
	 * Looking for Peaks
	 * 2917-07-Adam Dolgos
	 * A peak is searched for all values that are higher than the threshold and are in between two values that are equal to the threshold
	 * The last peak of a signal may be lost because the threshold value may not be reached any more.
	 */
	private void lookingForPeakPoints(float threshold, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ) {
	
		boolean lookingForPeak = false;
		boolean first = true;
		int     numberOfFoundPoints = 0;
		double  timeStamp1 = rangeOld.get(0);
		double  timeStamp2 = rangeOld.get(0);
		double  median = 0;
		double  localMax = signal.get(0);
		double  currentValue = signal.get(0);
		ArrayList<Double> valuesOfOneInterval = new ArrayList<Double>();
		
			for (int i = 1; i < signal.size() - 1; i++) {
				double value = signal.get(i);

				if (value > threshold) {
					lookingForPeak = true; // looking for peak
					if (signal.get(i + 1) < signal.get(i) && signal.get(i - 1) <= signal.get(i)) {

						if (first) { //first maximum over the threshold
							first = false;
							timeStamp2 = rangeOld.get(i);
							localMax = signal.get(i);
						}
						currentValue = signal.get(i);

						if (currentValue > localMax) {
							timeStamp2 = rangeOld.get(i);
							localMax = currentValue;
						}
					}
				}
				if (lookingForPeak) {

					if (value < threshold && timeStamp2 != 0) {  //After being a while higher than the threshold it is again now under the threshold and we are locally ready 

						numberOfFoundPoints = numberOfFoundPoints + 1;

						
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
							rangeNew.add(timeStamp2);
							coordinateY.add(localMax);
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
							rangeNew.add((double) numberOfFoundPoints);
							//intervals.add((stamp2 - stamp1) * samplingInterval);
							intervals.add(timeStamp2 -timeStamp1);
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_HEIGHTS) {
							// looking for baseline
							// median is a good choice if there is a longer baseline
							// for short baselines this may fail
							for (int n = (int) rangeOld.indexOf(timeStamp1); n < rangeOld.indexOf(timeStamp2); n++) {
								valuesOfOneInterval.add(signal.get(n));
							}
							if (valuesOfOneInterval.size() > 0) {
								Collections.sort(valuesOfOneInterval);
								int middleOfInterval = (valuesOfOneInterval.size()) / 2;
								median = 0;
								if (valuesOfOneInterval.size() % 2 == 0) {
									median = (valuesOfOneInterval.get(middleOfInterval)
											+ valuesOfOneInterval.get(middleOfInterval - 1)) / 2;
								} else {
									median = valuesOfOneInterval.get(middleOfInterval);
								}
							}
							rangeNew.add((double) numberOfFoundPoints);
							heights.add(signal.get(rangeOld.indexOf(timeStamp2)) - median);

						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_ENERGIES) {
							// looking for baseline
							// median is a good choice if there is a longer baseline
							// for short baselines this may fail
							for (int n = (int) rangeOld.indexOf(timeStamp1); n < rangeOld.indexOf(timeStamp2); n++) {
								valuesOfOneInterval.add(signal.get(n));
							}
							if (valuesOfOneInterval.size() > 0) {
								Collections.sort(valuesOfOneInterval);

								int middleOfInterval = (valuesOfOneInterval.size()) / 2;
								median = 0;
								if (valuesOfOneInterval.size() % 2 == 0) {
									median = (valuesOfOneInterval.get(middleOfInterval)
											+ valuesOfOneInterval.get(middleOfInterval - 1)) / 2;
								} else {
									median = valuesOfOneInterval.get(middleOfInterval);
								}
							}
							rangeNew.add((double) numberOfFoundPoints);
							energies.add((timeStamp2 - timeStamp1)
									* ((signal.get(rangeOld.indexOf(timeStamp2)) - median) * (signal.get(rangeOld.indexOf( timeStamp2)) - median))); //interval*hight^2
							//	peakEnergies.add(((stamp2 - stamp1) * samplingInterval) * ((signal.get((int) stamp2) - median)));
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_DELTAHEIGHTS) {
							rangeNew.add((double) numberOfFoundPoints);
							deltaHeights.add((signal.get(rangeOld.indexOf(timeStamp2))) - (signal.get(rangeOld.indexOf(timeStamp1))));
						}

						dataX2.add(timeStamp2);
						dataY2.add(localMax);
						timeStamp1 = timeStamp2;
						timeStamp2 = 0;

						valuesOfOneInterval.clear();

						lookingForPeak = false;
						first = true;

					} else {
						lookingForPeak = true;
					}
				}

				this.fireProgressChanged((int) (i) * 95 / (signal.size()));
				if (this.isCancelled(this.getParentTask())){
					rangeNew         = null;
					coordinateY      = null;
					intervals        = null;
					heights      = null;
					deltaHeights = null;
					energies     = null;
					break;
				}
			}		
			if (!coordinateY.isEmpty()) {
				//rangeNew.remove(0);
				//coordinateY.remove(0);
				//dataX2.remove(0);
				//dataY2.remove(0);
			}
			if (!intervals.isEmpty()) {// eliminate first element because it is almost always too short
				rangeNew.remove(rangeNew.size()-1);
				intervals.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}
			if (!heights.isEmpty()) {
//				rangeNew.remove(rangeNew.size()-1);
//				heights.remove(0);
//				dataX2.remove(0);
//				dataY2.remove(0);
			}
			if (!deltaHeights.isEmpty()) { //eliminate first entry because it is wrong (delta of the first point in the signal)
				rangeNew.remove(rangeNew.size()-1);
				deltaHeights.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}
			if (!energies.isEmpty()) {
				rangeNew.remove(rangeNew.size()-1);
				energies.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}	
	}
	// ------------------------------------------------------------------------------
	/**
	 * Looking for Valleys
	 * 2917-07-Adam Dolgos
	 * A valley is searched for all values that are lower than the threshold and are in between two values that are equal to the threshold
	 * The last peak of a signal may be lost because the threshold value may not be reached any more.
	 */
	private void lookingForValleyPoints(float threshold, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ) {
		
		boolean lookingForValley = false;
		boolean first = true;
		int     numberOfFoundPoints = 0;
		double  timeStamp1 = rangeOld.get(0);
		double  timeStamp2 = rangeOld.get(0);
		double  median = 0;
		double  localMin = signal.get(0);
		double  currentValue = signal.get(0);	
	    ArrayList<Double> valuesOfOneInterval = new ArrayList<Double>();

		for (int i = 1; i < signal.size() - 1; i++) {

			double value = signal.get(i);

			if (value < threshold) {
				lookingForValley = true; // looking for valley
				if (signal.get(i + 1) > signal.get(i) && signal.get(i - 1) >= signal.get(i)) { 

					if (first) { //first minimum under the threshold
						first = false;
						timeStamp2 = rangeOld.get(i);
						localMin = signal.get(i);
					}
					currentValue = signal.get(i);

					if (currentValue < localMin) {
						timeStamp2 = rangeOld.get(i);	
						localMin = currentValue;
					}
				}
			}
			if (lookingForValley) {
				if (value > threshold && timeStamp2 != 0) { //After being a while lower than the threshold it is again now over the threshold and we are locally ready 

					numberOfFoundPoints = numberOfFoundPoints + 1;

					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
						rangeNew.add(timeStamp2);
						coordinateY.add(localMin);
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
						rangeNew.add((double) numberOfFoundPoints);
						//intervals.add((stamp2 - stamp1) * samplingInterval);
						intervals.add((timeStamp2 - timeStamp1));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_HEIGHTS) {
						// looking for baseline
						// median is a good choice if there is a longer baseline
						// for short baselines this may fail
						for (int n = (int) rangeOld.indexOf(timeStamp1); n < rangeOld.indexOf(timeStamp2); n++) {
							valuesOfOneInterval.add(signal.get(n));
						}
						if (valuesOfOneInterval.size() > 0) {
							Collections.sort(valuesOfOneInterval);
							int middleOfInterval = (valuesOfOneInterval.size()) / 2;
							median = 0;
							if (valuesOfOneInterval.size() % 2 == 0) {
								median = (valuesOfOneInterval.get(middleOfInterval)
										+ valuesOfOneInterval.get(middleOfInterval - 1)) / 2;
							} else {
								median = valuesOfOneInterval.get(middleOfInterval);
							}
						}
						rangeNew.add((double) numberOfFoundPoints);
						heights.add(signal.get(rangeOld.indexOf(timeStamp2)) - median);

					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_ENERGIES) {
						// looking for baseline
						// median is a good choice if there is a longer baseline
						// for short baselines this may fail
						for (int n = (int) rangeOld.indexOf(timeStamp1); n < rangeOld.indexOf(timeStamp2); n++) {
							valuesOfOneInterval.add(signal.get(n));
						}
						if (valuesOfOneInterval.size() > 0) {
							Collections.sort(valuesOfOneInterval);

							int middleOfInterval = (valuesOfOneInterval.size()) / 2;
							median = 0;
							if (valuesOfOneInterval.size() % 2 == 0) {
								median = (valuesOfOneInterval.get(middleOfInterval)
										+ valuesOfOneInterval.get(middleOfInterval - 1)) / 2;
							} else {
								median = valuesOfOneInterval.get(middleOfInterval);
							}
						}
						rangeNew.add((double) numberOfFoundPoints);
						energies.add((timeStamp2 - timeStamp1)
								* ((signal.get(rangeOld.indexOf(timeStamp2)) - median) * (signal.get(rangeOld.indexOf( timeStamp2)) - median))); //interval*hight^2
						//	peakEnergies.add(((stamp2 - stamp1) * samplingInterval) * ((signal.get((int) stamp2) - median)));
					}
					if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_DELTAHEIGHTS) {
						rangeNew.add((double) numberOfFoundPoints);
						deltaHeights.add((signal.get(rangeOld.indexOf(timeStamp2))) - (signal.get(rangeOld.indexOf(timeStamp1))));
					}
					
					dataX2.add(timeStamp2);
					dataY2.add(localMin);
					timeStamp1 = timeStamp2;
					timeStamp2 = 0;

					valuesOfOneInterval.clear();

					lookingForValley = false;
					first = true;

				} else {
					lookingForValley = true;
				}
			}
			this.fireProgressChanged((int) (i) * 95 / (signal.size()));
			if (this.isCancelled(this.getParentTask())){
				rangeNew     = null;
				coordinateY  = null;
			    intervals    = null;
			    heights      = null;
			    deltaHeights = null;
			    energies     = null;
				break;
			}
		}	
		if (!coordinateY.isEmpty()) {
			//rangeNew.remove(0);	
			//coordinateY.remove(0);
			//dataX2.remove(0);
			//dataY2.remove(0);
		}
		if (!intervals.isEmpty()) {// eliminate first element because interval is almost always too short
			rangeNew.remove(rangeNew.size()-1);
			intervals.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}
		if (!heights.isEmpty()) {
//			rangeNew.remove(rangeNew.size()-1);
//			heights.remove(0);
//			dataX2.remove(0);
//			dataY2.remove(0);
		}
		if (!deltaHeights.isEmpty()) {//eliminate first entry because it is wrong (delta of the first point in the signal)
			rangeNew.remove(rangeNew.size()-1);
			deltaHeights.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}
		if (!energies.isEmpty()) {
			rangeNew.remove(rangeNew.size()-1);
			energies.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}	
	}
	//------------------------------------------------------------------------------------------------------------------------
	/**
	 * Looking for QRS Peaks
	 * 2018-10 HA according to Chen, H.C., Chen, S.W., 2003. A moving average based filtering system with its application to real-time QRS detection, in: Computers in Cardiology, 2003. Presented at the Computers in Cardiology, 
	 * 													2003, pp. 585–588. https://doi.org/10.1109/CIC.2003.1291223
	 *
	 * adapted  float[] to vectors and floats doubles
	 */
	private void lookingForQRSPointsChen(int M, int sumInterval, int peakFrame, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ) {
	
		double timeStamp1 = 0.0;
		double timeStamp2 = 0.0;
			
	    //int M = 5; //default 5 for highPass should be 3,5,7
	    //int sumInterval = 19; //default 30 for lowPass should be about 150ms (15 points for 100Hz, 30 points for 200Hz) 
	    //int peakFrame = 150; //default 250
	    
		Vector<Double> highPass = highPass(signal,  M);
		Vector<Double> lowPass = lowPass(highPass, sumInterval);
		int[] QRSpeaks = QRS(lowPass, peakFrame); //gives back 1 for a peak, otherwise zero. 
		
		
//		dataX2 = rangeOld;
//		dataY2 = highPass;
//		this.rangeNew = dataX2;
//		this.coordinateY = dataY2;

		
		int numberOfFoundPoints = 0;
		for (int i=0; i < signal.size(); i++) {
			if (QRSpeaks[i]	== 1) {	
				numberOfFoundPoints = numberOfFoundPoints +1;
				timeStamp2 = rangeOld.get(i);
				if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
					rangeNew.add(timeStamp2);
					coordinateY.add(signal.get(i));
				}
				
				if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
					rangeNew.add((double) numberOfFoundPoints);
					intervals.add(timeStamp2 -timeStamp1);
				}						
				dataX2.add(timeStamp2);
				dataY2.add(signal.get(i));
				timeStamp1 = timeStamp2;
				timeStamp2 = 0.0;
			}
		
			this.fireProgressChanged((int) (i) * 95 / (signal.size()));
			if (this.isCancelled(this.getParentTask())){
				rangeNew         = null;
				coordinateY      = null;
				intervals        = null;
				heights      = null;
				deltaHeights = null;
				energies     = null;
				break;
			}
		}				
		
		if (!coordinateY.isEmpty()) {
			//rangeNew.remove(0);
			//coordinateY.remove(0);
			//dataX2.remove(0);
			//dataY2.remove(0);
		}
		if (!intervals.isEmpty()) {// eliminate first element because it is almost always too long
			rangeNew.remove(rangeNew.size()-1);
			intervals.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}
		if (!heights.isEmpty()) {
//			rangeNew.remove(rangeNew.size()-1);
//			heights.remove(0);
//			dataX2.remove(0);
//			dataY2.remove(0);
		}
		if (!deltaHeights.isEmpty()) { //eliminate first entry because it is wrong (delta of the first point in the signal)
			rangeNew.remove(rangeNew.size()-1);
			deltaHeights.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}
		if (!energies.isEmpty()) {
			rangeNew.remove(rangeNew.size()-1);
			energies.remove(0);
			dataX2.remove(0);
			dataY2.remove(0);
		}	
	}
	//-------------------------------------------------------------------------------------------------------------
	 //===============High Pass Filter================================
	 // M Window size， 5 or 7
	 // y1: M
	 // y2:Group delay (M+1/2)
	 //HA adapted float[] to Vector
	 
	 public Vector<Double> highPass(Vector<Double> sig, int M) { //nsamp: data
	        Vector<Double> highPass = new Vector<Double>();
	        int nsamp = sig.size();
	        float constant = (float) 1/M;
	 
	        for(int i=0; i<sig.size(); i++) { //sig: input data array
	            double y1 = 0;
	            double y2 = 0;
	 
	            int y2_index = i-((M+1)/2);
	            if(y2_index < 0) { //array
	                y2_index = nsamp + y2_index;
	            }
	            y2 = sig.get(y2_index);
	 
	            float y1_sum = 0;
	            for(int j=i; j>i-M; j--) {
	                int x_index = i - (i-j);
	                if(x_index < 0) {
	                    x_index = nsamp + x_index;
	                }
	                y1_sum += sig.get(x_index);
	            }
	 
	            y1 = constant * y1_sum; //constant = 1/M
	            //highPass.set(i, y2 - y1); 
	            highPass.add(i, y2 - y1); 
	        }         
	        return highPass;
	    }
	
	 //============Low pass filter==================
	 //Non Linear
	 public static Vector<Double> lowPass(Vector<Double> sig, int sumInterval) {
		  	int nsamp = sig.size();
		 	Vector<Double> lowPass = new Vector<Double>();
	        for(int i=0; i<sig.size(); i++) {
	            double sum = 0.0;
	            if(i+sumInterval < sig.size()) {
	                for(int j=i; j<i+sumInterval; j++) {
	                    double current = sig.get(j) * sig.get(j); //
	                    sum += current; //
	                }
	            }
	            else if(i+sumInterval >= sig.size()) { //array
	                int over = i+sumInterval - sig.size(); 
	                for(int j=i; j<sig.size(); j++) {
	                    double current = sig.get(j) * sig.get(j);
	                    sum += current;
	                }
	                //?? over<0
	                for(int j=0; j<over; j++) {
	                    double current = sig.get(j) * sig.get(j);
	                    sum += current;
	                }
	            }
	 
	            lowPass.add(i, sum);
	        }
	 
	        return lowPass;
	 
	    }
	 
	 //=================QRS Detection================
	 //beat seeker
	 //alpha: forgetting factor 0.001 - 0.1
	 //Gama: weighting factor 0.15 - 0.2 
	 
	 public static int[] QRS(Vector<Double> lowPass, int peakFrame) {
	        int[] QRS = new int[lowPass.size()];
	 
	        double treshold = 0;
	 
	        //Threshold
	        for(int i=0; i<peakFrame; i++) {
	            if(lowPass.get(i) > treshold) {
	                treshold = lowPass.get(i);
	            }
	        }
	 
	        //int frame = 250; //window size 250 PEAK
	        int frame = peakFrame;
	    		        
	        for(int i=0; i<lowPass.size(); i+=frame) { //250
	            double max = 0;
	            int index = 0;
	            if(i + frame > lowPass.size()) { //
	                index = lowPass.size();
	            }
	            else {
	                index = i + frame;
	            }
	            for(int j=i; j<index; j++) {
	                if(lowPass.get(j) > max) max = lowPass.get(j); //250data
	            }
	            boolean added = false;
	            for(int j=i; j<index; j++) {
	                if(lowPass.get(j) > treshold && !added) {
	                    QRS[j] = 1; //250 (0.5)
	                    			//real time frame1
	                    added = true;
	                }
	                else {
	                    QRS[j] = 0;
	                }
	            }	 
	            double gama = (Math.random() > 0.5) ? 0.15 : 0.20;
	            double alpha = 0.01 + (Math.random() * ((0.1 - 0.01)));
	 
	            treshold = alpha * gama * max + (1 - alpha) * treshold;	 
	        }
	 
	        return QRS;
	    }
	
	 
		//------------------------------------------------------------------------------------------------------------------------
		/**
		 * Looking for QRS Peaks
		 * 2018-10 HA according OSEA
		 *
		 */
		private void lookingForQRSPointsOsea(int oseaMethod, int sampleRate, int outputoptions, Vector<Double> rangeOld, Vector<Double> signal ) {
		
			double timeStamp1 = 0.0;
			double timeStamp2 = 0.0;
				
			//QRS-Detection
			int numberOfFoundPoints = 0;
			double meanInterval = 0.0;
			
			//int sampleRate = 125; //
			//int oseaMethod = 2;  //0...QRSDetector, 1...QRSDetector2,  2....BeatDetectionAndClassify
			
			//Method1-QRSDetector----------------------------------------------------------------------------
			//Hamilton, Tompkins, W. J., "Quantitative investigation of QRS detection rules using the MIT/BIH arrhythmia database",IEEE Trans. Biomed. Eng., BME-33, pp. 1158-1165, 1987.
			//using medians
			if (oseaMethod == PlotOpPointFinderDescriptor.OSEA_QRSDETECTION){
				QRSDetector qrsDetector = OSEAFactory.createQRSDetector(sampleRate);	
				for (int i = 0; i < signal.size(); i++) {
					int result = qrsDetector.QRSDet(signal.get(i).intValue());
					
					if (result != 0) {
					    //System.out.println("A QRS-Complex was detected at sample: " + (i-result));
						numberOfFoundPoints = numberOfFoundPoints +1;
						timeStamp2 = rangeOld.get(i-result);
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
							rangeNew.add(timeStamp2);//eventually divide by sample rate to get absolute times in s
							coordinateY.add(signal.get(i-result));
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
							rangeNew.add((double) numberOfFoundPoints);
							intervals.add((timeStamp2 -timeStamp1)/sampleRate);
							meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
						}				
						dataX2.add(timeStamp2);
						dataY2.add(signal.get(i-result));
						timeStamp1 = timeStamp2;
						timeStamp2 = 0.0;				
					}
			
					//this.fireProgressChanged((int) (i) * 95 / (signal.size()));
					if (this.isCancelled(this.getParentTask())){
						rangeNew         = null;
						coordinateY      = null;
						intervals        = null;
						heights      = null;
						deltaHeights = null;
						energies     = null;
						break;
					}
		    	}
				BoardPanel.appendTextln("Number of detected QRS complexes using QRSDetect: "+ numberOfFoundPoints);
				if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
					meanInterval = meanInterval/numberOfFoundPoints;
					BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
					BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
				}
			}//-----------------------------------------------------------------------------------------------
			//Method2-QRSDetector2----------------------------------------------------------------------------
			//Hamilton, Tompkins, W. J., "Quantitative investigation of QRS detection rules using the MIT/BIH arrhythmia database",IEEE Trans. Biomed. Eng., BME-33, pp. 1158-1165, 1987.
			//using means
			if (oseaMethod == PlotOpPointFinderDescriptor.OSEA_QRSDETECTION2){
				QRSDetector2 qrsDetector = OSEAFactory.createQRSDetector2(sampleRate);	
				for (int i = 0; i < signal.size(); i++) {
					int result = qrsDetector.QRSDet(signal.get(i).intValue());
					
					if (result != 0) {
					    //System.out.println("A QRS-Complex was detected at sample: " + (i-result));
						numberOfFoundPoints = numberOfFoundPoints +1;
						timeStamp2 = rangeOld.get(i-result);
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
							rangeNew.add(timeStamp2);//eventually divide by sample rate to get absolute times in s
							coordinateY.add(signal.get(i-result));
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
							rangeNew.add((double) numberOfFoundPoints);
							intervals.add((timeStamp2 -timeStamp1)/sampleRate);
							meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
						}					
						dataX2.add(timeStamp2);
						dataY2.add(signal.get(i-result));
						timeStamp1 = timeStamp2;
						timeStamp2 = 0.0;				
					}
				
					//this.fireProgressChanged((int) (i) * 95 / (signal.size()));
					if (this.isCancelled(this.getParentTask())){
						rangeNew         = null;
						coordinateY      = null;
						intervals        = null;
						heights      = null;
						deltaHeights = null;
						energies     = null;
						break;
					}
		    	}
				BoardPanel.appendTextln("Number of detected QRS complexes using QRSDetect2: "+ numberOfFoundPoints);
				if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
					meanInterval = meanInterval/numberOfFoundPoints;
					BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
					BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
				}
			}//------------------------------------------------------------------------------------------------------
			
			//Method3-detection and classification--------------------------------------------------------------------
			//QRSDetector2 for detection
			if (oseaMethod == PlotOpPointFinderDescriptor.OSEA_QRSBEATDETECTIONANDCLASSIFY){
				int numberOfNormalPeaks   = 0;
				int numberOfPVCPeaks      = 0;
				int numberOfUnknownPeaks  = 0;
				BeatDetectionAndClassification bdac = OSEAFactory.createBDAC(sampleRate, sampleRate/2);		
				for (int i = 0; i < signal.size(); i++) {
					BeatDetectAndClassifyResult result = bdac.BeatDetectAndClassify(signal.get(i).intValue());
						
					if (result.samplesSinceRWaveIfSuccess != 0) {
							int qrsPosition =  i - result.samplesSinceRWaveIfSuccess;
	
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
						timeStamp2 = rangeOld.get(qrsPosition);
						//System.out.println("qrsPosition: " +qrsPosition);
						//System.out.println("timeStamp2: "  +timeStamp2);
						//System.out.println("timeStamp1: "  +timeStamp1);
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
							rangeNew.add(timeStamp2);//eventually divide by sample rate to get absolute times in s
							coordinateY.add(signal.get(qrsPosition));
						}
						if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
							rangeNew.add((double) numberOfFoundPoints);
							intervals.add((timeStamp2 -timeStamp1)/sampleRate);
							meanInterval += (timeStamp2 -timeStamp1)/sampleRate;
						}							
						dataX2.add(timeStamp2);
						dataY2.add(signal.get(qrsPosition));	
						timeStamp1 = timeStamp2;
						timeStamp2 = 0.0;
					}			
						//this.fireProgressChanged((int) (i) * 95 / (signal.size()));
					if (this.isCancelled(this.getParentTask())){
						rangeNew         = null;
						coordinateY      = null;
						intervals        = null;
						heights      = null;
						deltaHeights = null;
						energies     = null;
						break;
					}
					//--------------------------------------------------------------------------------------
				}
				BoardPanel.appendTextln("Number of detected QRS complexes using BeatDetectAndClassify: "+ numberOfFoundPoints);
				BoardPanel.appendTextln("Number of normal QRS peaks: "+ numberOfNormalPeaks);
				BoardPanel.appendTextln("Number of premature ventricular contractions: "+ numberOfPVCPeaks);
				BoardPanel.appendTextln("Number of unknown QRS peaks: "+ numberOfUnknownPeaks);
				if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
					meanInterval = meanInterval/numberOfFoundPoints;
					BoardPanel.appendTextln("Mean RR interval: "+ meanInterval*1000 + " [ms]");
					BoardPanel.appendTextln("Mean HR: "+ 1.0/meanInterval*60 + " [1/min]");
				}
			}//Method 3-------------------------------------------------------------------------------------
			
			if (!coordinateY.isEmpty()) {
				//rangeNew.remove(0);
				//coordinateY.remove(0);
				//dataX2.remove(0);
				//dataY2.remove(0);
			}
			if (!intervals.isEmpty()) {// eliminate first element because it is too long (osea skips first 8 beats)
				rangeNew.remove(rangeNew.size()-1);
				intervals.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}
			if (!heights.isEmpty()) {
//				rangeNew.remove(rangeNew.size()-1);
//				heights.remove(0);
//				dataX2.remove(0);
//				dataY2.remove(0);
			}
			if (!deltaHeights.isEmpty()) { //eliminate first entry because it is wrong (delta of the first point in the signal)
				rangeNew.remove(rangeNew.size()-1);
				deltaHeights.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}
			if (!energies.isEmpty()) {
				rangeNew.remove(rangeNew.size()-1);
				energies.remove(0);
				dataX2.remove(0);
				dataY2.remove(0);
			}	
		}
	 
	 
	 
	//-------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("null")
	@Override
	public IResult run(IWorkPackage wp) throws Exception {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method         = pb.getIntParameter("method");    //Slope  Peaks  Valleys
		int options        = pb.getIntParameter("options");   //Threshold  MAC
		int thres          = pb.getIntParameter("threshold"); //Threshold value
		int tau            = pb.getIntParameter("tau");       //tau value for MAC
		int offS           = pb.getIntParameter("offset");    //offset value for MAC
		int scaledown      = pb.getIntParameter("scaledown"); //for threshold or MAC
		int slope          = pb.getIntParameter("slope");
		int m              = pb.getIntParameter("chenm");       //QRSPeaks Chen&Chen highpass filter parameter
		int sumInterval    = pb.getIntParameter("suminterval"); //QRSPeaks Chen&Chen lowpass filter parameter
		int peakFrame      = pb.getIntParameter("peakframe");   //QRSPeaks Chen&Chen frame of QRS event parameter
		int oseaMethod     = pb.getIntParameter("oseamethod");  //QRSDetect, QRSDetect2, BeatDetectionAndClassify
		int sampleRate     = pb.getIntParameter("samplerate");  //for osea
		int outputoptions  = pb.getIntParameter("outputoptions");
		
		int intersections = 1; // TODO: GUI; for MAC: 1=intervals between intersections of signal and MAC, 0=intervals between maxima/minima

		float threshold = (float) thres / (float) scaledown;
		float offset   = (float) offS / (float) scaledown;
	
		String plotModelName            = plotModel.getModelName();
		Vector<Double> signal           = plotModel.getData();
		Vector<Double> rangeOld         = plotModel.getDomain();
	
		double samplingInterval   = rangeOld.get(2) - rangeOld.get(1);

		rangeNew.removeAllElements();
		coordinateY.removeAllElements();
		intervals.removeAllElements();
		heights.removeAllElements();
		deltaHeights.removeAllElements();
		energies.removeAllElements();
		dataX2.removeAllElements();
		dataY2.removeAllElements();
		
		if (method == PlotOpPointFinderDescriptor.METHOD_SLOPE) {// Thresholding or MAC)
		
			if (options == PlotOpPointFinderDescriptor.OPTION_THRES) {// Thresholding	
				this.lookingForSlopePointsUsingThresholding(threshold, slope, outputoptions, rangeOld, signal);	
			}	
			if (options == PlotOpPointFinderDescriptor.OPTION_MAC) { // Moving Average Curves (MACs):/ Lu et al., Med. Phys. 33, 3634 (2006); http://dx.doi.org/10.1118/1.2348764
				this.lookingForSlopePointsUsingMAC(threshold, tau, offset, intersections, slope, outputoptions, rangeOld, signal );
			}				
		}	
		if (method == PlotOpPointFinderDescriptor.METHOD_PEAKS) {
			this.lookingForPeakPoints(threshold, outputoptions, rangeOld, signal);			
		}	
		if (method == PlotOpPointFinderDescriptor.METHOD_VALLEYS) {		
			this.lookingForValleyPoints(threshold, outputoptions, rangeOld, signal);			
		}
		if (method == PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_CHENCHEN) {		
			this.lookingForQRSPointsChen(m, sumInterval, peakFrame, outputoptions, rangeOld, signal);			
		}
		if (method == PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_OSEA) {		
			this.lookingForQRSPointsOsea(oseaMethod, sampleRate, outputoptions, rangeOld, signal);			
		}
		
		//Output options
		//###############################################################################################	
	
		if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES) {
			if (coordinateY == null || coordinateY.isEmpty()){
				BoardPanel.appendTextln("No coordinates found! Maybe that threshold is not well set");
				DialogUtil.getInstance().showDefaultErrorMessage("No coordinates found! Maybe that threshold is not well set");	
				return null;
			}
			PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
			// it is necessary to generate a new instance of PlotModel
			// it is necessary to clone output data for stack processing
			PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(), "X",
			plotModel.getDataHeader(), "Y", (Vector<Double>) rangeNew.clone(), (Vector<Double>) coordinateY.clone());
			plotModelNew.setModelName(plotModelName);
			if (this.isCancelled(this.getParentTask()))
				return null;
			return new Result(plotModelNew);
		}	
		if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS) {
			if (intervals == null || intervals.isEmpty()){
				BoardPanel.appendTextln("No intervals found! Maybe that threshold is not well set");
				DialogUtil.getInstance().showDefaultErrorMessage("No intervals found! Maybe that threshold is not well set");	
				return null;
			}
			PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
			// it is necessary to generate a new instance of PlotModel
			// it is necessary to clone output data for stack processing
			PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(), "#",
			plotModel.getDataHeader(), "Interval", (Vector<Double>) rangeNew.clone(), (Vector<Double>) intervals.clone());
			plotModelNew.setModelName(plotModelName);
			if (this.isCancelled(this.getParentTask()))
				return null;
			return new Result(plotModelNew);
		}	
		if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_HEIGHTS) {
			if (heights == null || heights.isEmpty()){
				BoardPanel.appendTextln("No heights found! Maybe that threshold is not well set");
				DialogUtil.getInstance().showDefaultErrorMessage("No heights found! Maybe that threshold is not well set");	
				return null;
			}
			PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
			// it is necessary to generate a new instance of PlotModel
			// it is necessary to clone output data for stack processing
			PlotModel plotModelNew1 = new PlotModel(plotModel.getDomainHeader(), "#",
			plotModel.getDataHeader(), "Height", (Vector<Double>) rangeNew.clone(), (Vector<Double>) heights.clone());
			plotModelNew1.setModelName(plotModelName);
			if (this.isCancelled(this.getParentTask()))
				return null;
			return new Result(plotModelNew1);

		}
		if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_DELTAHEIGHTS) {
			if (deltaHeights == null || deltaHeights.isEmpty()) {
				BoardPanel.appendTextln("No delta Heights found! Maybe that threshold is not well set");
				DialogUtil.getInstance().showDefaultErrorMessage("No delta Heights found! Maybe that threshold is not well set");	
				return null;
			}
			PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
			// it is necessary to generate a new instance of PlotModel
			// it is necessary to clone output data for stack processing
			PlotModel plotModelNew2 = new PlotModel(plotModel.getDomainHeader(), "#",
			plotModel.getDataHeader(), "dHeight", (Vector<Double>) rangeNew.clone(), (Vector<Double>) deltaHeights.clone());
			plotModelNew2.setModelName(plotModelName);
			if (this.isCancelled(this.getParentTask()))
				return null;
			return new Result(plotModelNew2);
		}
		if (outputoptions == PlotOpPointFinderDescriptor.OUTPUTOPTION_ENERGIES) {
			if (energies     == null || energies.isEmpty()){
				BoardPanel.appendTextln("No energies found! Maybe that threshold is not well set");
				DialogUtil.getInstance().showDefaultErrorMessage("No energies found! Maybe that threshold is not well set");
				return null;
			}
			PlotTools.displayPointFinderPlotXY(rangeOld, signal, dataX2, dataY2, false, "Point Finder", "Signal + Points", "Samples [a.u.]", "Values [a.u.]");
			// it is necessary to generate a new instance of PlotModel
			// it is necessary to clone output data for stack processing
			PlotModel plotModelNew3 = new PlotModel(plotModel.getDomainHeader(), "#",
			plotModel.getDataHeader(), "Energy", (Vector<Double>) rangeNew.clone(), (Vector<Double>) energies.clone());
			plotModelNew3.setModelName(plotModelName);
			if (this.isCancelled(this.getParentTask()))
				return null;
			return new Result(plotModelNew3);
		}
		return null;
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpPointFinderDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return PlotOpPointFinderDescriptor.TYPE;
	}

}
