package at.mug.iqm.img.bundle.imagej;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: Colour_Deconvolution.java
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

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.IndexColorModel;


public class Colour_Deconvolution implements PlugIn {

// G.Landini at bham ac uk
// 30/Mar/2004 released
// 03/Apr/2004 resolved ROI exiting
// 07/Apr/2004 added Methyl Green DAB vectors
// 08/Jul/2004 shortened the code
// 01/Aug/2005 added fast red/blue/DAB vectors
// 02/Nov/2005 changed code to work with image stacks (DLC - dchao at fhcrc org)
// 02/Nov/2005 changed field names so user-defined colours can be set within 
//             macros (DLC - dchao at fhcrc org)
// 04/Feb/2007 1.3 disable popup menu when right clicking
// 23/May/2009 added Feulgen-light green vectors
//14/Apr/2010 v 1.4 added Giemsa vector (Methylene blue & eosin) 
//           the images are now names "title"-(Colour_1) etc so there are not clash of names when using [ ]
//           the log window now prints the java code of the translation matrix to include new vectors in the plugin.
//           added "Hide legend" option
//22/Jun/2010 v 1.5 added Masson Trichrome vector (Methyl blue & Ponceau Fuchsin only (this does not have Iron Haematoxylin vector!)
//           fixed bug: check for 0 components before hiding legend (otherwise there was no image shown if legent hidden) 
//
// This plugin implements stain separation using the colour deconvolution
// method described in:
//
//     Ruifrok AC, Johnston DA. Quantification of histochemical
//     staining by color deconvolution. Analytical & Quantitative
//     Cytology & Histology 2001; 23: 291-299.
//
// The code is based on "Color separation-30", a macro for NIH Image kindly provided
// by A.C. Ruifrok. Thanks Arnout!
//
// The plugin assumes images generated by color subtraction (i.e. light-absorbing dyes
// such as those used in bright field histology or ink on printed paper) but the dyes
// should not be neutral grey.
//
// I strongly suggest to read the paper reference above to understand how to determine
// new vectors and how the whole procedure works.
//
// The plugin works correctly when the background is neutral (white to light grey), 
// so background subtraction and colour correction must be applied to the images before 
// processing.
//
// The plugin provides a number of "built in" stain vectors some of which were determined
// experimentally in our lab (marked GL), but you may have to determine your own vectors to
// provide a more accurate stain separation, depending on the stains and methods you use.
// Ideally, vector determination should be done on slides stained with only one colour
// at a time (using the "From ROI" interactive option).
//
// The plugin takes an RGB image and returns three 8-bit images. If the specimen is
// stained with a 2 colour scheme (such as H & E) the 3rd image represents the
// complimentary of the first two colours (i.e. green).
//
// Please be *very* careful about how to interpret the results of colour deconvolution
// when analysing histological images.
// Most staining methods are not stochiometric and so optical density of the chromogen
// may not correlate well with the *quantity* of the reactants.
// This means that optical density of the colour may not be a good indicator of
// the amount of material stained.
//
// Read the paper!
//

	@Override
	public void run(String arg) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp==null){
			IJ.error("No image!");
			return;
		}
		if (imp.getBitDepth()!=24){
			IJ.error("RGB image needed.");
			return;
		}
		ImageStack stack = imp.getStack();
		int width = stack.getWidth();
		int height = stack.getHeight();
                String title = imp.getTitle();

		GenericDialog gd = new GenericDialog("Colour Deconvolution 1.5", IJ.getInstance());
		//gd.addMessage("Select subtractive colour model");
		String [] stain={"From ROI", "H&E", "H&E 2","H DAB", "Feulgen Light Green", "Giemsa", "FastRed FastBlue DAB", "Methyl Green DAB", "H&E DAB", "H AEC","Azan-Mallory","Masson Trichrome","Alcian blue & H","H PAS","RGB","CMY", "User values"};
		gd.addChoice("Vectors", stain, stain[0]);
		gd.addCheckbox("Show matrices",false);
		gd.addCheckbox("Hide legend",false);

		//gd.addCheckbox("Linear image",false);

		gd.showDialog();
		if (gd.wasCanceled())
			return;
		String myStain = gd.getNextChoice();
		boolean doIshow = gd.getNextBoolean();
		boolean hideLegend = gd.getNextBoolean();

		double leng, A, V, C, log255=Math.log(255.0);
		int i,j;
		double [] MODx = new double[3];
		double [] MODy = new double[3];
		double [] MODz = new double[3];
		double [] cosx = new double[3];
		double [] cosy = new double[3];
		double [] cosz  = new double[3];
		double [] len = new double[3];
		double [] q = new double[9];
		byte [] rLUT = new byte[256];
		byte [] gLUT = new byte[256];
		byte [] bLUT = new byte[256];

// stains are defined after this line

		if (myStain.equals("H&E")){
			// GL Haem matrix
			MODx[0]= 0.644211; //0.650;
			MODy[0]= 0.716556; //0.704;
			MODz[0]= 0.266844; //0.286;
			// GL Eos matrix
			MODx[1]= 0.092789; //0.072;
			MODy[1]= 0.954111; //0.990;
			MODz[1]= 0.283111; //0.105;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("H&E 2")){
			// GL Haem matrix
			MODx[0]= 0.49015734;
			MODy[0]= 0.76897085;
			MODz[0]= 0.41040173;
			// GL Eos matrix
			MODx[1]= 0.04615336;
			MODy[1]= 0.8420684;
			MODz[1]= 0.5373925;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("H DAB")){
			// 3,3-diamino-benzidine tetrahydrochloride
			// Haem matrix
			MODx[0]= 0.650;
			MODy[0]= 0.704;
			MODz[0]= 0.286;
			// DAB matrix
			MODx[1]= 0.268;
			MODy[1]= 0.570;
			MODz[1]= 0.776;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("Feulgen Light Green")){
			//GL Feulgen & light green
			//Feulgen
			MODx[0]= 0.46420921;
			MODy[0]= 0.83008335;
			MODz[0]= 0.30827187;
			// light green
			MODx[1]= 0.94705542;
			MODy[1]= 0.25373821;
			MODz[1]= 0.19650764;
			// Zero matrix
			MODx[2]= 0.0; // 0.0010000
			MODy[2]= 0.0; // 0.47027777
			MODz[2]= 0.0; //0.88235928
		}

		if (myStain.equals("Giemsa")){
			// GL  Methylene Blue and Eosin
			MODx[0]= 0.834750233;
			MODy[0]= 0.513556283;
			MODz[0]= 0.196330403;
			// GL Eos matrix
			MODx[1]= 0.092789;
			MODy[1]= 0.954111;
			MODz[1]= 0.283111;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("FastRed FastBlue DAB")){
			//fast red
			MODx[0]= 0.21393921;
			MODy[0]= 0.85112669;
			MODz[0]= 0.47794022;
			// fast blue
			MODx[1]= 0.74890292;
			MODy[1]= 0.60624161;
			MODz[1]= 0.26731082;
			// dab
			MODx[2]= 0.268;
			MODy[2]= 0.570;
			MODz[2]= 0.776;
		}

		if (myStain.equals("Methyl Green DAB")){
			// MG matrix (GL)
			MODx[0]= 0.98003;
			MODy[0]= 0.144316;
			MODz[0]= 0.133146;
			// DAB matrix
			MODx[1]= 0.268;
			MODy[1]= 0.570;
			MODz[1]= 0.776;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("H&E DAB")){
			// Haem matrix
			MODx[0]= 0.650;
			MODy[0]= 0.704;
			MODz[0]= 0.286;
			// Eos matrix
			MODx[1]= 0.072;
			MODy[1]= 0.990;
			MODz[1]= 0.105;
			// DAB matrix
			MODx[2]= 0.268;
			MODy[2]= 0.570;
			MODz[2]= 0.776;
		}

		if (myStain.equals("H AEC")){
			// 3-amino-9-ethylcarbazole
			// Haem matrix
			MODx[0]= 0.650;
			MODy[0]= 0.704;
			MODz[0]= 0.286;
			// AEC matrix
			MODx[1]= 0.2743;
			MODy[1]= 0.6796;
			MODz[1]= 0.6803;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("Azan-Mallory")){
			//Azocarmine and Aniline Blue (AZAN)
			// GL Blue matrix Anilline Blue
			MODx[0]= .853033;
			MODy[0]= .508733;
			MODz[0]= .112656;
			// GL Red matrix Azocarmine
			MODx[1]=0.09289875;
			MODy[1]=0.8662008;
			MODz[1]=0.49098468;
			//GL  Orange matrix Orange-G
			MODx[2]=0.10732849;
			MODy[2]=0.36765403;
			MODz[2]=0.9237484;
		}

		if (myStain.equals("Masson Trichrome")){
			// GL Methyl blue
			MODx[0]=0.7995107;
			MODy[0]=0.5913521;
			MODz[0]=0.10528667;
			// GL Ponceau Fuchsin has 2 hues, really this is only approximate
			MODx[1]=0.09997159;
			MODy[1]=0.73738605;
			MODz[1]=0.6680326;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
			// GL Iron Haematoxylin, but this does not seem to work well because it gets confused with the other 2 components
//			MODx[2]=0.6588232;
//			MODy[2]=0.66414213;
//			MODz[2]=0.3533655;
		}

		if (myStain.equals("Alcian blue & H")){
			// GL Alcian Blue matrix
			MODx[0]= 0.874622;
			MODy[0]= 0.457711;
			MODz[0]= 0.158256;
			// GL Haematox after PAS matrix
			MODx[1]= 0.552556;
			MODy[1]= 0.7544;
			MODz[1]= 0.353744;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("H PAS")){
			// GL Haem matrix
			MODx[0]= 0.644211; //0.650;
			MODy[0]= 0.716556; //0.704;
			MODz[0]= 0.266844; //0.286;
			// GL PAS matrix
			MODx[1]= 0.175411;
			MODy[1]= 0.972178;
			MODz[1]= 0.154589;
			// Zero matrix
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("RGB")){
			//R
			MODx[0]= 0.0;
			MODy[0]= 1.0;
			MODz[0]= 1.0;
			//G
			MODx[1]= 1.0;
			MODy[1]= 0.0;
			MODz[1]= 1.0;
			//B
			MODx[2]= 1.0;
			MODy[2]= 1.0;
			MODz[2]= 0.0;
		}

		if (myStain.equals("CMY")){
			//C
			MODx[0]= 1.0;
			MODy[0]= 0.0;
			MODz[0]= 0.0;
			//M
			MODx[1]= 0.0;
			MODy[1]= 1.0;
			MODz[1]= 0.0;
			//Y
			MODx[2]= 0.0;
			MODy[2]= 0.0;
			MODz[2]= 1.0;
		}

		if (myStain.equals("User values")){
			GenericDialog gd2 = new GenericDialog("User values", IJ.getInstance());
			gd2.addMessage("Colour[1]");
			gd2.addNumericField("[R1]", 0, 5);
			gd2.addNumericField("[G1]", 0, 5);
			gd2.addNumericField("[B1]", 0, 5);
			gd2.addMessage("Colour[2]");
			gd2.addNumericField("[R2]", 0, 5);
			gd2.addNumericField("[G2]", 0, 5);
			gd2.addNumericField("[B2]", 0, 5);
			gd2.addMessage("Colour[3]");
			gd2.addNumericField("[R3]", 0, 5);
			gd2.addNumericField("[G3]", 0, 5);
			gd2.addNumericField("[B3]", 0, 5);

			gd2.showDialog();
			if (gd2.wasCanceled())
				return;

			MODx[0]= gd2.getNextNumber();
			MODy[0]= gd2.getNextNumber();
			MODz[0]= gd2.getNextNumber();
			MODx[1]= gd2.getNextNumber();
			MODy[1]= gd2.getNextNumber();
			MODz[1]= gd2.getNextNumber();
			MODx[2]= gd2.getNextNumber();
			MODy[2]= gd2.getNextNumber();
			MODz[2]= gd2.getNextNumber();
		}

		if (myStain.equals("From ROI")){
			IJ.runMacro("setOption('DisablePopupMenu', true)");
			// imp.getCanvas().disablePopupMenu(true);
			double [] rgbOD = new double[3];
			for (i=0; i<3; i++){
				getmeanRGBODfromROI(i, rgbOD, imp);
				MODx[i]= rgbOD[0];
				MODy[i]= rgbOD[1];
				MODz[i]= rgbOD[2];
			}

			IJ.runMacro("setOption('DisablePopupMenu', false)");
			// imp.getCanvas().disablePopupMenu(false);

		}
		// start
		for (i=0; i<3; i++){
			//normalise vector length
			cosx[i]=cosy[i]=cosz[i]=0.0;
			len[i]=Math.sqrt(MODx[i]*MODx[i] + MODy[i]*MODy[i] + MODz[i]*MODz[i]);
			if (len[i] != 0.0){
				cosx[i]= MODx[i]/len[i];
				cosy[i]= MODy[i]/len[i];
				cosz[i]= MODz[i]/len[i];
			}
		}


		// translation matrix
		if (cosx[1]==0.0){ //2nd colour is unspecified
			if (cosy[1]==0.0){
				if (cosz[1]==0.0){
					cosx[1]=cosz[0];
					cosy[1]=cosx[0];
					cosz[1]=cosy[0];
				}
			}
		}

		if (cosx[2]==0.0){ // 3rd colour is unspecified
			if (cosy[2]==0.0){
				if (cosz[2]==0.0){
					if ((cosx[0]*cosx[0] + cosx[1]*cosx[1])> 1){
						if (doIshow)
							IJ.log("Colour_3 has a negative R component.");
						cosx[2]=0.0;
					}
					else {
						cosx[2]=Math.sqrt(1.0-(cosx[0]*cosx[0])-(cosx[1]*cosx[1]));
					}

					if ((cosy[0]*cosy[0] + cosy[1]*cosy[1])> 1){
						if (doIshow)
							IJ.log("Colour_3 has a negative G component.");
						cosy[2]=0.0;
					}
					else {
						cosy[2]=Math.sqrt(1.0-(cosy[0]*cosy[0])-(cosy[1]*cosy[1]));
					}

					if ((cosz[0]*cosz[0] + cosz[1]*cosz[1])> 1){
						if (doIshow)
							IJ.log("Colour_3 has a negative B component.");
						cosz[2]=0.0;
					}
					else {
						cosz[2]=Math.sqrt(1.0-(cosz[0]*cosz[0])-(cosz[1]*cosz[1]));
					}
				}
			}
		}

		leng=Math.sqrt(cosx[2]*cosx[2] + cosy[2]*cosy[2] + cosz[2]*cosz[2]);

		cosx[2]= cosx[2]/leng;
		cosy[2]= cosy[2]/leng;
		cosz[2]= cosz[2]/leng;

		for (i=0; i<3; i++){
			if (cosx[i] == 0.0) cosx[i] = 0.001;
			if (cosy[i] == 0.0) cosy[i] = 0.001;
			if (cosz[i] == 0.0) cosz[i] = 0.001;
		}

		if (!hideLegend) {
			ImagePlus imp0 = NewImage.createRGBImage("Colour Deconvolution", 350, 65, 1, 0);
			ImageProcessor ip0 = imp0.getProcessor();
			ip0.setFont(new Font("Monospaced", Font.BOLD, 11));
			ip0.setAntialiasedText(true);
			ip0.setColor(Color.black);
			ip0.moveTo(10,15);
			ip0.drawString("Colour deconvolution: "+myStain);
			ip0.setFont(new Font("Monospaced", Font.PLAIN, 10));

			for (i=0; i<3; i++){
				ip0.setRoi(10,18+ i*15, 14, 14); 
				ip0.setColor( 
					(((255 -(int)(255.0* cosx[i])) & 0xff)<<16)+
					(((255 -(int)(255.0* cosy[i])) & 0xff)<<8 )+
					(((255 -(int)(255.0* cosz[i])) & 0xff)    ));
				ip0.fill();
				ip0.setFont(new Font("Monospaced", Font.PLAIN, 10));
				ip0.setAntialiasedText(true);
				ip0.setColor(Color.black);
				ip0.moveTo(27,32+ i*15);
				ip0.drawString("Colour_"+(i+1)+" R:"+(float)cosx[i]+", G:"+(float)cosy[i]+", B:"+(float)cosz[i] );
			}
			imp0.show();
			imp0.updateAndDraw();
		}

/*		if (myStain.equals("From ROI")){
			IJ.showMessage("Vectors","Colour[1]:\n"+
			"  R: "+(float) cosx[0] +"\n"+
			"  G: "+(float) cosy[0] +"\n"+
			"  B: "+(float) cosz[0] +"\n \n"+
			"Colour[2]:\n"+
			"  R: "+ (float) cosx[1] +"\n"+
			"  G: "+ (float) cosy[1] +"\n"+
			"  B: "+ (float) cosz[1] +"\n \n"+
			"Colour[3]:\n"+
			"  R: "+ (float) cosx[2] +"\n"+
			"  G: "+ (float) cosy[2] +"\n"+
			"  B: "+ (float) cosz[2]);
		}
*/

		if (doIshow){
			IJ.log( myStain +" Vector Matrix ---");
			for (i=0; i<3; i++){
				IJ.log("Colour["+(i+1)+"]:\n"+
				"  R"+(i+1)+": "+ (float) MODx[i] +"\n"+
				"  G"+(i+1)+": "+ (float) MODy[i] +"\n"+
				"  B"+(i+1)+": "+ (float) MODz[i] +"\n \n");
			}

/*			IJ.log( myStain +" Translation Matrix ---");
			for (i=0; i<3; i++){
				IJ.log("Colour["+(i+1)+"]:\n"+
				"  R"+(i+1)+": "+ (float) cosx[i] +"\n"+
				"  G"+(i+1)+": "+ (float) cosy[i] +"\n"+
				"  B"+(i+1)+": "+ (float) cosz[i] +"\n \n");
			}
*/
			IJ.log( myStain +" Java code ---");
			IJ.log("\t\tif (myStain.equals(\"New_Stain\")){");
			IJ.log("\t\t// This is the New_Stain");
			for (i=0; i<3; i++){
				IJ.log("\t\t\tMODx["+i+"]="+ (float) cosx[i] +";\n"+
					"\t\t\tMODy["+i+"]="+ (float) cosy[i] +";\n"+
					"\t\t\tMODz["+i+"]="+ (float) cosz[i] +";\n\n");
			}
			IJ.log("}");
		}

		//matrix inversion
		A = cosy[1] - cosx[1] * cosy[0] / cosx[0];
		V = cosz[1] - cosx[1] * cosz[0] / cosx[0];
		C = cosz[2] - cosy[2] * V/A + cosx[2] * (V/A * cosy[0] / cosx[0] - cosz[0] / cosx[0]);
		q[2] = (-cosx[2] / cosx[0] - cosx[2] / A * cosx[1] / cosx[0] * cosy[0] / cosx[0] + cosy[2] / A * cosx[1] / cosx[0]) / C;
		q[1] = -q[2] * V / A - cosx[1] / (cosx[0] * A);
		q[0] = 1.0 / cosx[0] - q[1] * cosy[0] / cosx[0] - q[2] * cosz[0] / cosx[0];
		q[5] = (-cosy[2] / A + cosx[2] / A * cosy[0] / cosx[0]) / C;
		q[4] = -q[5] * V / A + 1.0 / A;
		q[3] = -q[4] * cosy[0] / cosx[0] - q[5] * cosz[0] / cosx[0];
		q[8] = 1.0 / C;
		q[7] = -q[8] * V / A;
		q[6] = -q[7] * cosy[0] / cosx[0] - q[8] * cosz[0] / cosx[0];

                // initialize 3 output colour stacks
                ImageStack[] outputstack = new ImageStack[3];
		for (i=0; i<3; i++){
			for (j=0; j<256; j++) { //LUT[1]
				//if (cosx[i] < 0)
				//	rLUT[255-j]=(byte)(255.0 + (double)j * cosx[i]);
				//else
					rLUT[255-j]=(byte)(255.0 - j * cosx[i]);

				//if (cosy[i] < 0)
				//	gLUT[255-j]=(byte)(255.0 + (double)j * cosy[i]);
				//else
					gLUT[255-j]=(byte)(255.0 - j * cosy[i]);

				//if (cosz[i] < 0)
				//	bLUT[255-j]=(byte)(255.0 + (double)j * cosz[i]);
				///else
					bLUT[255-j]=(byte)(255.0 - j * cosz[i]);
			}
			IndexColorModel cm = new IndexColorModel(8, 256, rLUT, gLUT, bLUT);
                        outputstack[i] = new ImageStack(width, height, cm);
                }

		// translate ------------------
		int imagesize = width * height;
		for (int imagenum=1; imagenum<=stack.getSize(); imagenum++) {
			int[] pixels = (int[])stack.getPixels(imagenum);
			String label = stack.getSliceLabel(imagenum);
			byte[][] newpixels = new byte[3][];
			newpixels[0] = new byte[imagesize];
			newpixels[1] = new byte[imagesize];
			newpixels[2] = new byte[imagesize];

			for (j=0;j<imagesize;j++){
				 // log transform the RGB data
				int R = (pixels[j] & 0xff0000)>>16;
				int G = (pixels[j] & 0x00ff00)>>8 ;
				int B = (pixels[j] & 0x0000ff);
				double Rlog = -((255.0*Math.log(((double)R+1)/255.0))/log255);
				double Glog = -((255.0*Math.log(((double)G+1)/255.0))/log255);
				double Blog = -((255.0*Math.log(((double)B+1)/255.0))/log255);
				for (i=0; i<3; i++){
					// rescale to match original paper values
					double Rscaled = Rlog * q[i*3];
					double Gscaled = Glog * q[i*3+1];
					double Bscaled = Blog * q[i*3+2];
					double output = Math.exp(-((Rscaled + Gscaled + Bscaled) - 255.0) * log255 / 255.0);
					if(output>255) output=255;
					newpixels[i][j]=(byte)(0xff&(int)(Math.floor(output+.5)));
				}
			}
			 // add new values to output images
			outputstack[0].addSlice(label,newpixels[0]);
			outputstack[1].addSlice(label,newpixels[1]);
			outputstack[2].addSlice(label,newpixels[2]);
		}
		new ImagePlus(title+"-(Colour_1)",outputstack[0]).show();
		new ImagePlus(title+"-(Colour_2)",outputstack[1]).show();
		new ImagePlus(title+"-(Colour_3)",outputstack[2]).show();
	}

	void getmeanRGBODfromROI(int i, double [] rgbOD, ImagePlus imp){
		//get a ROI and its mean optical density. GL
		int [] xyzf = new int [4]; //[0]=x, [1]=y, [2]=z, [3]=flags
		int x1, y1, x2, y2, h=0, w=0, px=0, py=0, x, y,p;
		double log255=Math.log(255.0);
		ImageProcessor ip = imp.getProcessor();
		int mw = ip.getWidth()-1;
		int mh = ip.getHeight()-1;

		IJ.showMessage("Select ROI for Colour_"+(i+1)+".\n \n(Right-click to end)");
		getCursorLoc( xyzf, imp );
		while ((xyzf[3] & 4) !=0){  //trap until right released
			getCursorLoc( xyzf, imp );
			IJ.wait(20);
		}

		while (((xyzf[3] & 16) == 0) && ((xyzf[3] & 4) ==0)) { //trap until one is pressed
			getCursorLoc( xyzf, imp );
			IJ.wait(20);
		}

		rgbOD[0]=0;
		rgbOD[1]=0;
		rgbOD[2]=0;

		if ((xyzf[3] & 4) == 0){// right was not pressed, but left (ROI) was
			x1=xyzf[0];
			y1=xyzf[1];
			//IJ.write("first point x:" + x1 + "  y:" + y1);
			x2=x1;  y2=y1;
			while ((xyzf[3] & 4) == 0){//until right pressed
				getCursorLoc( xyzf, imp );
				if (xyzf[0]!=x2 || xyzf[1]!=y2) {
					if (xyzf[0]<0) xyzf[0]=0;
					if (xyzf[1]<0) xyzf[1]=0;
					if (xyzf[0]>mw) xyzf[0]=mw;
					if (xyzf[1]>mh) xyzf[1]=mh;
					x2=xyzf[0]; y2=xyzf[1];
					w=x2-x1+1;
					h=y2-y1+1;
					if (x2<x1) {px=x2;  w=(x1-x2)+1;} else px=x1;
					if (y2<y1) {py=y2;  h=(y1-y2)+1;} else py=y1;
					IJ.makeRectangle(px, py, w, h);
					//IJ.write("Box x:" + x2 +"  y:" + y2+" w:"+w+" h:"+h);
				}
				IJ.wait(20);
			}
			while ((xyzf[3] & 16) !=0){  //trap until left released
				getCursorLoc( xyzf, imp );
				IJ.wait(20);
			}

			for (x=px;x<(px+w);x++){
				for(y=py;y<(py+h);y++){
					p=ip.getPixel(x,y);
					// rescale to match original paper values
					rgbOD[0] = rgbOD[0]+ (-((255.0*Math.log(((double)((p & 0xff0000)>>16)+1)/255.0))/log255));
					rgbOD[1] = rgbOD[1]+ (-((255.0*Math.log(((double)((p & 0x00ff00)>> 8) +1)/255.0))/log255));
					rgbOD[2] = rgbOD[2]+ (-((255.0*Math.log(((double)((p & 0x0000ff))        +1)/255.0))/log255));
				}
			}
			rgbOD[0] = rgbOD[0] / (w*h);
			rgbOD[1] = rgbOD[1] / (w*h);
			rgbOD[2] = rgbOD[2] / (w*h);
		}
		IJ.run("Select None");
	}


	void getCursorLoc(int [] xyzf, ImagePlus imp ) {
		ImageWindow win = imp.getWindow();
		ImageCanvas ic = win.getCanvas();
		Point p = ic.getCursorLoc();
		xyzf[0]=p.x;
		xyzf[1]=p.y;
		xyzf[2]=imp.getCurrentSlice()-1;
		xyzf[3]=ic.getModifiers();
	}

}

