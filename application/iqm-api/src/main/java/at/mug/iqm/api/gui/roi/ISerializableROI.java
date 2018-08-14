package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: ISerializableROI.java
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

import java.awt.geom.PathIterator;

/**
 * This interface defines methods for serializable ROI shapes.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public interface ISerializableROI {
	/*
	 * Wrap the constants of a path.
	 */
	int SEG_MOVETO = PathIterator.SEG_MOVETO;
	int SEG_LINETO = PathIterator.SEG_LINETO;
	int SEG_QUADTO = PathIterator.SEG_QUADTO;
	int SEG_CUBICTO = PathIterator.SEG_CUBICTO;
	int SEG_CLOSE = PathIterator.SEG_CLOSE;
	int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;
	int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

	/**
	 * Get the underlying path iterator of the shape. 
	 * 
	 * @return the path iterator
	 */
	PathIterator getPathIterator();
}
