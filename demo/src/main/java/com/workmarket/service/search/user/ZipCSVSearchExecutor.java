package com.workmarket.service.search.user;

import com.workmarket.reporting.util.ZipCSVSearchWriter;
import com.workmarket.search.response.user.PeopleSearchResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ZipCSVSearchExecutor extends CSVSearchExecutor {

	private static final Log logger = LogFactory.getLog(ZipCSVSearchExecutor.class);

	public ZipCSVSearchExecutor() {}

	public ZipCSVSearchExecutor(ZipCSVSearchWriter writer, PeopleSearchResponse response) {
		super(writer, response);
	}

	public PeopleSearchResponse search() throws Exception {
		openAndWrite();
		try {
			((ZipCSVSearchWriter)writer).closeZipFile();
		} catch (IOException e) {
			logger.error(String.format("file save failed for zipped csv %s", writer.getFilename()));
		}
		return response;
	}
}
