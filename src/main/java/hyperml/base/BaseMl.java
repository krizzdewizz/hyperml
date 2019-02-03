package hyperml.base;

import static hyperml.base.Util.escapeHtmlXml;
import static hyperml.base.Util.flatten;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hyperml.HyperMlException;

/**
 * Writes arbitrary HTML/XML with only the methods {@link #$(Object, Object...)}
 * and {@link #text(Object...)}.
 * <p>
 * <code>$()</code> expects its parameters as follows:
 * <p>
 * Example:
 * 
 * <pre>
 * $("html");
 * {
 *   $("body", "onload", "doThings()");               // attribute name-value pairs
 *   {
 *      $("h1", "class", "title", "hello world", $);  // with text content, $ --&gt; 'short close' 
 *   }
 *   $(); // body                                     // no parameters --&gt; end element
 * }
 * $(); // html
 * 
 * --&gt;
 * 
 * &lt;html&gt;
 *   &lt;body onload=&quot;doThings()&quot;&gt;
 *     &lt;h1 class=&quot;title&quot;&gt;hello world&lt;/h1&gt;
 *   &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 * 
 * @author krizzdewizz
 */
public abstract class BaseMl<T extends BaseMl<?>> {

	public BaseMl() {
		this(new StringWriter());
	}

	public BaseMl(Writer writer) {
		this.writer = writer;
	}

	public BaseMl(OutputStream out) {
		this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
	}

	/**
	 * If given as last argument to {@link #$(Object, Object...)}, will call
	 * {@link #$()} just after starting the element (auto-end). Resembles XML short
	 * closing of tags like <code>&lt;x/&gt;</code> --&gt; <code>$("x", $)</code>.
	 */
	public static final Object $ = new Object();

	/**
	 * Returns an attribute name/value pair which can directly be put on
	 * {@link #$(Object, Object...)}.
	 * 
	 * @param name  Name of the attribute
	 * @param value Value of the attribute
	 * @return Pair
	 */
	public static Pair<String, Object> attr(String name, Object value) {
		return Pair.of(name, value);
	}

	/**
	 * Returns the key/value pairs array as a map.
	 * 
	 * @param keyValuePairs
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> map(Object... keyValuePairs) {
		Map<K, V> map = new LinkedHashMap<>();
		for (int i = 0, n = keyValuePairs.length; i < n; i += 2) {
			map.put((K) keyValuePairs[i], (V) keyValuePairs[i + 1]);
		}
		return map;
	}

	/**
	 * Returns the items as a list.
	 * 
	 * @param items Items
	 * @return List
	 */
	@SafeVarargs
	public static <T> List<T> list(T... items) {
		return asList(items);
	}

	/**
	 * Returns the items as a set.
	 * 
	 * @param items Items
	 * @return Set
	 */
	@SafeVarargs
	public static <T> Set<T> set(T... items) {
		return new LinkedHashSet<T>(list(items));
	}

	protected static class ParamInfo<T> {
		public final T obj;
		public final ParamsHandler<T> handler;
		public final String elementName;
		public final Object[] params;

		public ParamInfo(String elementName, Object[] params, T obj, ParamsHandler<T> handler) {
			this.handler = handler;
			this.elementName = elementName;
			this.obj = obj;
			this.params = params;
		}
	}

	protected interface ParamsHandler<T> {
		ParamInfo<T> init(Object elementName, Object... params);

		boolean applyAttribute(T obj, String name, Object value);

		void endElementHead(T obj);

		void start(T obj);

		void end(T obj);
	}

	private static final ParamsHandler<Object> NULL_HANDLER = new ParamsHandler<Object>() {

		@Override
		public ParamInfo<Object> init(Object elementName, Object... params) {
			return null;
		}

		@Override
		public boolean applyAttribute(Object obj, String name, Object value) {
			return false;
		}

		@Override
		public void endElementHead(Object o) {
		}

		@Override
		public void start(Object obj) {
		}

		@Override
		public void end(Object obj) {
		}
	};

	protected static String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	/**
	 * Stack of element names started so far.
	 */
	protected final LinkedList<Object> stack = new LinkedList<>();

	protected Writer writer;
	private boolean written;

	protected abstract boolean isVoidElement(String name);

	protected abstract boolean escapeText();

	protected boolean writeAttribute(@SuppressWarnings("unused") Object value) {
		return true;
	}

	protected boolean writeAttributeValue(@SuppressWarnings("unused") Object value) {
		return true;
	}

	/**
	 * Builds the xml by transforming it to the given writer.
	 * <p>
	 * May be called several times.
	 * 
	 * @param out destination
	 */
	public void build(Writer out) {
		Writer prevWriter = writer;
		try {
			writer = out;
			create();
			checkStack();
		} finally {
			writer = prevWriter;
		}
	}

	/**
	 * Builds the xml by transforming it to the given output stream using UTF-8
	 * encoding.
	 * <p>
	 * May be called several times.
	 * 
	 * @param out destination
	 */
	public void build(OutputStream out) {
		build(new OutputStreamWriter(out, StandardCharsets.UTF_8));
	}

	@Override
	public String toString() {
		if (written) {
			// fluent mode
			checkStack();
			return writer.toString();
		}
		StringWriter sw = new StringWriter();
		build(sw);
		written = false;
		return sw.toString();
	}

	/**
	 * Maybe overridden by subclasses.
	 */
	protected void create() {
	}

	/**
	 * Checks that the name stack is empty upon endDocument().
	 * 
	 * @throws HyperXmlException if the name stack is not empty
	 */
	private void checkStack() {
		if (stack.isEmpty()) {
			return;
		}

		throw new HyperMlException("Missing end element call $(). Names left on stack: '%s'", stack.stream()
				.map(Object::toString)
				.collect(joining(", ")));
	}

	/**
	 * Outputs the given text raw/unescaped.
	 * 
	 * @param texts The text to output. The last item may be {@link #$}, in which
	 *              case the element is ended.
	 */
	public T raw(Object... texts) {
		return textInternal(texts, false);
	}

	/**
	 * Outputs the given text.
	 * 
	 * @param texts The text to output. The last item may be {@link #$}, in which
	 *              case the element is ended.
	 */
	public T text(Object... texts) {
		return textInternal(texts, escapeText());
	}

	protected T textInternal(Object[] texts, boolean escapeText) {
		int nTexts = texts.length;
		if (nTexts == 0) {
			return _this();
		}
		boolean hasEnd = texts[nTexts - 1] == $;
		for (int i = 0, n = hasEnd ? nTexts - 1 : nTexts; i < n; i++) {
			Object text = texts[i];
			if (text != null) {
				_text(text.toString(), escapeText);
			}
		}
		return hasEnd ? $() : _this();
	}

	/**
	 * Starts an element, its attributes and an optional value. If the last argument
	 * is <code>$</code>, will call {@link #$(Object, Object...)} w/o parameters,
	 * just after starting the element (auto-end).
	 * 
	 * @param elementName The name of the element or a component {@link Class}
	 * @param params      attribute [name, value] pairs, optionally followed by a
	 *                    single value.If the last argument is <code>$</code>, will
	 *                    call {@link #$(Object, Object...)} w/o parameters, just
	 *                    after starting the element (auto-end). If the length of
	 *                    the array is odd, the last element designates the value
	 *                    for the element. May be empty. An attribute name may be
	 *                    namespace-prefixed.
	 */
	public T $(Object elementName, Object... params) {
		return _$(elementName, params);
	}

	@SuppressWarnings("unchecked")
	protected <P> ParamsHandler<P> getParamsHandler() {
		return (ParamsHandler<P>) NULL_HANDLER;
	}

	protected T _$(Object elementName, Object... params) {
		Object[] flatParams = flatten(params);
		ParamsHandler<Object> paramsHandler = getParamsHandler();
		ParamInfo<?> paramInfo = paramsHandler.init(elementName, flatParams);
		String name = paramInfo == null ? elementName.toString() : paramInfo.elementName;
		Object[] theParams = paramInfo == null ? flatParams : paramInfo.params;

		int nParams = theParams.length;
		boolean endElement = nParams > 0 && theParams[nParams - 1] == $;

		_startElementHead(name);

		String elementValue = null;

		boolean voidElement = isVoidElement(name);

		if (nParams > 0) {
			if (endElement) {
				if (voidElement) {
					throw new HyperMlException("void elements must not be ended: %s", name);
				}
				nParams--;
			}

			boolean paramsOdd = (nParams % 2) > 0;

			if (paramsOdd) {
				// last
				elementValue = toString(theParams[nParams - 1]);
			}

			if (nParams > 1) {
				if (paramsOdd) {
					nParams--;
				}
				for (int i = 0; i < nParams; i += 2) {
					Object attrValue = theParams[i + 1];
					if (attrValue != null) {
						String attrName = attrName(theParams[i]);
						if (paramInfo == null || !paramsHandler.applyAttribute(paramInfo.obj, attrName, attrValue)) {
							_attribute(attrName, attrValue);
						}
					}
				}
			}
		}

		if (paramInfo != null) {
			paramsHandler.endElementHead(paramInfo.obj);
		}

		_endElementHead();

		if (paramInfo != null) {
			paramsHandler.start(paramInfo.obj);
		}

		if (!voidElement) {

			if (elementValue != null) {
				text(elementValue);
			}

			stack.add(name);

			if (paramInfo != null) {
				stack.add(paramInfo);
			}
		}

		if (endElement) {
			$();
		}

		return _this();
	}

	private String attrName(Object name) {
		if (name == null) {
			throw new HyperMlException("attribute name must not be null");
		} else if (name.toString()
				.isEmpty()) {
			throw new HyperMlException("attribute name must not be empty");
		}

		return name.toString();
	}

	/**
	 * Ends the last written element.
	 */
	public T $() {
		if (stack.isEmpty()) {
			throw new HyperMlException("Too many calls to $()");
		}
		Object name = stack.removeLast();
		if (name instanceof ParamInfo) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ParamInfo<Object> paramInfo = (ParamInfo) name;
			paramInfo.handler.end(paramInfo.obj);
			$(); // end host element
		} else {
			_endElement(name.toString());
		}

		if (stack.isEmpty()) {
			try {
				writer.flush();
			} catch (Exception e) {
				throw HyperMlException.wrap(e);
			}
		}

		return _this();
	}

	@SuppressWarnings("unchecked")
	private T _this() {
		return (T) this;
	}

	protected void _startElementHead(String name) {
		_write("<");
		_write(name);
	}

	protected void _endElementHead() {
		_write(">");
	}

	protected void _attribute(String name, Object valueObj) {
		if (!writeAttribute(valueObj)) {
			return;
		}
		String value = valueObj.toString();
		if (value.isEmpty()) {
			return;
		}
		_write(" ");
		_write(name);
		if (writeAttributeValue(valueObj)) {
			_write("=\"");
			_write(escapeHtmlXml(value));
			_write("\"");
		}
	}

	protected void _endElement(String name) {
		_write("</");
		_write(name);
		_write(">");
	}

	protected void _text(String text, boolean escape) {
		if (text.isEmpty()) {
			return;
		}
		_write(escape ? escapeHtmlXml(text) : text);
	}

	protected void _write(String s) {
		try {
			written = true;
			writer.write(s);
		} catch (Exception e) {
			throw HyperMlException.wrap(e);
		}
	}
}
