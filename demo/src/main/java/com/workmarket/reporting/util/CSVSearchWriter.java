package com.workmarket.reporting.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * User: workmarketuser
 * Date: 8/21/13
 */
public class CSVSearchWriter extends CSVBaseWriter implements Serializable {

	private static final long serialVersionUID = -1026529845567354464L;

	private static final Log logger = LogFactory.getLog(CSVSearchWriter.class);
	protected static final List<String> fields;

	static {
		fields = Lists.newArrayList("User Number", "First Name", "Last Name", "Email", "Role", "Work Phone", "Mobile Phone",
				"User Created", "Address", "Address 2", "City", "State", "Postal Code", "Latitude", "Longitude", "Last Login",
				"Background Check", "Drug Test", "Shared worker", "Status", "Confirmed Email", "Company Id", "Company Name",
				"Company Status", "Bank Account", "Tin Verified", "Resource active assignments", "Resource completed assignments",
				"Cbsa Name");
	}

	public CSVSearchWriter(String filename, String directory) {
		super(directory, filename);
	}

	public void open() throws IOException, ReportingFormatException {
		setFileWriter(new CSVWriter(new FileWriter(new File(getAbsolutePath()))));

		// Write header row
		writeRowToCSV(fields);
	}

	public void addRow(String[] row) {
		Assert.notNull(row);
		try {
			writeRowToCSV(row);
		} catch (Exception ex) {
			logger.error("[export to csv] error processing row " + Arrays.toString(row), ex);
		}
	}

}