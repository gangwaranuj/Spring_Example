package com.workmarket.web.views;

import au.com.bytecode.opencsv.CSVWriter;
import com.workmarket.data.export.adapter.CSVAdapter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class CSVView extends AbstractView {

	public final static String CSV_MODEL_KEY = "csvData";
	public final static String CSV_SEARCH_KEY = "csvSearchResults";
	private String filename;
	private Character delimiter = CSVWriter.DEFAULT_SEPARATOR;
	private Character quoteChar = CSVWriter.DEFAULT_QUOTE_CHARACTER;

	public CSVView() {
		setContentType("text/csv");
	}

	public CSVView(String filename, Character delimiter) {
		this();
		this.filename = filename;
		this.delimiter = delimiter;
	}

	public CSVView(String filename, Character delimiter, Character quoteChar) {
		this(filename, delimiter);
		this.quoteChar = quoteChar;
	}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected void renderMergedOutputModel(Map<String,Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setContentType(getContentType());
		if (filename != null) {
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
		}

		@SuppressWarnings("unchecked")
		List<String[]> data = (List)model.get(CSV_MODEL_KEY);

		ByteArrayOutputStream baos = createTemporaryOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));

		CSVAdapter csv = new CSVAdapter(delimiter, quoteChar);
		csv.export(writer, data);

		writeToResponse(response, baos);
	}
}
