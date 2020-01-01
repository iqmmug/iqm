package at.mug.iqm.core.processing.loader;

/*
 * #%L
 * Project: IQM - Application Core
 * File: SynchronousTankItemLoader.java
 * 
 * $Id: SynchronousTankItemLoader.java 505 2015-01-09 09:19:54Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-core/src/main/java/at/mug/iqm/core/processing/loader/TankItemLoader.java $
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

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.LookPanel;
import at.mug.iqm.gui.PlotPanel;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This loader is responsible for <b>synchronously</b> assembling items a given
 * {@link List} of {@link IqmDataBox}es to the {@link DefaultListModel} in the
 * {@link TankPanel} . This class is called by the {@link Tank}'s methods, e.g.
 * when the user double clicks on the {@link JList} items in order to show an
 * item in the {@link LookPanel} or {@link PlotPanel}.
 * 
 * @see TankItemLoader
 * 
 * @author Philipp Kainz
 * @since 3.2.2
 */
public class SynchronousTankItemLoader extends TankItemLoader {

	/**
	 * Logging variable.
	 */
	private static final Logger logger = LogManager.getLogger(SynchronousTankItemLoader.class);

	/**
	 * The constructor.
	 * 
	 * @param list
	 * @param replaceItem
	 * @param replaceIndex
	 */
	public SynchronousTankItemLoader(List<IqmDataBox> list,
			boolean replaceItem, int replaceIndex) {
		super(list, replaceItem, replaceIndex);
	}

	/**
	 * Uses existing methods of the {@link TankItemLoader} superclass for
	 * synchronous item loading to the tank.
	 */
	public void load() {
		logger.info("Loading items to tank synchronously");
		try {
			List<IqmDataBox> list = super.doInBackground();
			super.setTankAndManager(list);
		} catch (Exception e) {
			//DialogUtil.getInstance().showErrorMessage("Cannot load tank items synchronously!", e, true);
			logger.error(e);			
		} finally {
			GUITools.getStatusPanel().resetProgressBarValueStack();
		}
	}
}
