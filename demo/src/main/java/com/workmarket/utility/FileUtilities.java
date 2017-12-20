package com.workmarket.utility;

import com.workmarket.configuration.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FileUtilities {

	private static final String TEMP_FILE = "work_attachment_";

	public static void createFolder(String path) {
		File directory = new File(path);

		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static String generateTemporaryFileName() {
		return Constants.TEMPORARY_FILE_DIRECTORY + UUID.randomUUID().toString();
	}

	public static String generateTemporaryPNGFileName() {
		return generateTemporaryFileName() + ".png";
	}

	public static void deleteFile(String filename) {
		File f = new File(filename);
		if (f.exists())
			f.delete();
	}

	public static boolean fileExists(String subjectTemplatePath) {
		Assert.hasText(subjectTemplatePath);
		return (new File(subjectTemplatePath).exists());
	}

	public static String createRemoteFileandDirectoryStructor(String fileName){
		Assert.notNull(fileName, "fileName object can't be null");
		String normalized = fileName.replaceAll("[^a-zA-Z0-9]", "");
		StringBuilder directory = new StringBuilder();
		directory.append(normalized.substring(0, 2));
		directory.append("/" + normalized.substring(2, 4));
		directory.append("/" + normalized.substring(4,6));
		directory.append("/" + normalized.substring(6,8));
		directory.append("/" + normalized.substring(8,10) + "/");
		directory.append(fileName);
		return directory.toString();
	}

	public static String createRemoteFileandDirectoryStructor(String baseUrl, String filename) {
		return baseUrl + createRemoteFileandDirectoryStructor(filename);
	}

	public static File storeFile(String sourceFilePath, String name, String mimeType) throws IOException {
		String fileName = generateTemporaryFileName() + name;
		File source = new File(sourceFilePath);
		File target = new File(fileName);
		FileUtils.copyFile(source, target);
		return target;
	}

	public static File temporaryStoreFile(InputStream inputStream) throws IOException {
		Assert.notNull(inputStream);
		String filename = Constants.TEMPORARY_FILE_DIRECTORY + TEMP_FILE +  UUID.randomUUID().toString() + ".dat";
		File file = new File(filename);
		if (file.exists()){
			file.delete();
		}
		IOUtils.copy(inputStream, new FileOutputStream(file));
		return file;
	}
}
