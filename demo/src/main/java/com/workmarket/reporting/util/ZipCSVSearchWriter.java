package com.workmarket.reporting.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.configuration.Constants;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: alexsilva Date: 7/25/14 Time: 2:07 PM
 */
public class ZipCSVSearchWriter extends CSVSearchWriter {

	private static final long serialVersionUID = 73857323290936038L;

	private ZipOutputStream zipFile;
	private String csvFilename;

	public ZipCSVSearchWriter(String filename, String directory) {
		super(filename + Constants.ZIP_EXTENSION, directory);
		this.csvFilename = filename + Constants.CSV_EXTENSION;
	}

	public void open() throws IOException, ReportingFormatException {
		FileOutputStream dest = new FileOutputStream(getAbsolutePath());
		this.zipFile = new ZipOutputStream(new BufferedOutputStream(dest));

		ZipEntry zipEntry = new ZipEntry(csvFilename);
		zipFile.putNextEntry(zipEntry);

		setFileWriter(new CSVWriter(new OutputStreamWriter(zipFile)));

		// Write header row
		writeRowToCSV(fields);
	}

	public void closeZipFile() throws IOException {
		zipFile.closeEntry();
		zipFile.close();
	}

}
