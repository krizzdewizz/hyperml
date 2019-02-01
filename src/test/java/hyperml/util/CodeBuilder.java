package hyperml.util;

public class CodeBuilder {
	private int depth;
	private final StringBuilder printer;

	public CodeBuilder() {
		this.printer = new StringBuilder();
	}

	public CodeBuilder create() {
		doCreate();
		return this;
	}

	protected void doCreate() {
		// no action
	}

	public CodeBuilder $$(Object... all) {
		for (Object s : all) {
			doPrint(s);
		}
		return this;
	}

	protected void doPrint(Object object) {
		getPrinter().append(String.valueOf(object));
	}

	public CodeBuilder $(Object... strings) {

		String last = strings.length > 0 ? strings[strings.length - 1].toString()
				.trim() : "";
		if (last.endsWith("}") || last.startsWith("}")) {
			depth--;
		}

		addIndent();

		$$(strings);

		doPrint("\n");

		if (last.endsWith("{")) {
			depth++;
		}

		return this;
	}

	protected void addIndent() {
		for (int i = 0; i < depth; i++) {
			doPrint("  ");
		}
	}

	@Override
	public String toString() {
		return getPrinter().toString();
	}

	protected int getDepth() {
		return depth;
	}

	protected StringBuilder getPrinter() {
		return printer;
	}
}