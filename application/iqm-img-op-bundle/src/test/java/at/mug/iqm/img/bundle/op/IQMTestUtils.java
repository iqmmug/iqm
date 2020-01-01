package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IQMTestUtils.java
 * 
 * $Id$
 * $HeadURL$
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


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.WorkPackage;

/**
 * Utility methods for unit tests
 */
public class IQMTestUtils {
    public static void printMatrix(int[][] m) {
        try {
            int rows = m.length;
            int columns = m[0].length;
            String str = "|\t";

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    str += m[i][j] + "\t";
                }

                System.out.println(str + "|");
                str = "|\t";
            }

        } catch (Exception e) {
            System.out.println("Matrix is empty!!");
        }
    }

    /**
     * Creates a random planar image with the desired size and number of bands.
     * Source: http://www.lac.inpe.br/JIPCookbook/index.jsp
     *
     * @param width
     * @param height
     * @param bands
     * @return
     */
    public static PlanarImage createRandomImage(int width, int height, int bands) {
        //To prevent warning in console
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");

        SampleModel sampleModel = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE, width, height, bands);
        ColorModel cm = TiledImage.createColorModel(sampleModel);

        TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0, sampleModel, cm);
        WritableRaster wr = tiledImage.getWritableTile(0, 0);
        for (int b = 0; b < bands; b++) {
            for (int h = 0; h < height / 32; h++) {
                for (int w = 0; w < width / 32; w++) {

                    int[] fill = new int[32 * 32];
                    Arrays.fill(fill, (int) (Math.random() * 256));
                    wr.setSamples(w * 32, h * 32, 32, 32, b, fill);

                }
            }
        }
        return tiledImage;
    }

    /**
     * Creates a sinus image with the desired size and number of bands.
     * Source: http://www.lac.inpe.br/JIPCookbook/index.jsp
     *
     * @param width
     * @param height
     * @param bands
     * @return
     */
    public static PlanarImage createSinusImage(int width, int height, int bands) {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
        SampleModel sampleModel = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE, width, height, bands);
        ColorModel cm = TiledImage.createColorModel(sampleModel);
        TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0, sampleModel, cm);
        WritableRaster wr = tiledImage.getWritableTile(0, 0);

        for (int h = 0; h < height / 32; h++) {
            for (int w = 0; w < width / 32; w++) {
                for (int b = 0; b < bands; b++) {
                    int[] fill = new int[32 * 32];
                    int value = 127 + (int) (128 * Math.sin(w) * Math.sin(h));
                    Arrays.fill(fill, value);
                    wr.setSamples(w * 32, h * 32, 32, 32, b, fill);
                }
            }
        }
        return tiledImage;
    }

    /**
     * Creates a new random image and adds it to the work package
     * @param wp
     */
    public static void addNewRandomImage(WorkPackage wp) {
        int width = 480;
        int height = 320;
        int bands = 3;
        String imgName = height + "*" + width + "*" + bands;
        PlanarImage pi = createRandomImage(width, height, bands);
        updateWorkPackage(wp, pi, imgName);

        //JAI.create("filestore", pi, "testimage_color.png", "PNG");
    }

    /**
     * Loads an image from classpath and adds it to the work package
     * @param path
     * @param wp
     * @throws IOException
     */
    public static void addLoadedImage(String path, WorkPackage wp) throws IOException {
        PlanarImage pi = loadImage(path);
        updateWorkPackage(wp, pi, path);
    }

    /**
     * Creates a new sinus image and adds it to the work package
     * @param wp
     */
    public static void addNewSinusImage(WorkPackage wp) {
        int width = 480;
        int height = 320;
        int bands = 3;
        String imgName = height + "x" + width + "x" + bands;
        PlanarImage pi = createSinusImage(width, height, bands);
        updateWorkPackage(wp, pi, imgName);

        //JAI.create("filestore", pi, imgName + ".png", "PNG");
    }

    /**
     * Creates a new ImageModel and adds it to the work package
     * @param wp
     * @param pi
     * @param imgName
     */
    private static void updateWorkPackage(WorkPackage wp, PlanarImage pi, String imgName) {
        ImageModel im = new ImageModel(pi);
        im.setModelName(imgName);
        im.setFileName(imgName + ".png");
        IqmDataBox iqmDataBox = new IqmDataBox(im);
        Vector<Object> vector = new Vector<Object>();
        vector.add(iqmDataBox);
        wp.updateSources(vector);
    }

    /**
     * Loads an image from the classpath
     * @param path
     * @return
     * @throws IOException
     */
    public static PlanarImage loadImage(String path) throws IOException {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
        RenderedImage renderedImage = javax.imageio.ImageIO.read(IQMTestUtils.class.getResourceAsStream(path));
        return PlanarImage.wrapRenderedImage(renderedImage);
    }

    /**
     * Executes an operator several times and prints a number of descriptive statistics.
     * @param op
     * @param wp
     * @param times
     * @throws Exception
     */
    public static void benchMarkOperator(AbstractOperator op, WorkPackage wp, int times) throws Exception {
        double[] durations = new double[times];

        long testBegin = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            addNewRandomImage(wp);
            //String sample8bit = "test_images/FluorescentCells_8bit.jpg";
            //IQMTestUtils.addLoadedImage(sample8bit, wp);
            long startTime = System.currentTimeMillis();
            op.run(wp);
            long endTime = System.currentTimeMillis();
            durations[i] = endTime - startTime;
        }
        long testDuration = System.currentTimeMillis() - testBegin;
        DescriptiveStatistics stats = calcStatistics(durations);
        System.out.println("Exectution summary, executed " + times + " times.");
        System.out.println("Mean: " + stats.getMean());
        System.out.println("Median: " + stats.getPercentile(50));
        System.out.println("Max: " + stats.getMax());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Total test duration: " + testDuration);
    }

    private static DescriptiveStatistics calcStatistics(double[] durations) {
        return new DescriptiveStatistics(durations);
    }

    /**
     * Utility-method for printing the TableModel-results of an operator to the console
     * @param res
     */
    public static void printTableResults(IResult res) {
        ArrayList<IqmDataBox> dataBoxList = res.listTableResults();
        for (IqmDataBox dataBox : dataBoxList) {
            TableModel tm = dataBox.getTableModel();
            printTable(tm);
        }
    }

    /**
     * Prints a TableModel to the console
     * @param tm
     */
    public static void printTable(TableModel tm) {
        Vector dataRows = tm.getDataVector();

        assertTrue(dataRows.size() > 0); //at least one row

        for (int i = 0; i < tm.getColumnCount(); i++) {
            String colName = tm.getColumnName(i);
            StringBuilder sb = new StringBuilder(colName);
            sb.append(": ");
            for (Object o : dataRows) {
                Vector row = (Vector) o;
                sb.append(row.get(i));
                sb.append(", ");
            }
            System.out.println(sb);
        }
    }
}
