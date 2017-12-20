package com.workmarket.data.export.adapter;

import au.com.bytecode.opencsv.CSVWriter;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CSVAdapter {
	private char delimiter = CSVWriter.DEFAULT_SEPARATOR;
	private char quoteChar = CSVWriter.DEFAULT_QUOTE_CHARACTER;

	public CSVAdapter() {
	}

	public CSVAdapter(char delimiter) {
		this.delimiter = delimiter;
	}

	public CSVAdapter(char delimiter, char quoteChar) {
		this.delimiter = delimiter;
		this.quoteChar = quoteChar;
	}

	public void export(Writer writer, List<String[]> rows) throws IOException {
		if (CollectionUtils.isEmpty(rows)) {
			return;
		}
		CSVWriter csv = new CSVWriter(writer, delimiter, quoteChar, "\r\n");
		for (String[] row : rows) {
			csv.writeNext(row);
		}
		csv.close();
	}

	public void export(Writer writer, ArrayList<Map<String, Object>> map) throws IOException {
		CSVWriter csv = new CSVWriter(writer, delimiter, quoteChar, "\r\n");
		for (Map<String, Object> row : map) {
			Collection<Object> values = row.values();
			String[] s = values.toArray(new String[values.size()]);
			csv.writeNext(s);
		}
		csv.close();
	}

	public void exportWithoutClose(Writer writer, List<String[]> rows) throws IOException {
		CSVWriter csv = new CSVWriter(writer, delimiter, quoteChar, "\r\n");
		for (String[] row : rows) {
			csv.writeNext(row);
		}
	}

	public CSVWriter exportWithoutCloseAndUnquoted(Writer writer, final List<String[]> rows) {
		CSVWriter csv = new CSVWriter(writer, delimiter, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
		for (String[] row : rows) {
			csv.writeNext(row);
		}
		return csv;
	}
}
