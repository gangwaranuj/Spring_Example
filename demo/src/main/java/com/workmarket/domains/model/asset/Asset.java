package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "asset")
@Table(name = "asset")
@NamedQueries({
		@NamedQuery(
				name = "asset.findByUUID",
				query = "from asset where uuid = :uuid"),
		@NamedQuery(
				name="asset.findByIdAndCompany",
				query="from asset where id = :assetId and company_id = :companyId"
		)
})
@AuditChanges
public class Asset extends ActiveDeletableEntity implements Comparable<Asset>, AssetResource {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private String uuid;
	private String mimeType;
	private boolean displayable = false;
	private AvailabilityType availability = new AvailabilityType(AvailabilityType.ALL);

	private AssetCdnUri assetCdnUri;
	private AssetRemoteUri assetRemoteUri;
	private String cdnUri;
	private String remoteUri;
	private String localUri;
	private Integer fileByteSize;
	private Integer order;

	private String content;

	public Asset() {}

	public Asset(String name, String description, String uuid, String mimeType) {
		this.name = name;
		this.description = description;
		this.uuid = uuid;
		this.mimeType = mimeType;
	}


	/**
	 * If an asset needs to be ordered in a custom way
	 *
	 * @return
	 */
	@Column(name = "order_number", nullable = true)
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * Typically the filename (including file extension) of the original document.
	 *
	 * @return
	 */
	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = true, length = 200)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Unique ID of the asset, should be unique across all assets. We are using UUIDs as file names for all the assets.
	 *
	 * @return uuid
	 */
	@Column(name = "uuid", nullable = false, length = 36)
	public String getUUID() {
		return uuid;
	}

	public void setUUID(String UUID) {
		this.uuid = UUID;
	}

	/**
	 * Mime type of the asset e.g.: plain/text All assets must have a mime type; otherwise browsers will not handle file correctly
	 *
	 * @return
	 */
	@Column(name = "mime_type", nullable = false, length = 150)
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Column(name = "displayable", nullable = false)
	public boolean isDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "availability_type_code", referencedColumnName = "code", nullable = false)
	public AvailabilityType getAvailability() {
		return availability;
	}

	public void setAvailability(AvailabilityType availability) {
		this.availability = availability;
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

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "asset_cdn_uri_id", referencedColumnName = "id", nullable = false, columnDefinition = "int default 1")
	public AssetCdnUri getAssetCdnUri() {
		return assetCdnUri;
	}

	public void setAssetCdnUri(AssetCdnUri assetCdnUri) {
		this.assetCdnUri = assetCdnUri;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "asset_remote_uri_id", referencedColumnName = "id", nullable = false, columnDefinition = "int default 1")
	public AssetRemoteUri getAssetRemoteUri() {
		return assetRemoteUri;
	}

	public void setAssetRemoteUri(AssetRemoteUri assetRemoteUri) {
		this.assetRemoteUri = assetRemoteUri;
	}


	@Transient
	public String getCdnUri() {
		if (assetCdnUri == null) {
			return null;
		}

		return FileUtilities.createRemoteFileandDirectoryStructor(assetCdnUri.getCdnUriPrefix(), uuid);
	}

	@Transient
	public String getRemoteUri() {
		if (assetRemoteUri == null) {
			return null;
		}

		return FileUtilities.createRemoteFileandDirectoryStructor(assetRemoteUri.getAssetRemoteUri(), uuid);
	}

	public void setCdnUri(String cdnUri) {
		this.cdnUri = cdnUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	@Transient
	public String getUri() {
		// Bypass the need for initializing the availability type lookup entity
		return availability.getUri(uuid, getCdnUri(), getRemoteUri(), localUri);
	}

	@Transient
	public String getRelativeUri() {
		return StringUtilities.stripUriProtocol(getUri());
	}

	@Transient
	public String getByteCountToDisplaySize() {
		if (fileByteSize != null) {
			return FileUtils.byteCountToDisplaySize(fileByteSize);
		}
		return StringUtils.EMPTY;
	}

	@Transient
	public String getDownloadableUri(){
		return availability.getDownloadableUri(uuid, getCdnUri(), getRemoteUri(), localUri);
	}

	@Transient
	public boolean isImage() {
		return MimeTypeUtilities.isImage(mimeType);
	}

	@Transient
	public boolean isMedia() {
		return MimeTypeUtilities.isMedia(mimeType);
	}

	static public Asset newInstance(Upload upload) {
		Asset instance = new Asset();
		instance.setUUID(upload.getUUID());
		instance.setName(upload.getFilename());
		instance.setMimeType(upload.getMimeType());
		instance.setFileByteSize(upload.getFileByteSize());
		return instance;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String toString() {
		return "Asset [id=" + getId() + ", name=" + name + ", uuid=" + uuid + "]";
	}

	@Override
	@Transient
	public String getAssetResourceType() {
		return AssetResource.ASSET;
	}

	@Override
	public int compareTo(Asset o) {
		if (o == null) {
			return 1;
		}
		if (this.getOrder() != null) {
			return this.getOrder().compareTo(o.getOrder());
		}
		return -1;
	}
}
