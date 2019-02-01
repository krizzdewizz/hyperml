package hyperml;

import static hyperml.base.BaseMl.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.junit.Test;

/**
 * @author krizzdewizz
 */
public class Xml2Test extends AbstractXmlTest {

	@Test
	public void innerClass() throws Exception {
		Xml xml = new Xml() {
			@Override
			protected void create() {
				$("xml");
				{
					$("content", $);
				}
				$();
			}
		};
		assertThat(xml.toString()).isEqualTo("<xml><content></content></xml>");
		assertThat(xml.toString()).isEqualTo("<xml><content></content></xml>");
	}

	@Test
	public void fluentEmpty() throws Exception {
		Xml xml = Xml.of()
				.$("xml", $);

		StringWriter sw = new StringWriter();
		xml.build(sw);
		// does nothing because create() not overridden
		assertThat(sw.toString()).isEqualTo("");
	}

	@Test
	public void fluent() throws Exception {
		Xml xml = Xml.of()
				.$("xml")
				.$("content", $)
				.$();
		assertThat(xml.toString()).isEqualTo("<xml><content></content></xml>");
		assertThat(xml.toString()).isEqualTo("<xml><content></content></xml>");
	}

	@Test
	public void fluentWriter() throws Exception {
		StringWriter sw = new StringWriter();
		Xml.of(sw)
				.$("xml")
				.$("content", $)
				.$();
		assertThat(sw.toString()).isEqualTo("<xml><content></content></xml>");
	}

	@Test
	public void fluentOutputStream() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Xml.of(out)
				.$("xml")
				.$("content", "֍", $)
				.$();
		assertThat(out.toByteArray()).isEqualTo("<xml><content>֍</content></xml>".getBytes());
	}
}
