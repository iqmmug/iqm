package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: SingleProcessingTask.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.media.jai.PlanarImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IImageInvertible;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorFactory;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.operator.WorkPackage;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.IExplicitProcessingTask;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.img.bundle.descriptors.IqmOpInvertDescriptor;
import at.mug.iqm.img.bundle.op.IqmOpInvert;

/**
 * This class performs a processing task on a single image. The image
 * calculation can only be performed in RAM, hence, no <code>virtual</code> mode
 * is possible.
 * 
 * @author Philipp Kainz
 */
public class SingleProcessingTask extends AbstractProcessingTask implements
		IExplicitProcessingTask {
	// class specific logger
	private Class<?> caller = SingleProcessingTask.class;
	private static final Logger logger = LogManager.getLogger(SingleProcessingTask.class);

	public SingleProcessingTask() {
		this.setHeadless(true);
		this.setVirtual(false);
	}

	/**
	 * This constructor sets the required variables directly.
	 * <code>headless</code> will be set to <code>true</code>. This constructor
	 * shall be used when no GUI is needed to be updated. <code>virtual</code>
	 * is always <code>false</code>.
	 */
	public SingleProcessingTask(IWorkPackage wp) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}

		this.setHeadless(true);
		this.setVirtual(false);
	}

	/**
	 * This constructor sets the required variables directly.
	 * <code>headless</code> will be set to <code>false</code>. This constructor
	 * shall be used when a GUI is needed to be updated. <code>virtual</code> is
	 * always <code>false</code>.
	 */
	public SingleProcessingTask(IWorkPackage wp, IOperatorGUI opGUI) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}
		this.setOperatorGUI(opGUI);

		this.setHeadless(false);
		this.setVirtual(false);
	}

	/**
	 * This method performs the actual calculation on a single image. Multiple
	 * image processing routines might utilize this method in a loop.
	 * 
	 * @return IqmDataBox - a resulting {@link IqmDataBox} object containing the
	 *         processed image/file
	 * @throws Exception
	 *             if the requested operator is not defined
	 */
	@Override
	public IResult processExplicit() throws Exception {
		this.startTime = System.currentTimeMillis();

		// System.out.println("Parent Task: " + this.getParentTask());
		// System.out.println(Thread.currentThread().getName());

		Result result = new Result();

		try {
			// forward the parent task reference to the operator, important for
			// cancellation.
			this.getOperator().setParentTask(this.getParentTask());
			// block here, run and wait for the result
			result = (Result) this.getOperator().run(this.getWorkPackage());

			if (result == null) {
				logger.debug("Result is null, operator may have been cancelled.");
				return null;
			}
			logger.trace("Finished operator '" + this.getOperator().getName()
					+ "'");

			try {
				// INVERSION
				if (!this.isHeadless()) {
					// react according to return type of the result content
					if (result != null && result.hasImages()) {
						// invert image(s), if selected
						if (getOperatorGUI() != null
								&& (getOperatorGUI() instanceof IImageInvertible)
								&& ((IImageInvertible) getOperatorGUI())
										.isInvertSelected()) {
							for (IqmDataBox box : result.listImageResults()) {
								logger.debug("Inverting image: "
										+ box.getImageModel().getModelName());

								String opName = new IqmOpInvertDescriptor()
										.getName();
								IqmOpInvert op = (IqmOpInvert) OperatorFactory
										.createOperator(opName);

								WorkPackage wp = WorkPackage.create(opName);
								wp.addSource(box);
								PlanarImage invertedImage = op.run(wp)
										.listImageResults().get(0).getImage();

								box.setImage(invertedImage);
							}
						}
					}
				}
			} catch (ClassCastException cce) {
				DialogUtil
						.getInstance()
						.showErrorMessage(
								I18N.getMessage("application.iinvertable.notSupported"),
								cce);
			}

			this.duration = System.currentTimeMillis() - this.startTime;
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("HHH:mm:ss:SSS");
			BoardPanel.appendTextln(
					"Processing finished, elapsed time: "
							+ sdf.format(this.duration), caller);
			logger.trace("Done single processing.");
		} catch (Exception e) {
			logger.error("An error occured: ", e);
			BoardPanel.appendTextln("Processing failed.", caller);
			DialogUtil.getInstance().showErrorMessage(
					"Image or plot processing failed!", e, true);
			result = null;
		}

		return result;
	}

	/**
	 * The invocation of {@link #execute()} causes the creation of a new
	 * background swing worker. The {@link #get()} method has to be called in
	 * order to block on the calling thread and wait for the result. If you want
	 * to do this in one statement, use {@link #processExplicit()} and the
	 * calculation will be performed in the same thread as the call occurs.
	 * 
	 * @return an {@link IqmDataBox}, which is the result of
	 *         {@link #processExplicit()}
	 */
	@Override
	protected IResult doInBackground() throws Exception {
		return this.processExplicit();
	}
}
