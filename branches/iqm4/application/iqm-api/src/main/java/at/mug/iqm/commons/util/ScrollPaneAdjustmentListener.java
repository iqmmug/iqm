package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: ScrollPaneAdjustmentListener.java
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

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollPane;

/**
 * This class is responsible for reacting to changing adjustment values of e.g.
 * a {@link JScrollPane}.
 * 
 * @author Philipp Kainz
 * 
 */
public class ScrollPaneAdjustmentListener implements AdjustmentListener {

	@Override
	public void adjustmentValueChanged(AdjustmentEvent evt) {

		// get the source of the event
		Adjustable source = evt.getAdjustable();

		// check if user is currently dragging the scrollbar's knob
		if (evt.getValueIsAdjusting()) {
			return;
		}

		// get the orientation of the adjustable object
		int orientation = source.getOrientation();

		if (orientation == Adjustable.HORIZONTAL) {
			System.out.println("Event from horizontal scrollbar");
		}

		else {
			System.out.println("Event from vertical scrollbar");
		}

		// get the type of adjustment which caused the value changed event

//		int type = evt.getAdjustmentType();

//		switch (type) {
//		case AdjustmentEvent.UNIT_INCREMENT:
//			System.out.println("increased by one unit");
//			break;
//
//		case AdjustmentEvent.UNIT_DECREMENT:
//			System.out.println("decreased by one unit");
//			break;
//
//		case AdjustmentEvent.BLOCK_INCREMENT:
//			System.out.println("increased by one block");
//			break;
//
//		case AdjustmentEvent.BLOCK_DECREMENT:
//			System.out.println("decreased by one block");
//			break;
//		case AdjustmentEvent.TRACK:
//			System.out.println("knob on the scrollbar was dragged");
//			break;
//
//		}

		// get the current value in the adjustment event
//		int value = evt.getValue();
//		System.out.println("Current Value: " + value);
	}
}
