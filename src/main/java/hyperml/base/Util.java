package hyperml.base;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * Utils.
 * 
 * @author krizz
 */
public class Util {

	private Util() {
	}

	/**
	 * HTML/XML escape's the given text.
	 * 
	 * @param text
	 * @return Escaped text
	 */
	public static String escapeHtmlXml(String text) {
		StringBuilder sb = null;
		for (int i = 0, n = text.length(); i < n; i++) {
			String repl = null;
			char c = text.charAt(i);
			switch (c) {
			case '"':
				repl = "&quot;";
				break;
			case '&':
				repl = "&amp;";
				break;
			case '<':
				repl = "&lt;";
				break;
			case '>':
				repl = "&gt;";
				break;
			default:
				if (sb != null) {
					sb.append(c);
				}
			}

			if (repl != null) {
				if (sb == null) {
					sb = new StringBuilder(text.substring(0, i));
				}
				sb.append(repl);
			}
		}
		return sb != null ? sb.toString() : text;
	}

	/**
	 * Flattens structural items, such as an array, into a single list.
	 * <ul>
	 * <li>If the item is an array, iterable or stream, adds those items to the
	 * list.</li>
	 * <li>If the item is a {@link Map} adds its entries to the list</li>
	 * <li>If the item is a {@link Entry} adds its key and value to the list</li>
	 * </ul>
	 * Except for Map/Entry, the algorithm is perfomed recursively.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map = new HashMap&lt;&gt;();
	 * map.put(&quot;color&quot;, &quot;red&quot;);
	 * 
	 * assertThat(FlatList.flatten("a", map)).isEqualTo(new Object[] { "a", "color", "red" });
	 * </pre>
	 * 
	 * @author krizz
	 */
	public static Object[] flatten(Object... items) {
		List<Object> list = new ArrayList<>();
		for (Object it : items) {
			add(list, it);
		}
		return list.toArray();
	}

	private static void add(List<Object> list, Object it) {
		if (isArray(it)) {
			for (int i = 0, n = Array.getLength(it); i < n; i++) {
				add(list, Array.get(it, i));
			}
		} else if (it instanceof Iterable) {
			addIter(list, ((Iterable<?>) it).iterator());
		} else if (it instanceof Iterator) {
			addIter(list, (Iterator<?>) it);
		} else if (it instanceof Map) {
			addIter(list, ((Map<?, ?>) it).entrySet()
					.iterator());
		} else if (it instanceof Entry) {
			Entry<?, ?> e = (Entry<?, ?>) it;
			// do not flatten
			list.add(e.getKey());
			list.add(e.getValue());
		} else if (it instanceof Stream) {
			addIter(list, ((Stream<?>) it).iterator());
		} else {
			list.add(it); // may be null
		}
	}

	private static void addIter(List<Object> list, Iterator<?> iter) {
		while (iter.hasNext()) {
			add(list, iter.next());
		}
	}

	private static boolean isArray(Object obj) {
		return obj != null && obj.getClass()
				.isArray();
	}
}
