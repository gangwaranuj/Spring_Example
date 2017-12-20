package com.workmarket.api.v1;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Captures the servlet output stream for debugging and tracing use.
 */
public class ApiTraceServletResponseWrapper extends HttpServletResponseWrapper {
	private ApiTraceServletOutputStream streamTrace;
	private PrintWriter writer;

	public ApiTraceServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() has already been called for this response");
		}

		if (streamTrace == null) {
			streamTrace = new ApiTraceServletOutputStream(super.getOutputStream());
		}

		return streamTrace;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return writer;
		}

		if (streamTrace != null) {
			throw new IllegalStateException("getOutputStream() has already been called for this response");
		}

		streamTrace = new ApiTraceServletOutputStream(super.getOutputStream());
		writer = new PrintWriter(streamTrace);
		return writer;
	}

	public byte[] getWrittenBytes() {
		if (streamTrace != null) {
			return streamTrace.getBytes();
		}
		return new byte[0];
	}
}
