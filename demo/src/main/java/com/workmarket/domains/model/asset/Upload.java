package com.workmarket.domains.model.asset;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Upload entity that represents any uploads via upload service
 */
@Entity(name = "upload")
@Table(name = "upload")
@AuditChanges
public class Upload extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	@Size(min = Constants.MIN_FILENAME, max = Constants.MAX_FILENAME)
	private String filename;
	@Size(min = Constants.MIN_PATH, max = Constants.MAX_PATH)
	private String sourcePath;
	@NotNull
	private String UUID;
	@NotNull
	private String mimeType;
	private String filePath;

	@Deprecated private String eTag;

	private String cdnUri;
	private String remoteUri;
	private String localUri;
	private Integer fileByteSize;

	public Upload() {}

	/**
	 * File name that was part of the upload metadata
	 *
	 * @return file name
	 */
	@Column(name = "filename", nullable = false, length = Constants.MAX_FILENAME)
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Source file path from where the upload file was copied from
	 *
	 * @return source file path
	 */
	@Column(name = "source_path", nullable = false, length = Constants.MAX_FILENAME)
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * Mime type of the asses eg. "plain/text" All assets must have mime type because otherwise browsers will not handle file correctly
	 *
	 * @return
	 */

	@Column(name = "mime_type", nullable = false, length = 50)
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * This is unique id of the asset it is unique across all assets We are using uuids as file names for all the assets
	 *
	 * @return uuid
	 */
	@Column(name = "uuid", nullable = false, length = 36)
	public String getUUID() {
		return UUID;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	@Column(name = "cdn_uri", nullable = true, length = 255)
	public String getCdnUri() {
		return cdnUri;
	}

	public void setCdnUri(String cdnUri) {
		this.cdnUri = cdnUri;
	}

	@Column(name = "remote_uri", nullable = true, length = 255)
	public String getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	@Column(name = "local_uri", nullable = true, length = 255)
	public String getLocalUri() {
		return localUri;
	}

	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}

	@Column(name = "file_byte_size", nullable = true)
	public Integer getFileByteSize() {
		return fileByteSize;
	}

	public void setFileByteSize(Integer fileByteSize) {
		this.fileByteSize = fileByteSize;
	}

	/**
	 * File path to where the upload is actually stored
	 *
	 * @return file path
	 */
	@Transient
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Transient
	public String getETag() {
		return eTag;
	}

	public void setETag(final String eTag) {
		this.eTag = eTag;
	}
}
