package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

public class NoopXmlFriendlyReplacer extends XmlFriendlyReplacer {
	public NoopXmlFriendlyReplacer() {
		this(null, null);
	}

	public NoopXmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement) {
		super(dollarReplacement, underscoreReplacement);
	}

	@Override
	public String escapeName(String name) {
		return name.trim();
	}

	@Override
	public String unescapeName(String name) {
		return name.trim();
	}
}
