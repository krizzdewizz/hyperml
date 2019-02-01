package hyperml.base;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Clients may subclass to add custom behaviour.
 * 
 * @author krizzdewizz
 * @param <T> type of subclass
 */
public abstract class XmlBase<T extends XmlBase<?>> extends BaseMl<T> {
	public XmlBase() {
	}

	public XmlBase(Writer writer) {
		super(writer);
	}

	public XmlBase(OutputStream out) {
		super(out);
	}

	@Override
	protected boolean isVoidElement(String name) {
		return false;
	}

	@Override
	protected boolean escapeText() {
		return true;
	}
}