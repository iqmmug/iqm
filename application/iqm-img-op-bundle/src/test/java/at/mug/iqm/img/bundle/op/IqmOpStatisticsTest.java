package at.mug.iqm.img.bundle.op;

/*
 *#%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStatisticsTest.java
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

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.WorkPackage;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatisticsDescriptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class IqmOpStatisticsTest {
    IqmOpStatistics iOpStat;
    ParameterBlockIQM pb;
    WorkPackage wp;

    @BeforeEach
    public void setUp() throws Exception {
        iOpStat = new IqmOpStatistics();
        pb = new ParameterBlockIQM(new IqmOpStatisticsDescriptor());

        //Default for Binary & Order1 is 1 already
        pb.setParameter("Order2", 1);
        //pb.setParameter("Range",3);
        //pb.setParameter("Direction",2);

        wp = new WorkPackage(iOpStat, pb);
    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testRun1() throws Exception {
        IQMTestUtils.addNewSinusImage(wp);
        IResult res = iOpStat.run(wp);
        assertTrue(res.hasTables());
        ArrayList<IqmDataBox> dataBoxList = res.listTableResults();
        for (IqmDataBox dataBox : dataBoxList) {
            TableModel tm = dataBox.getTableModel();
            //IQMTestUtils.printTable(tm);


            for (Object obj : tm.getDataVector()) {
                Vector row = (Vector) obj;
                //Assertions calculated with operator before refactoring
                assertEquals("1st Moment", tm.getColumnName(18));
                assertEquals(129.11333333333332, row.get(18));

                assertEquals("2nd Dissimilarity", tm.getColumnName(30));
                assertEquals(2.0172925f, row.get(30));

                assertEquals("2nd StdDev x", tm.getColumnName(37));
                assertEquals(61.471565f, row.get(37));

                assertEquals("2nd StdDev y", tm.getColumnName(38));
                assertEquals(61.471558f, row.get(38));

                assertEquals("Skewness", tm.getColumnName(16));
                assertEquals(1.512723112578398, row.get(16));
            }
        }
    }
    @Test
    public void testRun2() throws Exception {
        wp.getParameters().setParameter("Range",3);
        wp.getParameters().setParameter("Direction",2);

        IQMTestUtils.addNewSinusImage(wp);
        IResult res = iOpStat.run(wp);
        assertTrue(res.hasTables());
        ArrayList<IqmDataBox> dataBoxList = res.listTableResults();
        for (IqmDataBox dataBox : dataBoxList) {
            TableModel tm = dataBox.getTableModel();
            //IQMTestUtils.printTable(tm);


            for (Object obj : tm.getDataVector()) {
                Vector row = (Vector) obj;
                //Assertions calculated with operator before refactoring
                assertEquals("1st Moment", tm.getColumnName(18));
                assertEquals(129.11333333333332, row.get(18));

                assertEquals("2nd Dissimilarity", tm.getColumnName(30));
                assertEquals(0.03770235f, row.get(30));

                assertEquals("2nd StdDev x", tm.getColumnName(37));
                assertEquals(1.7202834f, row.get(37));

                assertEquals("2nd StdDev y", tm.getColumnName(38));
                assertEquals(1.7202833f, row.get(38));

                assertEquals("Skewness", tm.getColumnName(16));
                assertEquals(1.512723112578398, row.get(16));
            }
        }
    }

    //@Test
    public void testRun3() throws Exception {
        //Sample images are in different module
        String sample8bit = "test_images/FluorescentCells_8bit.jpg";
        IQMTestUtils.addLoadedImage(sample8bit, wp);
        IResult res = iOpStat.run(wp);
        IQMTestUtils.printTableResults(res);
    }

    //@Test
    public void testRun4() throws Exception {
        //Sample images are in different module
        String sample8bit = "test_images/FluorescentCells_RGB.jpg";
        IQMTestUtils.addLoadedImage(sample8bit, wp);
        IResult res = iOpStat.run(wp);
        IQMTestUtils.printTableResults(res);
    }

    //@Test
    public void testRunPerformance() throws Exception {
        IQMTestUtils.benchMarkOperator(iOpStat, wp, 50);
    }
}
