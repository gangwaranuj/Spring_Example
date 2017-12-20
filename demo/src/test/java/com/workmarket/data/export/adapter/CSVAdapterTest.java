package com.workmarket.data.export.adapter;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CSVAdapterTest {

	private Writer writer;
	private CSVAdapter adapter;

	@Before
	public void setUp() throws Exception {
		writer = new StringWriter();
		adapter = new CSVAdapter("#".charAt(0));
	}

	@Test
	public void exportWithoutCloseAndUnquoted_withSpecialDelimiter_success() throws Exception {
		List<String[]> rows = Lists.newArrayList();
		rows.add(new String[]{"some", StringUtils.EMPTY, "hello", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, "1900"});
		rows.add(new String[]{"more", "data", "8902"});
		adapter.exportWithoutCloseAndUnquoted(writer, rows);
		assertTrue(writer.toString().contains("some##hello######1900"));
		assertTrue(writer.toString().contains("more#data#8902"));
	}
}