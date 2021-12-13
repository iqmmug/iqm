package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpTempMatch.java
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


import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpTempMatchDescriptor;
import at.mug.iqm.img.bundle.jai.stuff.TemplateMatchingWithIterators;
import at.mug.iqm.img.bundle.jai.stuff.TemplateMatchingWithRasters;

/**
 * This an implementation of a JAISTUFF algorithm by R.Santos
 * https://jaistuff.dev.java.net/algorithms.html
 * 
 * @author Ahammer, Kainz
 * @since 2009 05
 */

public class IqmOpTempMatch extends AbstractOperator {

	public IqmOpTempMatch() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
		this.setCancelable(true);
	}

	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();
		
		int method = pb.getIntParameter(0);
		String imageName = (String) pi1.getProperty("image_name");
		String fileName = String.valueOf(pi1.getProperty("file_name"));

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		if (method == 0) { // Raster
			TemplateMatchingWithRasters tm = new TemplateMatchingWithRasters(
					pi1, pi2);
			tm.addProgressListener(this.getProgressListeners("operatorProgress")[0], "operatorProgress");
			tm.setCancelable(this.isCancelable());
			tm.setParentTask(this.getParentTask());
			tm.run();
			piOut = tm.getOutput();
		}
		if (method == 01) { // Iterator
			TemplateMatchingWithIterators tm = new TemplateMatchingWithIterators(
					pi1, pi2);
			tm.addProgressListener(this.getProgressListeners("operatorProgress")[0], "operatorProgress");
			tm.setCancelable(this.isCancelable());
			tm.setParentTask(this.getParentTask());
			tm.run();
			piOut = tm.getOutput();
		}
		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName);		
		im.setModelName(imageName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpTempMatchDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpTempMatchDescriptor.TYPE;
	}
}
