package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class gets resources by identifier. All localized messages are stored in
 * a {@link ResourceBundle}.
 * 
 * @author Philipp Kainz
 * 
 */
public class I18N {

	/**
	 * Specify the {@link ResourceBundle} for the messages, use the fully
	 * qualified name of the <code>messages.properties</code> file located in
	 * <code>/src/main/resources/</code>.
	 */
	private static final ResourceBundle MESSAGES_BUNDLE = ResourceBundle
			.getBundle("feat.lbp.i18n.messages");

	/**
	 * This method fetches a String Object from a given String <code>key</code>
	 * in a specific resource file addressed by ResourceBundle <code>rb</code>.
	 * It returns '!key!' if the required value is not found for the key.
	 * 
	 * @param key
	 * @param rb
	 * @return value or '!'+<code>key</code>+'!'.
	 */
	private static String getString(final String key, ResourceBundle rb) {
		try {
			return rb.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * This method fetches a String Object from a given String <code>key</code>
	 * in a specific resource file addressed by ResourceBundle
	 * <code>MESSAGES_BUNDLE</code>. It returns '!key!' if the required value is
	 * not found for the key.
	 * 
	 * @param key
	 * @return value, or !key!, if the value is not found
	 */
	public static String getMessage(final String key) {
		return getString(key, MESSAGES_BUNDLE);
	}

	/**
	 * This method fetches a String Object from a given String <code>key</code>
	 * in a specific resource file addressed by ResourceBundle
	 * <code>MESSAGES_BUNDLE</code>. Additionally an Object...
	 * <code>params</code> can be specified. It returns the phrase or '!'+
	 * <code>key</code>+'!'. for the key.
	 * 
	 * @param key
	 * @return phrase or '!'+<code>key</code>+'!'.
	 */
	public static String getMessage(final String key, Object... params) {
		String phrase = getString(key, MESSAGES_BUNDLE);

		if (params != null && params.length > 0) {
			return MessageFormat.format(phrase, params);
		} else {
			return phrase;
		}
	}

}
