/*
 * Copyright 2009 Michael Bedward
 *
 * This file is part of jai-tools.

 * jai-tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * jai-tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with jai-tools.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package at.mug.iqm.commons.jaitools;

/*
 * #%L
 * Project: IQM - API
 * File: KernelUtil.java
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

import javax.media.jai.KernelJAI;

/**
 * Various utility methods that work with KernelJAI objects
 *
 * @author Michael Bedward
 */
public class KernelUtil {

    /**
     * Create a copy of the input kernel with element values
     * standardized to sum to 1.0. The input kernel's
     * dimensions and location of the key element are retained
     * by the new kernel.
     *
     * @param kernel the input kernel
     * @return a new KernelJAI object
     */
    public static KernelJAI standardize(KernelJAI kernel) {
        float[] data = kernel.getKernelData();
        float sum = 0f;
        for (float f : data) {
            sum += f;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] /= sum;
        }

        return new KernelJAI(
                kernel.getWidth(),
                kernel.getHeight(),
                kernel.getXOrigin(),
                kernel.getYOrigin(),
                data);
    }

    /**
     * A utility function that returns a string representation of
     * a KernelJAI object's data.
     *
     * @param kernel the input kernel
     * @param multiLine if true, each row of kernel data is followed by a newline;
     * if false, the string contains no newlines
     *
     * @return a String
     */
    public static String kernelToString(KernelJAI kernel, boolean multiLine) {
        float[] data = kernel.getKernelData();
        int w = kernel.getWidth();
        int h = kernel.getHeight();
        StringBuilder sb = new StringBuilder();

        boolean binaryData = true;
        for (int i = 0; i < data.length && binaryData; i++) {
            if (!(feq(data[i], 0) || feq(data[i], 1))) {
                binaryData = false;
            }
        }

        int k = 0;
        sb.append("[");
        for (int i = 0; i < w; i++) {
            sb.append("[");
            for (int j = 0; j < h; j++, k++) {
                if (binaryData) {
                    sb.append((int) data[k]);
                } else {
                    sb.append(String.format("%.4f", data[k]));
                    if (j < w-1) sb.append(" ");
                }
            }
            sb.append("]");
            if (i < w - 1) {
                if (multiLine) {
                    sb.append("\n ");
                } else {
                    sb.append(" ");
                }
            }
        }
        sb.append("]");

        return sb.toString();
    }

    // round-off tolerance: used in the fcomp method below
    private static final float TOL = 1.0e-8f;

    /**
     * Test if two float values are equal, taking into accont a
     * round-off tolerance
     * @param f1 first value
     * @param f2 second value
     * @return true if equal, false otherwise
     */
    private static boolean feq(float f1, float f2) {
        return Math.abs(f1 - f2) < TOL;
    }
}
