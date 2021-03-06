package hyperml;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.StringWriter;

import org.junit.Test;

import hyperml.base.BaseMl;

/**
 * @author krizzdewizz
 */
public class XmlTest extends AbstractXmlTest {

	@Test
	public void testSingleDocElem() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", $);
			}
		};

		myAssertXMLEqual("<xml></xml>", xml.toString());
	}

	@Test
	public void testSingleDocElemWithValue() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", "hello world!", $);
			}
		};

		myAssertXMLEqual("<xml>hello world!</xml>", xml.toString());
	}

	@Test
	public void testSmallestPossibleXml() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("a", $);
			}
		};

		myAssertXMLEqual("<a/>", xml.toString());
	}

	@Test
	public void testSingleDocElemWithSingleAttribute() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", "content-type", "text/css", $);
			}
		};

		myAssertXMLEqual("<xml content-type='text/css'></xml>", xml.toString());
	}

	@Test
	public void testSingleDocElemWithSingleAttributeAndValue() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", "content-type", "text/css", "the value", $);
			}
		};

		myAssertXMLEqual("<xml content-type='text/css'>the value</xml>", xml.toString());
	}

	@Test
	public void testSingleDocElemWithManyAttributes() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", "border", 1, "width", "100%", "enabled", true, $);
			}
		};

		myAssertXMLEqual("<xml border='1' width='100%' enabled='true'></xml>", xml.toString());
	}

	@Test
	public void testSingleDocElemWithManyAttributesAndValue() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml", "border", 1, "width", "100%", "enabled", true, "value is here", $);
			}
		};

		myAssertXMLEqual("<xml border='1' width='100%' enabled='true'>value is here</xml>", xml.toString());
	}

	@Test
	public void testNested() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml");
				{
					$("body");
					{
						$("p1", $);
						$("p2", $);
						$("h1", "border", true, $);
						$("b", "hello", $);
						$("code", "java.lang.String#valueOf(char)", $);
					}
					$();
				}
				$();
			}
		};

		myAssertXMLEqual("<xml><body><p1></p1><h1 border='true'></h1><b>hello</b><code>java.lang.String#valueOf(char)</code><p2></p2></body></xml>", xml.toString());
	}

	@Test
	public void testMore453() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("page");
				{
					$("body");
					{
						$("h1", "hello world", $);
					}
					$();
				}
				$();
			}
		};

		String result = xml.toString();
		myAssertXMLEqual("<page><body><h1>hello world</h1></body></page>", result);
	}

	@Test
	public void testMore453abcx() throws Exception {
		StringWriter out = new StringWriter();
		Xml x = new Xml(out);
		x.$("page");
		{
			x.$("body");
			{
				x.text("abc");
			}
			x.$();
		}
		x.$();

		String result = out.toString();
		myAssertXMLEqual("<page><body>abc</body></page>", result);
	}

	@Test
	public void testTextWithEnd() throws Exception {
		StringWriter out = new StringWriter();
		Xml x = new Xml(out);
		/* @formatter:off */
		x.$("page")
				.$("body")
				.text("abc", 1, BaseMl.$)
				.$();
		/* @formatter:on */

		String result = out.toString();
		myAssertXMLEqual("<page><body>abc1</body></page>", result);
	}

	@Test
	public void testTextOnlyEnd() throws Exception {
		StringWriter out = new StringWriter();
		Xml x = new Xml(out);
		/* @formatter:off */
		x.$("page")
				.$("body")
				.text(BaseMl.$)
				.$();
		/* @formatter:on */

		String result = out.toString();
		myAssertXMLEqual("<page><body></body></page>", result);
	}

	@Test
	public void testMore453abc() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("page");
				{
					$("body", "x", 1);
					{
						$("h1", "hello world with $", $);
						$("h2", "~h2", $);
					}
					$();
				}
				$();
			}
		};

		String result = xml.toString();
		myAssertXMLEqual("<page><body x='1'><h1>hello world with $</h1><h2>~h2</h2></body></page>", result);
	}

	@Test
	public void testAttrWithNullValue() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("content", "attr", null, $);
			}
		};

		String result = xml.toString();
		myAssertXMLEqual("<content/>", result);
	}

	@Test
	public void testAttrWithEmptyValue() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("content", "attr", "", $);
			}
		};

		String result = xml.toString();
		myAssertXMLEqual("<content/>", result);
	}

	@Test
	public void testWithNullText() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("content", null, $);
			}
		};

		String result = xml.toString();
		myAssertXMLEqual("<content/>", result);
	}

	@Test
	public void testFailTooManyEnds() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("content", null, $);
				$();
			}
		};

		expectedEx.expect(HyperMlException.class);
		expectedEx.expectMessage(containsString("Too many calls"));

		xml.toString();
	}

	@Test
	public void testFailTooFewEnds() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("content");
			}
		};

		expectedEx.expect(HyperMlException.class);
		expectedEx.expectMessage(containsString("Missing end element call"));

		xml.toString();
	}
}
