package com.workmarket.xml;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CDATAAdapterCharacterEscape implements CharacterEscapeHandler {

	@Override
	public void escape(char[] chars, int i, int i2, boolean b, Writer writer) throws IOException {
		writer.write(chars, i, i2);
	}
}
