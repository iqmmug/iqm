package at.mug.iqm.commons.util.osea4java.classification;

/*-
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: ECGCODES.java
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

/* file: ecgcodes.h	T. Baker and G. Moody	  June 1981
Last revised:  19 March 1992		dblib 7.0
ECG annotation codes

Copyright (C) Massachusetts Institute of Technology 1992. All rights reserved.
*/

public class ECGCODES {

	public static final int NOTQRS   =  0 ; /* not-QRS (not a getann/putann code) */
	public static final int NORMAL   =  1 ; /* normal beat */
	public static final int LBBB     =  2 ; /* left bundle branch block beat */
	public static final int RBBB     =  3 ; /* right bundle branch block beat */
	public static final int ABERR    =  4 ; /* aberrated atrial premature beat */
	public static final int PVC      =  5 ; /* premature ventricular contraction */
	public static final int FUSION   =  6 ; /* fusion of ventricular and normal beat */
	public static final int NPC      =  7 ; /* nodal (junctional) premature beat */
	public static final int APC      =  8 ; /* atrial premature contraction */
	public static final int SVPB     =  9 ; /* premature or ectopic supraventricular beat */
	public static final int VESC     = 10 ; /* ventricular escape beat */
	public static final int NESC     = 11 ; /* nodal (junctional) escape beat */
	public static final int PACE     = 12 ; /* paced beat */
	public static final int UNKNOWN  = 13 ; /* unclassifiable beat */
	public static final int NOISE    = 14 ; /* signal quality change */
	public static final int ARFCT    = 16 ; /* isolated QRS-like artifact */
	public static final int STCH     = 18 ; /* ST change */
	public static final int TCH      = 19 ; /* T-wave change */
	public static final int SYSTOLE  = 20 ; /* systole */
	public static final int DIASTOLE = 21 ; /* diastole */
	public static final int NOTE     = 22 ; /* comment annotation */
	public static final int MEASURE  = 23 ; /* measurement annotation */
	public static final int BBB      = 25 ; /* left or right bundle branch block */
	public static final int PACESP    = 26 ; /* non-conducted pacer spike */
	public static final int RHYTHM   = 28 ; /* rhythm change */
	public static final int LEARN    = 30 ; /* learning */
	public static final int FLWAV    = 31 ; /* ventricular flutter wave */
	public static final int VFON     = 32 ; /* start of ventricular flutter/fibrillation */
	public static final int VFOFF    = 33 ; /* end of ventricular flutter/fibrillation */
	public static final int AESC     = 34 ; /* atrial escape beat */
	public static final int SVESC    = 35 ; /* supraventricular escape beat */
	public static final int NAPC     = 37 ; /* non-conducted P-wave (blocked APB) */
	public static final int PFUS     = 38 ; /* fusion of paced and normal beat */
	public static final int PQ       = 39 ; /* PQ junction (beginning of QRS) */
	public static final int JPT      = 40 ; /* J point (end of QRS) */
	public static final int RONT     = 41 ; /* R-on-T premature ventricular contraction */

	/* ... annotation codes between RONT+1 and ACMAX inclusive are user-defined */

	public static final int ACMAX    = 49 ; /* value of largest valid annot code (must be < 50) */
}
