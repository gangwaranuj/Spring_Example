package com.workmarket.api.v1.xstream;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class StreamDriver extends JDomDriver {
	public StreamDriver(XmlFriendlyReplacer replacer) {
		super(replacer);
	}

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		return new CompactWriter(out, CompactWriter.XML_1_0, xmlFriendlyReplacer());
	}

	@Override
	public HierarchicalStreamWriter createWriter(OutputStream out) {
		return new CompactWriter(new OutputStreamWriter(out), CompactWriter.XML_1_0, xmlFriendlyReplacer());
	}
}
