package com.workmarket.api.v1;

import org.apache.commons.lang.NotImplementedException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Captures the servlet output stream for debugging and tracing use.
 */
public class ApiTraceServletOutputStream extends ServletOutputStream {
	private ServletOutputStream servletOutputStream;
	private ByteArrayOutputStream byteStream;

	public ApiTraceServletOutputStream(ServletOutputStream servletOutputStream) {
		this.servletOutputStream = servletOutputStream;
		byteStream = new ByteArrayOutputStream(512);
	}

	@Override
	public void write(int b) throws IOException {
		byteStream.write(b);
		servletOutputStream.write(b);
	}

	@Override
	public boolean isReady() {
		throw new NotImplementedException("isReady not implemented");
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new NotImplementedException("setWriteListener not implemented");
	}

	public byte[] getBytes() {
		return byteStream.toByteArray();
	}
}
