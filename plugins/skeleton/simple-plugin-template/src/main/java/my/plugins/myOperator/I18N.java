package my.plugins.myOperator;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class gets resources by identifier. All localized messages are stored in
 * a {@link ResourceBundle}.
 * 
 * @author Philipp Kainz
 */
public class I18N {

	/**
	 * Specify the {@link ResourceBundle} for the messages, use the fully
	 * qualified name of the <code>messages.properties</code> file located in
	 * <code>/src/main/resources/</code>.
	 */
	private static final ResourceBundle MESSAGES_BUNDLE = ResourceBundle
			.getBundle("my.plugins.myOperator.i18n.messages");

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
