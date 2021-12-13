package at.mug.iqm.commons.lau;

/*
 * #%L
 * Project: IQM - API
 * File: LA_conjgrad_methods.java
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


/**
 * This is from:
 * Hang T. Lau
 * A Numerical Library in Java for Scientists & Engineers
 * Chapman & Hall/Crc 2003
 * 
 * @author Helmut Ahammer
 * @since 2010 05
 */
public interface LA_conjgrad_methods {
	void matvec(double p[], double q[]);
	boolean goon(int iterate[], double norm2[]);
}
