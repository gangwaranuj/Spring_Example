package com.workmarket.reporting.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.workmarket.reporting.exception.ReportingFormatException;

import java.io.IOException;
import java.util.List;

/**
 * User: KhalidRich
 * Date: 8/21/13
 * Time: 12:56 PM
 */
public abstract class CSVBaseWriter {
	private String directory;
	private String filename;
	private CSVWriter fileWriter;
	private int rowCount = 0;

	protected CSVBaseWriter() {}

	protected CSVBaseWriter(String directory, String filename) {
		this.directory = directory;
		this.filename = filename;
	}

	protected abstract void open() throws IOException, ReportingFormatException;

	public void close() throws IOException {
		if (fileWriter != null) {
			fileWriter.close();
		}
	}

	protected void writeRowToCSV(List<String> fields) throws IOException {
		String[] fieldsArray = new String[fields.size()];
		fields.toArray(fieldsArray);
		writeRowToCSV(fieldsArray);
	}

	protected void writeRowToCSV(String[] fields) throws IOException {
		if (fileWriter != null) {
			fileWriter.writeNext(fields);
			fileWriter.flush();
			rowCount++;
		}
	}

	public String getAbsolutePath() {
		return directory + "/" + filename;
	}

	public String getFilename() {
		return filename;
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public String getDirectory() {
		return directory;
	}

	public CSVWriter getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(CSVWriter fileWriter) {
		this.fileWriter = fileWriter;
	}
}
