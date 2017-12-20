package com.workmarket.domains.model;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ZipAssetBundle;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.StringUtilities;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: micah
 * Date: 1/5/14
 * Time: 8:06 PM
 */
public class DocumentationManager {
	private static final int BUFFER = 2048;

	private ZipOutputStream zipOutputStream;
	private String tmpFile;

	private String topLevelFolderName;
	private List<String> resourcesIdentifiers = Lists.newArrayList();
	private List<String> documentationTypes = Lists.newArrayList();
	private int files = 0;

	// really only for tests. is there a better way?
	public void setZipOutputStream(ZipOutputStream zipOutputStream) {
		this.zipOutputStream = zipOutputStream;
	}

	public void start(String groupName) throws IOException {
		tmpFile = FileUtilities.generateTemporaryFileName();
		FileOutputStream dest = new FileOutputStream(tmpFile);
		this.zipOutputStream = new ZipOutputStream(new BufferedOutputStream(dest));
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		this.topLevelFolderName = ((groupName != null) ? groupName : "unknown") + "_" + sdf.format(new Date()) + "/";

		zipOutputStream.putNextEntry(new ZipEntry(this.topLevelFolderName));
	}

	public void putNextAsset(String type, Asset asset, String firstName, String lastName, String userNumber, InputStream stream) throws IOException {
		byte data[] = new byte[BUFFER];

		String path = topLevelFolderName;

		String identifier = StringUtilities.fullName(firstName, lastName)  + "_" + userNumber + "/";
		path += identifier;

		if (!resourcesIdentifiers.contains(identifier)) {
			resourcesIdentifiers.add(identifier);
			zipOutputStream.putNextEntry(new ZipEntry(path));
		}

		path += type + "/";

		if (!documentationTypes.contains(path)) {
			documentationTypes.add(path);
			zipOutputStream.putNextEntry(new ZipEntry(path));
		}

		// using last part of uuid to ensure unique file name
		String[] uuidParts = asset.getUUID().split("-");
		String uniquePrefix = uuidParts[uuidParts.length - 1];
		String filename = path + uniquePrefix + "_" + asset.getName();
		zipOutputStream.putNextEntry(new ZipEntry(filename));

		int count;
		while ((count = stream.read(data, 0, BUFFER)) != -1) {
			zipOutputStream.write(data, 0, count);
		}

		files++;
	}

	public ZipAssetBundle finish() throws IOException {
		zipOutputStream.close();

		ZipAssetBundle zipAssetBundle = new ZipAssetBundle();
		zipAssetBundle.setFileName(tmpFile);
		zipAssetBundle.setZipOutputStream(zipOutputStream);
		zipAssetBundle.setFileSize(files);

		return zipAssetBundle;
	}
}
