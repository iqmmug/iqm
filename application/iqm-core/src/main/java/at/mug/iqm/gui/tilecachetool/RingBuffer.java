package at.mug.iqm.gui.tilecachetool;

/*
 * #%L
 * Project: IQM - Application Core
 * File: RingBuffer.java
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

/**
 * <p>Title: Tile Cache Monitoring Tool</p>
 * <p>Description: Monitors and displays JAI Tile Cache activity.</p>
 * <p>Copyright: Copyright (c) 2002, 2010</p>
 * <p>    All Rights Reserved</p>
 * <p>Company: Virtual Visions Software, Inc.</p>
 *
 * @author Dennis Sigel
 * @version 1.02
 *
 * Circular array (fixed length)
 */
public final class RingBuffer {

    private int size  = 100; // minimum default size
    private int start = 0;
    private int count = 0;
    private int write_pointer = 0;
    private boolean filled = false;
    private float[] array;


    /**
     * Constructor
     * @param size ring buffer array dimension
     */
    public RingBuffer(int size) {
        if ( size > 0 ) {
            this.size = size;
        } else {
            this.size = 100;
        }

        array = new float[this.size];
    }

    /**
     * Changes the ring buffer array dimension
     * @param new_size The new ring buffer dimension
     * @throws IllegalArgumentException if <code>new_size</code> .lt. 2
     * @since TCT 1.0
     */
    public synchronized void setSize(int new_size) {
        int old_size = size;

        if ( size < 2 ) {
            throw new IllegalArgumentException("Invalid size.");
        }

        int min = 0;

        if ( old_size < new_size ) {
            filled = false;
            min = old_size;
        } else {
            filled = true;
            min = new_size;
        }

        float[] temp = array;
        array = new float[new_size];

        for ( int i = 0; i < min; i++ ) {
            array[i] = temp[i];
        }

        count = min;
        size  = new_size;
    }

    /**
     * Returns the current number of entries in the ring buffer,
     * up to the ring buffer size.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the ring buffer size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Write a data point into the ring buffer
     * @param data Floating point value of JVM memory usage.
     */
    public synchronized void write(float data) {
        if ( filled == false ) {
            if ( count == size ) {
                filled = true;
            } else {
                count += 1;
            }
        }

        if ( filled ) {
            start = (start + 1) % size;
        }

        array[write_pointer] = data;
        write_pointer = (write_pointer + 1) % size;
    }

    /**
     * Read a data point value from the ring buffer.  Returns the
     * value as a floating point number.
     * @param i Data index offset from the starting index.
     */
    public synchronized float read(int i) {
        if ( i < 0 ) i = 0;
        return array[(start + i) % size];
    }
}

