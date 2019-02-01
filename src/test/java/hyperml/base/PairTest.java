package hyperml.base;

import static hyperml.base.Pair.of;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PairTest {
	@Test
	public void testEquals() throws Exception {
		assertThat(of("a", "b")).isEqualTo(of("a", "b"));
		assertThat(of("a", "b")).isNotEqualTo(of("a", "c"));
		assertThat(of("a", "b")).isNotEqualTo(of("x", "b"));
	}
	
	@Test
	public void testToString() throws Exception {
		assertThat(of("a", "b").toString()).isEqualTo("Pair [key=a, value=b]");
	}
}
