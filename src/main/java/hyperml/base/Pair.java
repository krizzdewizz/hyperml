package hyperml.base;

import static java.lang.String.format;

import java.util.Map;
import java.util.Objects;

/**
 * A pair of values.
 * <p>
 * Honours equals/hashCode.
 * 
 * @author krizz
 * @param <K> key/first
 * @param <V> value/second
 */
public final class Pair<K, V> implements Map.Entry<K, V> {
	/**
	 * Returns a new pair.
	 * 
	 * @param key   Key/first
	 * @param value Value/second
	 * @return Pair
	 */
	public static <K, V> Pair<K, V> of(K key, V value) {
		return new Pair<K, V>(key, value);
	}

	private final K key;
	private final V value;

	private Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return format("Pair [key=%s, value=%s]", key, value);
	}
}