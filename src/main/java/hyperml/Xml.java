package hyperml;

import java.io.OutputStream;
import java.io.Writer;

import hyperml.base.XmlBase;

/**
 * XML elements/attributes.
 * <p>
 * Example:
 * 
 * <pre>
 * new Xml() {
 * 	protected void create() {
 * 		$(&quot;xml&quot;);
 * 		{
 * 			$(&quot;content&quot;, $);
 * 		}
 * 		$();
 * 	}
 * }.toString()
 * 
 * &lt;xml&gt;&lt;content&gt;&lt;/content&gt;&lt;/xml&gt;
 * 
 * </pre>
 * 
 * Or fluently:
 * 
 * <pre>
 * import static hyperml.base.BaseMl.$;
 * 
 * new Xml(System.out)
	.$(&quot;xml&quot;)
		.$(&quot;content&quot;, $)
	.$();
 * </pre>
 * 
 * @author krizzdewizz
 */
public class Xml extends XmlBase<Xml> {

	public static Xml of() {
		return new Xml();
	}

	public static Xml to(Writer writer) {
		return new Xml(writer);
	}

	public static Xml to(OutputStream out) {
		return new Xml(out);
	}

	public Xml() {
	}

	public Xml(Writer writer) {
		super(writer);
	}

	public Xml(OutputStream out) {
		super(out);
	}
}