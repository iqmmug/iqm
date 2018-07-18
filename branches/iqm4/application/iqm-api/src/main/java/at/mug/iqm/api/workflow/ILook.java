package at.mug.iqm.api.workflow;

/*
 * #%L
 * Project: IQM - API
 * File: ILook.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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


import java.awt.image.RenderedImage;
import java.util.ArrayList;

import javax.media.jai.PlanarImage;

import at.mug.iqm.api.gui.ILookPanel;

public interface ILook {

	void removeLookPanel(ILookPanel lookPanel);

	void addLookPanel(ILookPanel lookPanel);

	void setLookPanels(ArrayList<ILookPanel> lookPanels);

	ArrayList<ILookPanel> getLookPanels();

	void reset();

	void setImage(RenderedImage pi);

	PlanarImage getCurrentImage();

	void createExtraLookFrame();

	void setCurrentLookPanel(ILookPanel lookPanel);

	ILookPanel getCurrentLookPanel();

}
