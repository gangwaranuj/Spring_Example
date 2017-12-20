package com.workmarket.service.search.user;

import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.reporting.util.CSVSearchWriter;
import com.workmarket.search.response.user.PeopleSearchResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

@Component
public class CSVSearchExecutor {

	private static final Log logger = LogFactory.getLog(CSVSearchExecutor.class);

	protected CSVSearchWriter writer;
	protected PeopleSearchResponse response;

	public CSVSearchExecutor() {}

	public CSVSearchExecutor(CSVSearchWriter writer, PeopleSearchResponse response) {
		this.writer = writer;
		this.response = response;
	}

	public PeopleSearchResponse search() throws Exception {
		openAndWrite();
		try {
			writer.close();
		} catch (IOException e) {
			logger.error(String.format("file save failed for report %s", writer.getFilename()));
		}
		return response;
	}

	protected void openAndWrite() throws IOException, ReportingFormatException {
		Assert.notNull(writer);
		Assert.notNull(response);
		writer.open();

		for (String[] row : response.generateResultList()) {
			writer.addRow(row);
		}
	}
}
