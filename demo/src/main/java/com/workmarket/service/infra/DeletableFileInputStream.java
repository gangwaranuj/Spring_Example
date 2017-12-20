package com.workmarket.service.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeletableFileInputStream extends FileInputStream {

	private File file;

	public DeletableFileInputStream(String fileName) throws FileNotFoundException {
		this(new File(fileName));
	}

	public DeletableFileInputStream(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}

	public void close() throws IOException {
		try {
			super.close();
		} finally {
			if (file != null) {
				file.delete();
				file = null;
			}
		}
	}
}
