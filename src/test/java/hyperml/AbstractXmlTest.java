package hyperml;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import junit.framework.TestCase;

/**
 * @author krizzdewizz
 */
abstract public class AbstractXmlTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	public static void myAssertXMLEqual(String expected, String actual) throws Exception {
		boolean oldIgnoreWhitespace = XMLUnit.getIgnoreWhitespace();
		try {
			XMLUnit.setIgnoreWhitespace(true);

			Diff myDiff = new Diff(expected, actual);
			if (!myDiff.similar()) {
				TestCase.assertEquals(myDiff.toString(), expected, actual);
			}
		} finally {
			XMLUnit.setIgnoreAttributeOrder(oldIgnoreWhitespace);
		}
	}

}
