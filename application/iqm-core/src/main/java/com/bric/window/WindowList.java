/*
 * @(#)WindowList.java
 *
 * $Date$
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */

package com.bric.window;

/*
 * #%L
 * Project: IQM - Application Core
 * File: WindowList.java
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

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This keeps track of the origin and layering of windows and frames within this
 * JVM.
 * <P>
 * By "origin" I mean: when that window was first activated. This serves as a
 * running list of windows in the order they were used.
 * <P>
 * All windows are tracked by <code>WeakReferences</code>, just to make sure
 * this static monitor doesn't accidentally let windows linger in memory that
 * should otherwise be marked for garbage collection.
 * <P>
 * The layering is monitored by an <code>AWTEventListener</code> that listens
 * for all <code>WindowEvent.WINDOW_ACTIVATED</code> events. Whenever a window
 * is activated: it gets put on the top of the stack of windows.
 * <P>
 * When any change occurs to any of the lists in this class: the
 * <code>ChangeListeners</code> are notified.
 * 
 */
public class WindowList {

	private static ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	private static AWTEventListener windowListener = new AWTEventListener() {
		WeakReference<?>[] visibleWindows = new WeakReference<?>[0];
		WeakReference<?>[] invisibleWindows = new WeakReference<?>[0];
		WeakReference<?>[] visibleFrames = new WeakReference<?>[0];
		WeakReference<?>[] invisibleFrames = new WeakReference<?>[0];
		WeakReference<?>[] iconifiedFrames = new WeakReference<?>[0];

		public void eventDispatched(AWTEvent e) {
			if (e instanceof WindowEvent) {

				boolean changed = false;

				if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
					Window window = (Window) (e.getSource());

					updateWindowList: {
						for (int a = 0; a < windowList.size(); a++) {
							Reference<Window> r = windowList.get(a);
							if (r.get() == window) {
								break updateWindowList;
							}
						}
						windowList.add(new WeakReference<Window>(window));
						changed = true;
					}

					updateWindowLayerList: {
						int a = 0;
						while (a < windowLayerList.size()) {
							Reference<Window> r = windowLayerList.get(a);
							if (r.get() == window) {
								if (a == windowLayerList.size() - 1) {
									break updateWindowLayerList;
								}
								windowLayerList.remove(a);
							} else {
								a++;
							}
						}
						windowLayerList.add(new WeakReference<Window>(window));
						changed = true;
					}
				}

				if (changed) {
					fireChangeListeners();
				}

				proofRunnable.run();

				/**
				 * Run this test later. If we received a WINDOW_CLOSING event,
				 * then a window is not yet invisible... but it will by the time
				 * this runnable runs.
				 */
				SwingUtilities.invokeLater(proofRunnable);
			}
		}

		Runnable proofRunnable = new Runnable() {
			public void run() {
				boolean changed = false;

				Window[] newVisibleWindows = getWindows(false, false);
				Window[] newInvisibleWindows = getWindows(false, true);
				Frame[] newVisibleFrames = getFrames(false, false, false);
				Frame[] newInvisibleFrames = getFrames(false, true, false);
				Frame[] newIconifiedFrames = getFrames(false, false, true);

				if (arrayEquals(newVisibleWindows, visibleWindows) == false)
					changed = true;
				if (arrayEquals(newInvisibleWindows, invisibleWindows) == false)
					changed = true;
				if (arrayEquals(newVisibleFrames, visibleFrames) == false)
					changed = true;
				if (arrayEquals(newInvisibleFrames, invisibleFrames) == false)
					changed = true;
				if (arrayEquals(newIconifiedFrames, iconifiedFrames) == false)
					changed = true;

				visibleWindows = wrap(newVisibleWindows);
				invisibleWindows = wrap(newInvisibleWindows);
				visibleFrames = wrap(newVisibleFrames);
				invisibleFrames = wrap(newInvisibleFrames);
				iconifiedFrames = wrap(newIconifiedFrames);

				if (changed) {
					fireChangeListeners();
				}
			}
		};

		private WeakReference<?>[] wrap(Object[] array) {
			WeakReference<?>[] references = new WeakReference[array.length];
			for (int a = 0; a < references.length; a++) {
				references[a] = new WeakReference<Object>(array[a]);
			}
			return references;
		}

		private boolean arrayEquals(Object[] obj1, WeakReference<?>[] obj2) {
			if (obj1.length != obj2.length)
				return false;
			for (int a = 0; a < obj1.length; a++) {
				if (obj1[a] != obj2[a].get())
					return false;
			}
			return true;
		}
	};

	/**
	 * References to every Window that has been activated, in order of when they
	 * were made active.
	 */
	private static ArrayList<WeakReference<Window>> windowLayerList = new ArrayList<WeakReference<Window>>();
	/**
	 * References to every Window that has been activated, in order of
	 * z-layering.
	 */
	private static ArrayList<WeakReference<Window>> windowList = new ArrayList<WeakReference<Window>>();

	static {
		Toolkit.getDefaultToolkit().addAWTEventListener(windowListener,
				AWTEvent.WINDOW_EVENT_MASK);
	}

	/**
	 * Returns a list of windows.
	 * 
	 * @param sortByLayer
	 *            whether to sort by layer or origin. If this is true, then the
	 *            lowest index corresponds to the window farthest behind; the
	 *            highest index represents the highest window. If this is false,
	 *            then the lowest index corresponds to the first window
	 *            activated, and the highest index is the most recently
	 *            activated window.
	 * @param includeInvisible
	 *            if this is false then only visible Windows will be returned.
	 *            Otherwise all Windows will be returned.
	 */
	public static Window[] getWindows(boolean sortByLayer,
			boolean includeInvisible) {
		ArrayList<WeakReference<Window>> list = sortByLayer ? windowLayerList
				: windowList;
		Vector<Window> returnValue = new Vector<Window>();
		int a = 0;
		while (a < list.size()) {
			WeakReference<Window> r = list.get(a);
			Window w = r.get();
			if (w == null) {
				list.remove(a);
			} else {
				if (includeInvisible || w.isVisible()) {
					returnValue.add(w);
				}
				a++;
			}
		}
		return returnValue.toArray(new Window[returnValue.size()]);
	}

	/**
	 * Returns a list of frames.
	 * 
	 * @param sortByLayer
	 *            whether to sort by layer or origin. If this is true, then the
	 *            lowest index corresponds to the frame farthest behind; the
	 *            highest index represents the highest frame. If this is false,
	 *            then the lowest index corresponds to the first frame
	 *            activated, and the highest index is the most recently
	 *            activated frame.
	 * @param includeInvisible
	 *            if this is false then visible Frames will be returned. If this
	 *            is true then all Frames will be returned, so the next argument
	 *            is meaningless.
	 * @param includeIconified
	 *            if this is true then iconified Frames will be returned.
	 */
	public static Frame[] getFrames(boolean sortByLayer,
			boolean includeInvisible, boolean includeIconified) {
		ArrayList<WeakReference<Window>> list = sortByLayer ? windowLayerList
				: windowList;
		Vector<Frame> returnValue = new Vector<Frame>();
		int a = 0;
		while (a < list.size()) {
			WeakReference<Window> r = list.get(a);
			Window w = r.get();
			if (w == null) {
				list.remove(a);
			} else {
				if (w instanceof Frame) {
					Frame f = (Frame) w;
					if (includeInvisible || f.isVisible()) {
						returnValue.add(f);
					} else if (includeIconified
							&& f.getExtendedState() == Frame.ICONIFIED) {
						returnValue.add(f);
					}
				}
				a++;
			}
		}
		return returnValue.toArray(new Frame[returnValue.size()]);
	}

	/**
	 * Add a ChangeListener.
	 * <P>
	 * This listener will be notified when new windows are activated, layering
	 * of windows changes, or windows close.
	 * 
	 * @param l
	 *            a new ChangeListener.
	 */
	public static void addChangeListener(ChangeListener l) {
		if (changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}

	/**
	 * Remove a ChangeListener.
	 */
	public static void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}

	static void fireChangeListeners() {
		for (int a = 0; a < changeListeners.size(); a++) {
			ChangeListener l = changeListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(WindowList.class));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private WindowList() {
	}
}
