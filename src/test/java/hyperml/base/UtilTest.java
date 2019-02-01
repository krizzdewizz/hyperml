package hyperml.base;

import static hyperml.base.Util.flatten;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;

import hyperml.base.Pair;

public class UtilTest {

	@Test
	public void simple() throws Exception {
		assertThat(flatten("a", 1)).isEqualTo(new Object[] { "a", 1 });
	}

	@Test
	public void itemIsArray() throws Exception {
		assertThat(flatten("a", new int[] { 1 })).isEqualTo(new Object[] { "a", 1 });
	}

	@Test
	public void itemIsIterable() throws Exception {
		assertThat(flatten("a", asList(1))).isEqualTo(new Object[] { "a", 1 });
	}

	@Test
	public void itemIsMap() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("color", "red");
		assertThat(flatten("a", map)).isEqualTo(new Object[] { "a", "color", "red" });
	}

	@Test
	public void itemIsEntry() throws Exception {
		assertThat(flatten("a", Pair.of("color", "red"), Pair.of(44, "none"))).isEqualTo(new Object[] { "a", "color", "red", 44, "none" });
	}

	@Test
	public void itemIsStream() throws Exception {
		assertThat(flatten("a", Stream.of(1, 2, 3))).isEqualTo(new Object[] { "a", 1, 2, 3 });
	}

	@Test
	public void deeplyNested() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("0", false);
		map.put("1", true);
		assertThat(flatten("a", Stream.of(1, asList(4, Pair.of("color", "red")), map), "b")).isEqualTo(new Object[] { "a", 1, 4, "color", "red", "0", false, "1", true, "b" });
	}
}
