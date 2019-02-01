package hyperml;

import static hyperml.base.BaseMl.$;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author krizzdewizz
 */
public class XmlNullEmptyTest extends AbstractXmlTest {

	@Test
	public void testNullEmptyText() throws Exception {
		myAssertXMLEqual("<xml></xml>", Xml.of()
				.$("xml", "", $)
				.toString());

		myAssertXMLEqual("<xml></xml>", Xml.of()
				.$("xml", null, $)
				.toString());
	}

	@Test
	public void testNullEmptyText2() throws Exception {
		myAssertXMLEqual("<xml>a</xml>", Xml.of()
				.$("xml")
				.text("a", null, "", $)
				.toString());
	}

	@Test
	public void testNullEmptyAttributeValue() throws Exception {
		myAssertXMLEqual("<xml a='1'></xml>", Xml.of()
				.$("xml", "a", 1, "b", "", "c", null, $)
				.toString());
	}

	@Test
	public void testNullEmptyAttributeValue2() throws Exception {

		Map<String, String> attrs = new HashMap<>();
		attrs.put("a", "1");
		attrs.put("b", "");
		attrs.put("c", null);
		myAssertXMLEqual("<xml a='1'></xml>", Xml.of()
				.$("xml", attrs, $)
				.toString());
	}
}
