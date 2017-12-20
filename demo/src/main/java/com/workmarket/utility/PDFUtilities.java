package com.workmarket.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PDFUtilities {
	private static final Log logger = LogFactory.getLog(PDFUtilities.class);

	public static void createFromHtml(String html, OutputStream os) throws Exception {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(os);
		os.close();
	}

	public static void createFromHtml(String html, String pathToFile) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(pathToFile);
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(os);
			os.close();
		} catch (final Exception e) {
			logger.error("[pdf] Error creating file");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException e) {
					logger.error("[pdf] Error creating file");
				}
			}
		}
	}

}
