package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStatistics.java
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


import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatisticsDescriptor;

/**
 * <p/>
 * <b>Changes</b>
 * <ul>
 * <li>2014 04 03 HA bugfix column 0 and row 0 was not calculated for 2nd order statistics, many thanks to Martin Reiss
 * </ul>
 *
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class IqmOpStatistics extends AbstractOperator {
    // WARNING: Don't declare fields here
    // Fields declared here aren't thread safe!

    public IqmOpStatistics() {

    }

    @Override
    public IResult run(IWorkPackage wp) {

        ParameterBlockIQM pb = wp.getParameters();

        fireProgressChanged(5);

        int binary = pb.getIntParameter("Binary");
        int order1 = pb.getIntParameter("Order1");
        int order2 = pb.getIntParameter("Order2");
        int range = pb.getIntParameter("Range"); //Grey value range
        int dist = pb.getIntParameter("Distance"); //Distance used for Cooccurence-Matrix
        int dir = pb.getIntParameter("Direction"); //Direction used for Cooccurence-Matrix
        int back = pb.getIntParameter("Background"); //Whether the background is included or not. Background is everything with grey value = 0
        String strBack = "";
        String strDir = "";
        if (back == 0) strBack = "Incl";
        if (back == 1) strBack = "Excl";
        if (dir == 0) strDir = "Vertical";
        if (dir == 1) strDir = "Horizontal";
        if (dir == 2) strDir = "+ shaped";
        if (dir == 3) strDir = "X shaped";
        if (dir == 4) strDir = "All";

        boolean backgroundIsIncluded = (back == 0);

        PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();

        double imgTypeGreyMax = ImageTools.getImgTypeGreyMax(pi); //Maximum possible grey value for the image

        String imgName = String.valueOf(pi.getProperty("image_name"));
        String fileName = String.valueOf(pi.getProperty("file_name"));

        int greyMatrixMax = (int) Math.pow(2, range);

        int numBands = pi.getData().getNumBands();
        int width = pi.getWidth();
        int height = pi.getHeight();

        double[][] minMax = null; //0: Minimum values for each band, 1: Maximum values for each band

        CoocurrenceMatrix coocurrenceMatrix = null;

        fireProgressChanged(20);
        if (isCancelled(getParentTask())) {
            return null;
        }

        Histogram histo = createHistogram(pi, backgroundIsIncluded, imgTypeGreyMax);

        TableModel model = createModel(imgName);
        model = addModelColumns(model, binary, order1, order2);

        //An array of vectors, one for each band
        Vector[] dataRows = new Vector[numBands];

        for (int b = 0; b < numBands; b++) {
            dataRows[b] = new Vector(model.getColumnCount());

            calculateCommonFeatures(dataRows[b], b, fileName, imgName, strBack, width, height);

            if (binary == 1) {
                calculateBinaryFeatures(dataRows[b], b, histo, backgroundIsIncluded);
            }
            if (order1 == 1) {

                if (minMax == null) { //Minimum and maximum values are calculated only once for multiband images
                    minMax = calcMinMax(pi, backgroundIsIncluded, histo);
                }
                calculateFirstOrderFeatures(dataRows[b], b, minMax, histo, imgTypeGreyMax, back, pi);
            }
            if (order2 == 1) {
                if (coocurrenceMatrix == null) { //cooccurenceMatrix is calculated only once for multiband images
                    PlanarImage piRescaled = rescaleImage(pi, greyMatrixMax, imgTypeGreyMax);
                    coocurrenceMatrix = new CoocurrenceMatrix(piRescaled, dist, greyMatrixMax, backgroundIsIncluded, numBands, dir);
                }
                calculateSecondOrderFeatures(dataRows[b], b, coocurrenceMatrix, strDir, dist,range);
            }
        }

        //Each row represents one band
        for (Vector row : dataRows) {
            model.addRow(row);
        }


        fireProgressChanged(95);
        if (isCancelled(getParentTask())) {
            return null;
        }
        return new Result(model);
    }

    /**
     * Initializes the table model and adds the necessary columns
     */
    private TableModel createModel(String imgName) {
        //model.setProperty("disable_thumbnails", 1);

        return new TableModel("Statistics: " + imgName);
    }

    /**
     * Add Features that are always used
     * @param dataRow
     * @param b
     * @param fileName
     * @param imgName
     * @param strBack
     * @param width
     * @param height
     */
    @SuppressWarnings("unchecked")
	private void calculateCommonFeatures(Vector dataRow, int b, String fileName, String imgName, String strBack, int width, int height) {
        dataRow.add(fileName);
        dataRow.add(imgName);
        dataRow.add(b);
        dataRow.add(strBack);
        dataRow.add(width);
        dataRow.add(height);
    }

    /**
     * Reduce image gray values
     *
     * @return
     * @param pi
     * @param greyMatrixMax
     * @param imgTypeGreyMax
     */
    private PlanarImage rescaleImage(PlanarImage pi, double greyMatrixMax, double imgTypeGreyMax) {
        ParameterBlock pbWork = new ParameterBlock();
        pbWork.addSource(pi);

        //TODO: imgTypeGreyMax-0.0? Maybe imgTypeGreyMax - back?
        pbWork.add(new double[]{((greyMatrixMax - 1.0) / (imgTypeGreyMax - 0.0))}); // Rescale
        //pbWork.add(new double[] { (((greyMatrixMax - 1.0) * 0.0) / (0.0 - imgTypeGreyMax)) });  offset would result in 0
        return JAI.create("Rescale", pbWork);
    }

    /**
     * Calculates the binary features
     * @param dataRow
     * @param b
     * @param histo
     * @param backgroundIsIncluded
     */
    private void calculateBinaryFeatures(Vector dataRow, int b, Histogram histo, boolean backgroundIsIncluded) {
        int totals = histo.getTotals()[b];
        int greater0;
        if (backgroundIsIncluded) { // inclusive
            int totalZero = histo.getSubTotal(b, 0, 0);
            greater0 = totals - totalZero;
        } else {
            greater0 = totals;
        }
        dataRow.add(totals);
        dataRow.add(greater0);
    }

    /**
     * Calculates the first order features
     * @param dataRow
     * @param b
     * @param minMax
     * @param histo
     * @param imgTypeGreyMax
     * @param back
     * @param pi
     */
    private void calculateFirstOrderFeatures(Vector dataRow, int b, double[][] minMax, Histogram histo, double imgTypeGreyMax, int back, PlanarImage pi) {
        double min = minMax[0][b];
        double max = minMax[1][b];

        double mean = histo.getMean()[b];
        double stddev = histo.getStandardDeviation()[b];
        double energy;
        double entropy = histo.getEntropy()[b];
        int totals = histo.getTotals()[b];

        // Absolute Moment1 = Sum(g.P(g)),     Absolute Moment2 = Sum((g.P(g)^2) (eventually but probably not: Sum((g.P(g))^2) )
        // Central  Moment1 = Sum((g-µ).P(g)), Central Moment2  = Sum((g-µ)^2.P(g))
        double moment1 = histo.getMoment(1, true, false)[b]; //central moment would be zero, moment1 = mean
        double moment2 = histo.getMoment(2, false, true)[b];
        double moment3 = histo.getMoment(3, false, true)[b];
        double moment4 = histo.getMoment(4, false, true)[b];
        double skewness;
        double kurtosis;


        skewness = moment3 / Math.pow(stddev, 3);
        // kurtosis[b] = moment4[b]/(stddev[b]*stddev[b]*stddev[b]*stddev[b]) - 3.0d; "Excess" and not Kurtosis, see e.g. Wikipedia
        kurtosis = moment4 / Math.pow(stddev, 4); // changed 2011_12_18
        energy = 0.0d;
        double Pg;


        for (int g = 0; g <= imgTypeGreyMax - back; g++) {
            Pg = histo.getSubTotal(b, g, g); // for exclusive background: the first element at 0 of histo is the grey value 1
            // if (back == 0) Pg = histo.getSubTotal(b, g, g);
            // if (back == 1) Pg = histo.getSubTotal(b, g-1, g-1);
            // //because the first element at 0 of histo is the grey value 1
            energy = energy + (Pg * Pg);
        }
        //Math.pow(totals[b], 2);
        energy = energy / ((double) totals * (double) totals); // 1/(N^2) double essential!! 2010 12 30
        // energy[b] = energy[b] / ((double)imgWidth*(double)imgHeight*(double)imgWidth*(double)imgHeight); 1/(N^2) //too large for exclusive background

        double[] center = ImageTools.getCenterOfGravity(pi);
        center[1] = pi.getHeight() - 1 - center[1];

        dataRow.add(min);
        dataRow.add(max);
        dataRow.add(mean);
        dataRow.add(stddev);
        dataRow.add(energy);
        dataRow.add(entropy);
        dataRow.add(skewness);
        dataRow.add(kurtosis);
        dataRow.add(moment1);
        dataRow.add(moment2);
        dataRow.add(moment3);
        dataRow.add(moment4);
        dataRow.add(center[0]);
        dataRow.add(center[1]);
    }

    /**
     * Calculate second order features
     * @param dataRow
     * @param b
     * @param coocurrenceMatrix
     * @param strDir
     * @param dist
     * @param range
     */
    private void calculateSecondOrderFeatures(Vector dataRow, int b, CoocurrenceMatrix coocurrenceMatrix, String strDir, int dist, int range) {
        float[][] coMatrix = coocurrenceMatrix.getCoMatrix()[b];

        // calculation of 2nd moments
        float energy2 = 0.0f;
        float entropy2 = 0.0f;
        float contrast2 = 0.0f; // ==inertia2
        float dissim2 = 0.0f;
        float homogen2 = 0.0f;
        float inverseDiff2 = 0.0f;
        float correlation2 = 0.0f;
        float maxProb2 = 0.0f; // Maximum Probability
        float mean2x = 0.0f; // GLCM Mean
        float mean2y = 0.0f; // GLCM Mean
        float stdDev2x = 0.0f; // GLCM Mean
        float stdDev2y = 0.0f; // GLCM Mean

        float sumP = 0.0f; //sumP for normalization (sum of all probabilities should be 1);
        for (int i = 0; i < coMatrix.length; i++) {
            for (int j = 0; j < coMatrix[0].length; j++) {

                float coM = coMatrix[i][j];
                sumP = sumP + coM;
                // coM = 1.0f/(greyMatrixMax)/(greyMatrixMax); //matrix is flat
                //if (coM != 0) System.out.println("IqmOpStatistics: i  j  coM: " + i + " " + j + "  " + coM);
                energy2 = energy2 + (coM * coM);
                if (coM != 0) {
                    entropy2 = entropy2 - coM * (float) ((Math.log(coM) / Math.log(2.0f))); //which Log is the right one?
                }

                // if (coM != 0) entropy2[b] = entropy2[b] - coM * (float)(Math.log(coM));
                contrast2 = contrast2 + coM * (i - j) * (i - j);
                dissim2 = dissim2 + coM * Math.abs(i - j);
                homogen2 = homogen2 + coM / (1.0f + (i - j) * (i - j));
                if (i != j) {
                    inverseDiff2 = inverseDiff2 + coM / Math.abs(i - j);
                }
                maxProb2 = Math.max(maxProb2, coM);
                mean2x = mean2x + coM * i;
                mean2y = mean2y + coM * j;
            }
        }
        // energy2[b] = (float) Math.sqrt(energy2[b]); //??
        //System.out.println("IqmOpStatistics: Sum of all probabilities should be 1, current value is: " + sumP);


        // Standard Deviations
        for (int i = 0; i < coMatrix.length; i++) {
            for (int j = 0; j < coMatrix[0].length; j++) {
                float coM = coMatrix[i][j];
                stdDev2x = stdDev2x + coM * (i - mean2x) * (i - mean2x); //that is the variance
                stdDev2y = stdDev2y + coM * (j - mean2y) * (j - mean2y);
            }
        }
        stdDev2x = (float) Math.sqrt(stdDev2x);
        stdDev2y = (float) Math.sqrt(stdDev2y);

        // Correlation
        if (stdDev2x == 0.0 || stdDev2y == 0.0) {//special case for uniform grey values
            correlation2 = 1.0f;
        } else {
            //float var2x = stdDev2x[b] * stdDev2x[b];
            //float var2y = stdDev2y[b] * stdDev2y[b];
            for (int i = 0; i < coMatrix.length; i++) {
                for (int j = 0; j < coMatrix[0].length; j++) {
                    float coM = coMatrix[i][j];
                    // correlation2[b] = correlation2[b] + coM * (((float)i - mean2x[b])*((float)j - mean2y[b])/((float)Math.sqrt(var2x * var2y)));
                    correlation2 = correlation2 + coM * ((i - mean2x) * (j - mean2y));
                }
            }
            //correlation2[b] = correlation2[b] / ((float) Math.sqrt(var2x * var2y));
            //or
            correlation2 = correlation2 / (stdDev2x * stdDev2y);
        }


        dataRow.add(range);
        dataRow.add(dist);
        dataRow.add(strDir);
        dataRow.add(energy2);
        dataRow.add(entropy2);
        dataRow.add(contrast2);
        dataRow.add(dissim2);
        dataRow.add(homogen2);
        dataRow.add(inverseDiff2);
        dataRow.add(correlation2);
        dataRow.add(maxProb2);
        dataRow.add(mean2x);
        dataRow.add(mean2y);
        dataRow.add(stdDev2x);
        dataRow.add(stdDev2y);
    }

    /**
     * Adds the column names to the table model
     *
     * @param model
     */
    private TableModel addModelColumns(TableModel model, int binary, int order1, int order2) {
        model.addColumn("FileName");
        model.addColumn("ImageName");
        model.addColumn("Band");
        model.addColumn("Background");
        model.addColumn("Width");
        model.addColumn("Height");

        if (binary == 1) {
            model.addColumn("# Total");
            model.addColumn("# > 0");
        }
        if (order1 == 1) {
            model.addColumn("Min");
            model.addColumn("Max");
            model.addColumn("Mean");
            model.addColumn("StdDev");
            model.addColumn("Energy");
            model.addColumn("Entropy");
            model.addColumn("Skewness");
            model.addColumn("Kurtosis");
            model.addColumn("1st Moment");
            model.addColumn("2nd Moment");
            model.addColumn("3rd Moment");
            model.addColumn("4th Moment");
            model.addColumn("CentGrav x");
            model.addColumn("CentGrav y");
        }
        if (order2 == 1) {
            model.addColumn("Range");
            model.addColumn("Distance");
            model.addColumn("Direction");
            model.addColumn("2nd Energy");
            model.addColumn("2nd Entropy");
            model.addColumn("2nd Contrast"); // == 2nd Inertia
            model.addColumn("2nd Dissimilarity");
            model.addColumn("2nd Homogeneity");
            model.addColumn("2nd Inverse Difference");
            model.addColumn("2nd Correlation");
            model.addColumn("2nd Maximum Probability");
            model.addColumn("2nd Mean x");
            model.addColumn("2nd Mean y");
            model.addColumn("2nd StdDev x");
            model.addColumn("2nd StdDev y");
        }
        return model;
    }

    /**
     * Calculate min and max values for each band of an image. Depends on whether the background is included or not.
     * Index 0 holds minium values, index 1 holds maximum values.
     *
     * @param pi
     * @param backgroundIsIncluded
     * @param histo
     * @return
     */
    private double[][] calcMinMax(PlanarImage pi, boolean backgroundIsIncluded, Histogram histo) {
        double[] min;
        double[] max;

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(pi);
        RenderedOp rOp = JAI.create("extrema", pb);

        max = ((double[]) rOp.getProperty("maximum"));


        if (backgroundIsIncluded) {
            min = (double[]) rOp.getProperty("minimum");
        } else {
            int[] numBins = histo.getNumBins();

            min = new double[histo.getNumBands()];

            for (int b = 0; b < histo.getNumBands(); b++) {
                min[b] = 0.0;
                double total;
                for (int i = 0; i < numBins[b]; i++) {
                    total = histo.getSubTotal(b, i, i);
                    if (total > 0) {
                        min[b] = i + 1;
                        break;
                    }
                }
            }
        }


        return new double[][]{min, max};
    }

    /**
     * Create image histogram
     *
     * @param pi
     * @param backgroundIsIncluded
     * @param typeGreyMax
     * @return
     */
    private Histogram createHistogram(PlanarImage pi, boolean backgroundIsIncluded, double typeGreyMax) {
        ParameterBlock pbWork = new ParameterBlock();
        pbWork.addSource(pi);
        pbWork.add(null); // ROI
        pbWork.add(1);
        pbWork.add(1); // Sampling


        if (backgroundIsIncluded) { // inclusive
            pbWork.add(new int[]{(int) typeGreyMax + 1}); // Bins
            pbWork.add(new double[]{0.0d});
            pbWork.add(new double[]{typeGreyMax + 1}); // Range for inclusion
        } else { // exclusive
            pbWork.add(new int[]{(int) typeGreyMax}); // Bins
            pbWork.add(new double[]{1.0d});
            pbWork.add(new double[]{typeGreyMax + 1}); // Range for inclusion
        }
        // RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
        // BorderExtender.createInstance(BorderExtender.BORDER_COPY));
        RenderedOp rOp = JAI.create("Histogram", pbWork, null);
        return (Histogram) rOp.getProperty("histogram");

    }

    @Override
    public String getName() {
        if (this.name == null) {
            this.name = new IqmOpStatisticsDescriptor().getName();
        }
        return this.name;
    }

    @Override
    public OperatorType getType() {
        return IqmOpStatisticsDescriptor.TYPE;
    }
}

/**
 * Inner class responsible for calculating the coocurrence matrix
 */
class CoocurrenceMatrix {
    private final PlanarImage pi;
    private final int dist;
    private final int numGreyLevels;
    private final boolean backgroundIsIncluded;
    private final int numBands;
    private final int dir;
    private boolean normalize = true;
    private float[] count;
    private int width;
    private int height;
    private float[][][] coMatrix; //comatrix[band][x][y]
    private boolean symmetric = false;

    public CoocurrenceMatrix(PlanarImage pi, int dist, int numGreyLevels, boolean backgroundIsIncluded, int numBands, int dir) {
        this.pi = pi;
        this.dist = dist;
        this.numGreyLevels = numGreyLevels;
        this.backgroundIsIncluded = backgroundIsIncluded;
        this.numBands = numBands;
        this.dir = dir;


        count = new float[numBands];
        width = pi.getWidth();
        height = pi.getHeight();


    }

    /**
     * Performs the actual matrix calculation
     */
    public void calculateMatrix() {
        if (backgroundIsIncluded) {
            coMatrix = new float[numBands][numGreyLevels][numGreyLevels];
        } else {
            coMatrix = new float[numBands][numGreyLevels - 1][numGreyLevels - 1];
        }

        Raster ri = pi.getData();
        for (int b = 0; b < numBands; b++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int grayLevel = ImageTools.getRasterSample(ri, x, y, b);
                    if (!backgroundIsIncluded && grayLevel == 0) {
                        continue; // continue, because of exclusive background
                    } else {
                        if (dir == 0) { //Vertical
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y + dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y - dist));

                        } else if (dir == 1) { //Horizontal
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y));

                        } else if (dir == 2) { //+
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y + dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y));

                        } else if (dir == 3) { //x-shaped
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y + dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y + dist));

                        } else if (dir == 4) { //all
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y + dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y));

                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y + dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x + dist, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y - dist));
                            calcCoMatValue(ri, b, grayLevel, new Point(x - dist, y + dist));
                        }
                    }
                }
            }
        }
        if (normalize) {
            normalizeMatrix();
        }
    }

    /**
     * Helper method for calculating a specific value
     *
     * @param ri
     * @param b
     * @param grayLevel_i
     * @param p
     */
    private void calcCoMatValue(Raster ri, int b, int grayLevel_i, Point p) {
        if (pointIsInImage(p)) {
            int grayLevel_j = ImageTools.getRasterSample(ri, p.x, p.y, b);
            if (backgroundIsIncluded || grayLevel_j != 0) {
                coMatrix[b][grayLevel_i][grayLevel_j]++;
                count[b]++;
                if (symmetric) {
                    coMatrix[b][grayLevel_j][grayLevel_i]++; //to make the matrix symmetrical "http://www.fp.ucalgary.ca/mhallbey/transposing.htm"
                    count[b]++;
                }

            }
        }
    }

    /**
     * Determines whether a point lies within an image
     *
     * @param p
     * @return
     */
    private boolean pointIsInImage(Point p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    /**
     * Normalizes the matrix
     */
    private void normalizeMatrix() {
        // http://www.fp.ucalgary.ca/mhallbey/GLCM_as_probability.htm;

        for (int b = 0; b < coMatrix.length; b++) {
            for (int i = 0; i < coMatrix[b].length; i++) {
                for (int j = 0; j < coMatrix[b][0].length; j++) {
                    coMatrix[b][i][j] = coMatrix[b][i][j] / count[b];

                }
            }
        }

    }

    public float[][][] getCoMatrix() {
        if (coMatrix == null) {
            calculateMatrix();
        }
        return coMatrix;
    }
}




