package hyperml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author krizzdewizz
 */
public class RawTest extends AbstractXmlTest {
	
	@Test
	public void raw() throws Exception {
		assertThat(Xml.of()
				.$("x")
				.raw("<>")
				.$()
				.toString()).isEqualTo("<x><></x>");
	}

	@Test
	public void escape() throws Exception {
		assertThat(Xml.of()
				.$("x")
				.text("<>")
				.$()
				.toString()).isEqualTo("<x>&lt;&gt;</x>");
	}
}
