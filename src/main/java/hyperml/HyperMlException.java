package hyperml;

import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;

/**
 * Exception thrown from within hyperml.
 * 
 * @author krizz
 */
public class HyperMlException extends RuntimeException {

	public static Throwable realException(Throwable t) {
		if (t instanceof InvocationTargetException) {
			t = ((InvocationTargetException) t).getTargetException();
		}
		return t;
	}

	public static RuntimeException wrap(Throwable t) {
		t = realException(t);
		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		}

		return new HyperMlException(t);
	}

	private static final long serialVersionUID = 1L;

	public HyperMlException(String message, Object... args) {
		super(format(message, args));
	}

	public HyperMlException(Throwable cause, String message, Object... args) {
		super(format(message, args), realException(cause));
	}

	protected HyperMlException(Throwable cause) {
		super(realException(cause));
	}

}
